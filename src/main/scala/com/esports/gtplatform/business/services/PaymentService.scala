package com.esports.gtplatform.business.services

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.esports.gtplatform.business.EventRepo
import com.stripe.exception._
import com.stripe.model.{Charge, Customer}
import models.PaymentType.PaymentType
import models._
import org.slf4j.LoggerFactory
import scala.collection.mutable
import collection.JavaConversions._

/**
 * Created by Matthew on 9/9/2014.
 */
abstract class PaymentService(event: Event)(implicit val bindingModule: BindingModule) extends Injectable {
  val logger = LoggerFactory.getLogger(getClass)
  val pType: PaymentType

  protected def checkForExistingUser(u: User): Option[String]

  def makePayment(u: User, info: mutable.Map[String, String]): (Boolean, String)
}

class StripePayment(event: Event)(implicit override val bindingModule: BindingModule) extends PaymentService(event) {
  val pType = PaymentType.Stripe
  val eventPayment = event.payments.find(x => x.payType == pType).getOrElse(throw new Exception("Event does not contain this payment type."))

   protected def checkForExistingUser(u: User): Option[String] = {
    val eventRepo = inject[EventRepo]
    val otherEvents = eventRepo.getAll.filter(x => x.payments.exists(y => y.secretKey.contains(eventPayment.secretKey)))
    if (otherEvents.isEmpty)
      None
    else {
      otherEvents.flatMap(x => x.users).find(y => y.user == u && y.receiptId.isDefined) match {
        case Some(ev: EventUser) =>
          ev.receiptId
        case None =>
          None
      }
    }
  }

  override def makePayment(u: User, info: mutable.Map[String, String]): (Boolean, String) = {
    try {
      val customerId: String = checkForExistingUser(u) match {
        case Some(id: String) =>
            id
        case None =>
          if (info.get("card").isEmpty)
          {
            logger.error("Card info was not passed for payment.")
            return (false,"Did not include card token in info")
          }
          else {
            info.put("email", u.email)
            info.put("description", "User " + u.id)
            Customer.create(info, eventPayment.secretKey.get).getId
          }
      }
      val charge = mutable.Map.empty[String, String]
      charge.put("amount", (eventPayment.amount*100).toInt.toString)
      charge.put("currency","usd")
      charge.put("description", "Registration for " + event.name)
      charge.put("customer", customerId)
      charge.put("receipt_email", u.email)
      Charge.create(charge, eventPayment.secretKey.get)
      (true, customerId)
    }
    catch {
      case ce: CardException  =>
        logger.error("Stripe had a card problem", ce)
        (false, ce.getMessage)
      case ir: InvalidRequestException =>
        logger.error("Stripe had a bad request", ir)
        (false, ir.getMessage)
      case auth: AuthenticationException =>
        logger.error("Stripe couldn't authenticate, probably API Key", auth)
        (false, auth.getMessage)
    }
  }
}
