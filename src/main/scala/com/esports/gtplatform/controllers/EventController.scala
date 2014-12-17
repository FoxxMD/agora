package com.esports.gtplatform.controllers

import javax.smartcardio.CardException

import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services._
import com.stripe.exception.{AuthenticationException, InvalidRequestException}
import models._
import org.json4s
import org.json4s.JsonDSL._
import org.json4s.{Extraction, _}
import org.scalatra.Ok
import scaldi.Injector

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(val eventRepo: EventRepo,
                      val eventUserRepo: EventUserRepo,
                      val tournamentRepo: TournamentRepo,
                      override val userRepo: UserRepo,
                      val ttRepo: BracketTypeRepo,
                      val eventService: EventServiceT,
                      val eventDetailRepo: EventDetailRepo,
                      val eventPaymentRepo: EventPaymentRepo)(implicit val inj: Injector) extends BaseController with APIController with EventControllerT {

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
        eventUserRepo.create(EventUser(eventId = insertedEvent.id.get, userId = user.id.get, isPresent = false, isAdmin = true, isModerator = true, hasPaid = false))

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
                Ok(eventUserRepo.getByEventHydrated(requestEvent.get.id.get).drop(pageSize * (p.toInt - 1)).take(pageSize))
            case None =>
                val e = eventUserRepo.getByEventHydrated(requestEvent.get.id.get)
                val a = e.take(pageSize)
                Ok(a)
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
        if (extractedEventUser.userId != user.id.get && !eventService.hasAdminPermissions(user, requestEvent.get.id.get)) {
            logger.warn("[Event](" + requestEvent.get.id.get + ") User " + user.id.get + " attempted to add User " + extractedEventUser.userId + " without permission.")
            halt(403, "You do not have permission to add users to this event.")
        }
        eventUserRepo.create(extractedEventUser)
        if (extractedEventUser.userId != user.id.get)
            logger.info("[Admin] (" + user.id + ") Added User " + extractedEventUser.userId + " to Event " + requestEvent.get.id.get)
        else
            logger.info("[Event] (" + requestEvent.get.id + ")User " + user.id + " joined Event ")
        Ok()
    }
    //Delete a user from an event
    delete("/:id/users/:userId") {
        auth()
        if (requestEventUser.get.userId != user.id.get && !eventService.hasAdminPermissions(user, requestEvent.get.id.get)) {
            logger.warn("[Event](" + requestEvent.get.id.get + ") User " + user.id.get + " attempted to delete User " + requestEventUser.get.userId + " without permission.")
            halt(403, "You do not have permission to delete users to this event.")
        }

        eventUserRepo.delete(requestEventUser.get)

        if (requestEventUser.get.userId != user.id.get)
            logger.info("[Admin] (" + user.id + ") Deleted User " + requestEventUser.get.userId + " to Event " + requestEvent.get.id.get)
        else
            logger.info("[Event] (" + requestEvent.get.id + ")User " + user.id + " left Event ")
        Ok()
    }
    patch("/:id/users/:userId") {
        auth()
        if (!eventService.hasModeratorPermissions(user, requestEvent.get.id.get)) {
            halt(403, "You do not have permission to edit users at this event.")
            logger.warn("[Event](" + requestEvent.get.id.get + ") User " + user.id.get + " attempted to modify User " + requestEventUser.get.userId + " without permission.")
        }


        val extractedEU = parsedBody.extract[EventUser]
        if ((extractedEU.isAdmin != requestEventUser.get.isAdmin || extractedEU.isModerator != requestEventUser.get.isModerator || extractedEU.hasPaid != requestEventUser.get.hasPaid) && !eventService.hasAdminPermissions(user, requestEvent.get.id.get)) {
            logger.warn("[Event](" + requestEvent.get.id.get + ") User " + user.id.get + " attempted to modify User " + requestEventUser.get.userId + " without admin permission.")
            halt(403, "You do not have permission to edit users at this event.")
        }

        eventUserRepo.update(extractedEU)
        Ok()
    }
    //get a list of tournaments for an event
    get("/:id/tournaments") {
        Ok(tournamentRepo.getByEvent(paramId.get))
    }

    get("/:id/users/:userId/pay") {
        auth()
        val paymentService = new StripePayment(event = requestEvent.get, eventUserRepo = eventUserRepo, eventPaymentRepo = eventPaymentRepo)
        Ok(paymentService.getExistingCustomer(user))
    }


    //A user paying for registration
    post("/:id/users/:userId/pay/stripe") {
        import scala.collection.mutable
        auth()
        val paymentService = new StripePayment(event = requestEvent.get, eventUserRepo = eventUserRepo, eventPaymentRepo = eventPaymentRepo)
        val eventUser = eventUserRepo.getByEventAndUser(requestEvent.get.id.get, user.id.get).get

        try {
            if (paymentService.getExistingCustomer(user).isEmpty) {
                val card = parsedBody.\("card").extract[String]
                val evWithCustomer = paymentService.createCustomer(eventUser, mutable.Map("card" -> card))
                paymentService.makePayment(evWithCustomer)
            }
            else
                paymentService.makePayment(eventUser)
        }
        catch {
            case ce: CardException =>
                halt(500, "There was a problem with your card. Make sure it is valid and your information is entered correctly. Error: " + ce.getMessage)
            case ir: InvalidRequestException =>
                halt(500, "Something was missing from our Stripe call! Double check your payment information and if this error persists contact the admins. Error: " + ir.getMessage)
            case auth: AuthenticationException =>
                halt(500, "We couldn't authenticate with Stripe! this could be a problem with Stripe, or the payment information for this event is not setup correctly. Error: " + auth.getMessage)
            case e: Exception =>
                halt(500, "There was a problem with processing your payment. Please contact an admin. Error: " + e.getMessage)
        }
        Ok("Payment successful!")
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
