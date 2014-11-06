package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.services._
import com.esports.gtplatform.business._
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.joda.time.DateTime
import org.json4s
import org.json4s.Extraction
import org.scalatra.scalate.ScalateUrlGeneratorSupport
import org.scalatra.{BadRequest, NotImplemented, Ok}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scaldi.Injector

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(val eventRepo: EventRepo,
                      val eventUserRepo: EventUserRepo,
                      val tournamentRepo: TournamentRepo,
                      val userRepo: UserRepo,
                      val ttRepo: TournamentTypeRepo,
                         val eventService: EventServiceT,
                         val eventDetailRepo: EventDetailRepo,
                         val eventPaymentRepo: EventPaymentRepo) extends APIController with EventControllerT with ScalateUrlGeneratorSupport {

    get("/") {
        val events = eventRepo.getPaginated(params.getOrElse("page", "1").toInt)
        Ok(events)
    }
    post("/") {
        auth()
        val extractedEvent = parsedBody.extract[Event]
        if (eventService.isUnique(extractedEvent))
            halt(400, "Event with this name already exists.")

            //TODO transaction
            val insertedEvent = eventRepo.create(extractedEvent)
            eventDetailRepo.create(extractDetails(parsedBody.\("details")).copy(eventId = insertedEvent.id))
            eventUserRepo.create(EventUser(eventId = insertedEvent.id.get,userId = user.id.get, isPresent = false, isAdmin = true, isModerator = true, hasPaid = false))

        Ok(insertedEvent.id.get)
    }

    get("/:id") {
        Ok(requestEvent.get)
    }
    patch("/:id") {
        auth()
        if (!eventService.canModify(user, requestEvent.get))
            halt(403, "You do not have permission to edit this event.")
        eventRepo.update(parsedBody.extract[Event])
        Ok()
    }

    //Get a list of teams that are participating in tournaments at this event
    get("/:id/groups") {
        val teams = requestEvent.get.tournaments.flatMap { x =>
            x.teams.map { u =>
                Extraction.decompose(u) merge render(
                    "tournament" -> Extraction.decompose(x).removeField {
                        case ("details", _) => true
                        case ("event", _) => true
                        case ("teams", _) => true
                        case ("users", _) => true
                        case _ => false
                    }.merge(render(
                        ("name" -> x.details.get.name) ~
                            ("teamMaxSize" -> x.details.get.teamMaxSize) ~
                            ("teamMinSize" -> x.details.get.teamMinSize))))
            }
        }
        params.get("page") match {
            case Some(p: String) =>
                Ok(teams.slice(pageSize * (p.toInt - 1), pageSize))
            case None =>
                Ok(teams)
        }

    }
    //Get a list of users for this event
    get("/:id/users") {
        params.get("page") match {
            case Some(p: String) =>
                Ok(eventUserRepo.getByEvent(requestEvent.get.id.get).drop(pageSize * (p.toInt - 1)).take(pageSize))
            case None =>
                Ok(eventUserRepo.getByEvent(requestEvent.get.id.get).take(pageSize))
        }
    }
    get("/:id/users/:userId") {
        Ok(requestEventUser.get)
    }
    //Add a user to an event
    post("/:id/users") {
        auth()
        if (!eventService.canJoin(user))
            halt(403, "This event is invite only. In order to join a moderator must invite you or accept your join request.")
        val extractedEventUser = parsedBody.extract[EventUser]
        if(extractedEventUser.userId != user.id.get && !eventService.hasAdminPermissions(user,requestEvent.get.id.get))
        {
            logger.warn("[Event]("+requestEvent.get.id.get+") User " + user.id.get + " attempted to add User " + extractedEventUser.userId + " without permission.")
            halt(403, "You do not have permission to add users to this event.")
        }
        eventUserRepo.create(extractedEventUser)
        if(extractedEventUser.userId != user.id.get)
            logger.info("[Admin] (" + user.id + ") Added User " + extractedEventUser.userId + " to Event " + requestEvent.get.id.get)
        else
            logger.info("[Event] ("+ requestEvent.get.id +")User " + user.id + " joined Event ")
        Ok()
    }
    //Delete a user from an event
    delete("/:id/users/:userId") {
        auth()
        if(requestEventUser.get.userId != user.id.get && !eventService.hasAdminPermissions(user,requestEvent.get.id.get))
        {
            logger.warn("[Event]("+requestEvent.get.id.get+") User " + user.id.get + " attempted to delete User " + requestEventUser.get.userId + " without permission.")
            halt(403, "You do not have permission to delete users to this event.")
        }

        eventUserRepo.delete(requestEventUser.get)

        if(requestEventUser.get.userId != user.id.get)
            logger.info("[Admin] (" + user.id + ") Deleted User " + requestEventUser.get.userId + " to Event " + requestEvent.get.id.get)
        else
            logger.info("[Event] ("+ requestEvent.get.id +")User " + user.id + " left Event ")
        Ok()
    }
    patch("/:id/users/:userId") {
        auth()
        if (!eventService.hasModeratorPermissions(user, requestEvent.get.id.get))
        {
            halt(403, "You do not have permission to edit users at this event.")
            logger.warn("[Event]("+requestEvent.get.id.get+") User " + user.id.get + " attempted to modify User " + requestEventUser.get.userId + " without permission.")
        }


        val extractedEU = parsedBody.extract[EventUser]
        if((extractedEU.isAdmin != requestEventUser.get.isAdmin || extractedEU.isModerator != requestEventUser.get.isModerator) && !eventService.hasAdminPermissions(user, requestEvent.get.id.get))
        {
            logger.warn("[Event]("+requestEvent.get.id.get+") User " + user.id.get + " attempted to modify User " + requestEventUser.get.userId + " without admin permission.")
            halt(403, "You do not have permission to edit users at this event.")
        }

        eventUserRepo.update(extractedEU)
        Ok()
    }
    //get a list of tournaments for an event
    get("/:id/tournaments") {
     Ok(tournamentRepo.getByEvent(paramId.get))
    }


    //A user paying for registration
    post("/:id/users/:userId/payRegistration") {
        NotImplemented()
/*        import scala.collection.mutable
        auth()
        if (params("userId").toInt != user.id && (user.role == "admin" || requestEvent.get.isAdmin(user))) {
            val paid = parsedBody.\("paid").extractOrElse[Boolean](halt(400, "Missing payment status"))
            val receipt = parsedBody.\("receipt").extractOpt[String]
            val theuser = requestEventUser.get.userId //userRepo.get(params("userId").toInt).getOrElse(halt(400, "User with that Id does not exist."))
            eventRepo.update(requestEvent.get)
            val atype = if (user.role == "admin") "Admin" else "Event Admin"
            logger.info("[" + atype + "] (" + user.id + ") Setting payment for User " + theuser.id + " on Event " + requestEvent.get.id + " to " + paid.toString.toUpperCase)
            Ok()
        }
        else {
            parsedBody.\("type").extractOpt[String] match {
                case None =>
                    halt(400, "No payment type was specified.")
                case Some("Stripe") =>
                    val card = parsedBody.\("card").extract[String]
                    val paymentService: PaymentService = new StripePayment(requestEvent.get)
                    paymentService.checkForExistingUser(user) match {
                        case None =>
                            logger.info("No user with customer or payment Id found.")
                            val eventUserRepo = inject[EventUserRepo]
                            val payingUser = eventUserRepo.getByUser(user).headOption.getOrElse(halt(500,"Could not find you in our system! Contact an admin to sort this out."))

                           val evWithCustomer = try {
                                paymentService.createCustomer(payingUser,mutable.Map("card" -> card))
                            }
                            catch {
                                case cust: CustomerException =>
                                    halt(500,
                                      s"""There was a problem with stripe. Your card has NOT been charged. Please check your payment details and try again.
                                         | The problem: ${cust.getMessage} .
                                         | The error: ${cust.getCause.getMessage}""".stripMargin)
                                case e: Exception =>
                                   val json = render(("message" -> "We had a problem storing your payment Id. Your card has NOT been charged. DO NOT attempt to pay again, please contact an admin or send us a ticket.") ~
                                                    ("customerId" -> e.getMessage))
                                    halt(500, json)
                            }

                            try {
                                paymentService.makePayment(evWithCustomer)
                            }
                            catch {
                                case p: PaymentException =>
                                    val msg =
                                      s"""There was a problem with stripe. Your card has NOT been charged. DO NOT attempt to pay again, please contact an admin or send us a ticket.
                                         |The problem: ${p.getMessage} .
                                         |The error: ${p.getCause.getMessage} """.stripMargin
                                    val json = render(("message" -> msg) ~
                                        ("customerId" -> evWithCustomer.customerId))
                                    halt(500, json)
                                case e: Exception =>
                                    val msg = "We had a problem storing your payment receipt. Your card HAS been charged. Please contact an admin or send us a ticket."
                                    val json = render(("message" -> msg) ~
                                        ("customerId" -> evWithCustomer.customerId))
                                    halt(500, json)
                            }
                            Ok("Payment successful!")

                        case _ =>
                            logger.warn("User with customer or payment Id found!")
                            halt(500, "You already have already payment information in our system! Please contact an admin or send us a ticket letting us know the problem.")
                    }
                case Some(s: String) =>
                    NotImplemented
                //TODO more payment types?
            }
        }*/
    }
    //add new payment option
    post("/:id/payments") {
        auth()
        if (eventService.hasAdminPermissions(user, requestEvent.get.id.get))
            halt(403, "You do not have permission to do that")

        val extractedEP = parsedBody.extract[EventPayment]

        if (eventPaymentRepo.getByEvent(requestEvent.get.id.get).exists(x => x.payType == extractedEP.payType))
            halt(400, "You cannot add a payment type more than once.")
        eventPaymentRepo.create(extractedEP)
        Ok()
    }
    //Change details of a payment option
    patch("/:id/payments/:payId") {
        auth()
        if (eventService.hasAdminPermissions(user, requestEvent.get.id.get))
            halt(403, "You do not have permission to edit payments")

        val extractedEP = parsedBody.extract[EventPayment]

        eventPaymentRepo.update(extractedEP)
        Ok()
    }
    //delete a payment option
    delete("/:id/payments/:payId") {
        auth()
        if (eventService.hasAdminPermissions(user, requestEvent.get.id.get))
            halt(403, "You do not have permission to delete payments")

        eventPaymentRepo.delete(params("payId").toInt)
        Ok()
    }

    def extractDetails(obj: json4s.JValue) = {
        obj.extract[EventDetail].copy(
            credits = Option(compact(render(obj.\("credits")))),
            scheduledEvents = Option(compact(render(obj.\("scheduledEvents")))),
            faq = Option(compact(render(obj.\("faq")))),
            prizes = Option(compact(render(obj.\("prizes")))),
            description = Option(compact(render(obj.\("description"))))
        )
    }

    /*
    * Tournament URL Support
    * */

/*    post("/tournaments"){
        redirect(url("tournaments"))
    }
    get("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    patch("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    delete("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    get("/tournaments/:id/details"){
        redirect(url("tournaments/"+params("tid")))
    }
    patch("/tournaments/:tid/details"){
        redirect(url("tournaments/"+params("tid")+"/details"))
    }*/
}
