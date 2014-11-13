package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.{EventPaymentRepo, EventUserRepo}
import com.stripe.exception._
import com.stripe.model.{Account, Charge, Customer}
import models._
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * Created by Matthew on 9/9/2014.
 */
class CustomerException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)

class PaymentException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)

abstract class PaymentService(event: Event, eventUserRepo: EventUserRepo, eventPaymentRepo: EventPaymentRepo) {
    val logger = LoggerFactory.getLogger(getClass)
    val pType: String
    val logPrefix = "[Payment][Event](" + event.id + ") "

    def getExistingCustomer(u: User): Option[mutable.Map[String, String]]

    def updateExistingCustomer(ev: EventUser, info: mutable.Map[String, String]): Unit

    def createCustomer(ev: EventUser, info: mutable.Map[String, String]): EventUser

    def makePayment(ev: EventUser): EventUser
}

class StripePayment(event: Event, eventUserRepo: EventUserRepo, eventPaymentRepo: EventPaymentRepo) extends PaymentService(event, eventUserRepo, eventPaymentRepo) {
    val pType = "Stripe"
    val eventPayment = event.payments.find(x => x.payType == pType).getOrElse {
        logger.error("Event " + event.id + "does not contain payment option of type " + pType)
        throw new IllegalArgumentException("This event does not contain the payment type " + pType + ".")
    }

    def tryStripeApi(apiCall: => Any) = {
        try {
            apiCall
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
    }

    def getExistingCustomer(u: User): Option[mutable.Map[String, String]] = {
        logger.info(logPrefix + "Checking for existing Stripe customer for User " + u.id.get)
        val existingCustomer: mutable.Map[String, String] = mutable.Map()

        //Get stripe payment info for this event
        val currentPaymentInfo = eventPaymentRepo.getByEvent(event.id.get).find(x => x.payType == "stripe").get
        //Find any instances of this stripe account used in any other events
        val existingEventUser = eventPaymentRepo.getBySecret(currentPaymentInfo.secretKey.get).flatMap {
            //find EventUsers for each event that match our paying user
            x => eventUserRepo.getByEventAndUser(x.eventsId, u.id.get)
            //Find the first one where the user has paid.
        }.find(x => x.paymentType.isDefined && x.paymentType.get == "stripe" && x.customerId.isDefined)

        //If the EventUser exists then we know they have an existing customer object for this stripe account
        if (existingEventUser.isDefined) {
            logger.info(logPrefix + "Customer " + existingEventUser.get.customerId.get + " found! Returning customer info.")
            val cu: Customer = Customer.retrieve(existingEventUser.get.customerId.get)
            val card = cu.getCards.getData.head
            val displayName = Account.retrieve(currentPaymentInfo.secretKey.get).getDisplayName
            existingCustomer.put("last4", card.getLast4)
            existingCustomer.put("accountName", displayName)
            Option(existingCustomer)
        }
        else {
            logger.info(logPrefix + "No customer found.")
            None
        }
    }

    def updateExistingCustomer(ev: EventUser, info: mutable.Map[String, String]): Unit = {
        logger.info(logPrefix + "Retrieving Stripe customer " + ev.customerId.get + " to update.")
        val cu: Customer = Customer.retrieve(ev.customerId.get)
        tryStripeApi({
            cu.update(info)
        })
        logger.info(logPrefix + "Customer updated.")
    }

    def createCustomer(ev: EventUser, info: mutable.Map[String, String]): EventUser = {
        logger.info(logPrefix + "Attempting to create Stripe customer for User " + ev.userId)
        if (info.get("card").isEmpty) {
            logger.error(logPrefix + "Card info was not passed for payment.")
            throw new IllegalArgumentException("Card token was not passed from stripe.")
        }
        else {
            info.put("email", ev.user.get.email)
            info.put("description", "User " + ev.userId)

            val customerId = try {
                Customer.create(info, eventPayment.secretKey.get).getId
            }
            catch {
                case ce: CardException =>
                    logger.error(logPrefix + "Stripe had a card problem", ce)
                    throw ce
                    //throw new CustomerException("Stripe had a card problem", ce)
                case ir: InvalidRequestException =>
                    logger.error(logPrefix + "Stripe had a bad request", ir)
                    throw ir
                    //throw new CustomerException("Stripe had a bad request", ir)
                case auth: AuthenticationException =>
                    logger.error(logPrefix + "Stripe couldn't authenticate, probably API Key", auth)
                    throw auth
                    //throw new CustomerException("Stripe couldn't authenticate, probably API Key", auth)
                case ill: IllegalArgumentException =>
                    throw new CustomerException("Illegal argument", ill)
            }

            logger.info("[Payment][Event](" + ev.eventId + ") Customer " + customerId + " created successfully.")

            try {
                val updatedUser = eventUserRepo.update(ev.copy(customerId = Option(customerId)))
                logger.info(logPrefix + "Customer Id set to EventUser successfully")
                updatedUser
            }
            catch {
                case e: Exception =>
                    logger.error(logPrefix + "Could not set customer Id for EventUser", e)
                    throw new Exception(customerId, e)
            }
        }
    }


    def makePayment(ev: EventUser): EventUser = {
        logger.info(logPrefix + "Creating charge object...")
        val charge = mutable.Map.empty[String, String]
        charge.put("amount", (eventPayment.amount * 100).toInt.toString)
        charge.put("currency", "usd")
        charge.put("description", "Registration for " + event.name)
        charge.put("customer", ev.customerId.getOrElse(throw new IllegalArgumentException("EventUser does not have a customer Id, cannot make a charge.")))
        charge.put("receipt_email", ev.user.get.email)
        val returnedCharge = try {
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
        logger.info(logPrefix + "Charge success! " + returnedCharge.getId)
        logger.info(logPrefix + "Attempting to add charge id to Event User")

        val chargedUser = try {

            eventUserRepo.update(ev.copy(receiptId = Option(returnedCharge.getId)))
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
