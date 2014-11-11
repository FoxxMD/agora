package com.esports.gtplatform.business.services

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}

import com.esports.gtplatform.business.{EventUserRepo, EventRepo}
import com.googlecode.mapperdao.Persisted
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
class CustomerException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)

class PaymentException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)

abstract class PaymentService(event: Event)(implicit val bindingModule: BindingModule) extends Injectable {
    val logger = LoggerFactory.getLogger(getClass)
    val pType: PaymentType
    val eventUserRepo = inject[EventUserRepo]
    val eventRepo = inject[EventRepo]
    val logPrefix = "[Payment][Event](" + event.id + ") "

    def checkForExistingUser(u: User): Option[EventUser]

    def createCustomer(ev: EventUser with Persisted, info: mutable.Map[String, String]): EventUser with Persisted

    def makePayment(ev: EventUser with Persisted): EventUser
}

class StripePayment(event: Event)(implicit override val bindingModule: BindingModule) extends PaymentService(event) {
    val pType = PaymentType.Stripe
    val eventPayment = event.payments.find(x => x.payType == pType).getOrElse {
        logger.error("Event " + event.id + "does not contain payment option of type " + pType)
        throw new IllegalArgumentException("This event does not contain the payment type " + pType + ".")
    }

    def checkForExistingUser(u: User): Option[EventUser] = {
        logger.info(logPrefix + "Checking if User "+u.id+" has already paid")
        val otherEvents = eventRepo.getAll.filter(x => x.payments.exists(y => y.secretKey == eventPayment.secretKey))
        if (otherEvents.isEmpty)
        {
            None
        }
        else {
            otherEvents.flatMap(x => x.users).find(y => y.userId.id == u.id && (y.receiptId.isDefined || y.customerId.isDefined)) /*match {
        case Some(ev: EventUser) =>
          if(ev.event.id == event.id)
          {
            logger.error("User " + u.id + " has already paid for Event " + event.id)
            throw new IllegalArgumentException("You have already paid for this event!")
          }
          ev.receiptId
        case None =>
          None
      }*/
        }
    }

    def createCustomer(ev: EventUser with Persisted, info: mutable.Map[String, String]): EventUser with Persisted = {
        logger.info(logPrefix + "Attempting to create Stripe customer for User " + ev.userId.id)
        if (info.get("card").isEmpty) {
            logger.error(logPrefix + "Card info was not passed for payment.")
            throw new IllegalArgumentException("Card token was not passed from stripe.")
        }
        else {
            info.put("email", ev.userId.email)
            info.put("description", "User " + ev.userId.id)

            val customerId = try {
                Customer.create(info, eventPayment.secretKey.get).getId
            }
            catch {
                case ce: CardException =>
                    logger.error(logPrefix + "Stripe had a card problem", ce)
                    throw new CustomerException("Stripe had a card problem", ce)
                case ir: InvalidRequestException =>
                    logger.error(logPrefix + "Stripe had a bad request", ir)
                    throw new CustomerException("Stripe had a bad request", ir)
                case auth: AuthenticationException =>
                    logger.error(logPrefix + "Stripe couldn't authenticate, probably API Key", auth)
                    throw new CustomerException("Stripe couldn't authenticate, probably API Key", auth)
                case ill: IllegalArgumentException =>
                    throw new CustomerException("Illegal argument", ill)
            }

            logger.info("[Payment][Event](" + ev.eventId.id + ") Customer " + customerId + " created successfully.")

            try {
                eventUserRepo.update(ev)
            }
            catch {
                case e: Exception =>
                    logger.error(logPrefix + "Could not set customer Id for EventUser", e)
                    throw new Exception(customerId, e)
            }
        }
    }


    def makePayment(ev: EventUser with Persisted): EventUser = {
        logger.info(logPrefix + "Creating charge object...")
        val charge = mutable.Map.empty[String, String]
        charge.put("amount", (eventPayment.amount * 100).toInt.toString)
        charge.put("currency", "usd")
        charge.put("description", "Registration for " + event.name)
        charge.put("customer", ev.customerId.getOrElse(throw new IllegalArgumentException("EventUser does not have a customer Id, cannot make a charge.")))
        charge.put("receipt_email", ev.userId.email)
        val chargeId = try {
            logger.info(logPrefix + "Attempting to charge card...")
            Charge.create(charge, eventPayment.secretKey.get)
        }
        catch {
            case ce: CardException =>
                logger.error("Stripe had a card problem", ce)
                throw new PaymentException("Stripe had a card problem", ce)
            case ir: InvalidRequestException =>
                logger.error("Stripe had a bad request", ir)
                throw new PaymentException("Stripe had a bad request", ir)
            case auth: AuthenticationException =>
                logger.error("Stripe couldn't authenticate, probably API Key", auth)
                throw new PaymentException("Stripe couldn't authenticate, probably API Key", auth)
            case ill: IllegalArgumentException =>
                logger.error("Illegal argument, probably a bad parameter in the charge request")
                throw new PaymentException("Illegal argument", ill)
        }
        logger.info(logPrefix + "Charge success! " + chargeId.getId)
        logger.info(logPrefix + "Attempting to add charge id to Event User")

        val chargedUser = try {
            eventUserRepo.update(ev)
        }
        catch {
            case e: Exception =>
                logger.error(logPrefix + "Could not set charge Id for EventUser", e)
                throw e
        }
        logger.info(logPrefix + "Charge receipt added.")
        logger.info(logPrefix + "Payment success!")
        chargedUser
    }
}
