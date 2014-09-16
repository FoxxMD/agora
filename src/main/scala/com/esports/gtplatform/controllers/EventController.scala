package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.services.{PaymentService, StripePayment}
import com.esports.gtplatform.business.{EntityAuxillarySerializer, GenericMRepo, UserRepo}
import com.googlecode.mapperdao.jdbc.Transaction
import models.JoinType.JoinType
import models.PaymentType.PaymentType
import models._
import org.joda.time.DateTime
import org.scalatra.{BadRequest, NotImplemented, Ok}

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(implicit val bindingModule: BindingModule) extends APIController with EventControllerT {

  get("/") {
    val events = eventRepo.getPaginated(params.getOrElse("page", "1").toInt)
    Ok(events)
  }
  post("/") {
    auth()
    val newEvent: Event = Event(parsedBody.\("name").extract[String], parsedBody.\("joinType").extract[JoinType])
    if (eventRepo.getByName(newEvent.name).isDefined)
      halt(400, "Event with this name already exists.")

    val tx = inject[Transaction]
    val eventUserRepo = inject[GenericMRepo[EventUser]]
    val inserted = tx { () =>

      val insertedEvent = eventRepo.create(newEvent)
      val eventDetails = EventDetails(insertedEvent, timeStart = parsedBody.\("details").\("timeStart").extractOpt[DateTime], timeEnd = parsedBody.\("details").\("timeEnd").extractOpt[DateTime])
      eventRepo.update(insertedEvent, insertedEvent.setDetails(eventDetails))
      val userEvent = EventUser(insertedEvent, user, isPresent = false, isAdmin = true, isModerator = true, hasPaid = false)

      eventUserRepo.create(userEvent)
    }
    Ok(inserted.event.id)
  }

  get("/:id") {
    Ok(requestEvent.get)
  }
  patch("/:id") {
    auth()
    if (!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to edit this event.")

    val extractedDetails = parsedBody.\("details").extract[EventDetails].copy(event = requestEvent.get)

    val newEvent = requestEvent.get.copy(name = parsedBody.\("name").extract[String],
      joinType = parsedBody.\("joinType").extract[JoinType]).setDetails(extractedDetails)

    eventRepo.update(requestEvent.get, newEvent)
    Ok()
  }
  //Change the description of the event(front page)
  post("/:id/description") {
    auth()
    if (!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to edit this event.")
    val newEvent = requestEvent.get.setDescription(parsedBody.\("description").extract[String])
    eventRepo.update(requestEvent.get, requestEvent.get.setDescription(parsedBody.\("description").extract[String]))
    Ok()
  }
  //Get a list of teams that are participating in tournaments at this event
  get("/:id/teams") {
    Ok(requestEvent.get.tournaments.flatMap(x => x.teams))
  }
  //Get a list of users for this event
  get("/:id/users") {
    params.get("pageNo") match {
      case Some(p: String) =>
        Ok(requestEvent.get.users) //.slice(pageSize * (p.toInt - 1), pageSize * p.toInt))
      case None =>
        Ok(requestEvent.get.users) //.take(pageSize))
    }
  }
  get("/:id/users/:userId") {
    Ok(requestEventUser.get.user)
  }
  //Add a user to an event
  post("/:id/users") {
    val userId = parsedBody.\("userId").extractOpt[Int]
    auth()
    if (requestEvent.get.joinType == JoinType.Invite && user.role != "admin")
      halt(403, "This event is invite only. In order to join a moderator must invite you or accept your join request.")
    userId match {
      case Some(uid: Int) =>
        if (user.role != "admin")
          halt(403, "You do not have permission to add users to this event.")
        val userRepo = inject[UserRepo]
        userRepo.get(uid) match {
          case Some(u: User) =>
            eventRepo.update(requestEvent.get, requestEvent.get.addUser(u))
            Ok()
          case None =>
            BadRequest("No user exists with that Id")
        }
      case None =>
        eventRepo.update(requestEvent.get, requestEvent.get.addUser(user))
        Ok()
    }
  }
  //Delete a user from an event
  delete("/:id/users") {
    val userId = parsedBody.\("userId").extractOpt[Int]
    auth()
    userId match {
      case Some(uid: Int) =>
        if (user.role != "admin" || !requestEvent.get.isAdmin(user))
          halt(403, "You do not have permission to remove users to this event.")
        val userRepo = inject[UserRepo]
        userRepo.get(uid) match {
          case Some(u: User) =>
            eventRepo.update(requestEvent.get, requestEvent.get.removeUser(u))
            Ok()
          case None =>
            BadRequest("No user exists with that Id")
        }
      case None =>
        eventRepo.update(requestEvent.get, requestEvent.get.removeUser(user))
        Ok()
    }
  }
  patch("/:id/users/:userId") {
    auth()
    if (user.role != "admin" || !requestEvent.get.isAdmin(user))
      halt(403, "You do not have permission to edit users at this event.")

    val present = parsedBody.\("isPresent").extractOpt[Boolean]
    val admin = parsedBody.\("isAdmin").extractOpt[Boolean]
    val mod = parsedBody.\("isModerator").extractOpt[Boolean]

    var eu = requestEvent.get.users.find(x => x.user.id == params("userId").toInt).getOrElse {
      logger.warn("Admin " + user.id + " tried to modify an EventUser for a non-existent user " + params("userId") + " on Event " + requestEvent.get.id)
      halt(400, "This user is not in this event.")
    } //TODO make this work with requestEventUser
    if(present.isDefined)
      eu = eu.copy(isPresent = present.get)
    if(admin.isDefined)
      eu = eu.copy(isAdmin = admin.get)
    if(mod.isDefined)
      eu = eu.copy(isModerator = mod.get)
    eventRepo.update(requestEvent.get, requestEvent.get.removeUser(eu.user).addUser(eu))
    Ok()
  }
  //get a list of tournaments for an event
  get("/:id/tournaments") {
    params.get("pageNo") match {
      case Some(p: String) =>
        Ok(requestEvent.get.tournaments)
      case None =>
        Ok(requestEvent.get.tournaments)
    }
  }
  post("/:id/tournaments") {
    NotImplemented
    //HENRY WHERE YOU AT?!
  }
  //A user paying for registation
  post("/:id/users/:userId/payRegistration") {
    import scala.collection.mutable
    auth()
    if (params("userId").toInt != user.id && (user.role == "admin" || requestEvent.get.isAdmin(user))) {
      val paid = parsedBody.\("paid").extractOrElse[Boolean](halt(400, "Missing payment status"))
      val receipt = parsedBody.\("receipt").extractOpt[String]
      val userRepo = inject[UserRepo]
      val theuser = requestEventUser.get.user//userRepo.get(params("userId").toInt).getOrElse(halt(400, "User with that Id does not exist."))
      eventRepo.update(requestEvent.get, requestEvent.get.setUserPayment(theuser, paid = paid, receipt))
      val atype = if(user.role == "admin") "Admin" else "Event Admin"
      logger.info("["+atype+" "+ user.id + "] Setting payment for User " + theuser.id + " on Event " + requestEvent.get.id + " to " + paid.toString.toUpperCase)
      Ok()
    }
    else {
      parsedBody.\("type").extractOpt[String] match {
        case None =>
          halt(400, "No payment type was specified.")
        case Some("Stripe") =>
          val card = parsedBody.\("card").extract[String]
          val paymentService: PaymentService = new StripePayment(requestEvent.get)
          paymentService.makePayment(user, mutable.Map("card" -> card)) match {
            case (false, s: String) =>
              BadRequest("Payment was not successful. Reason: " + s)
            case (true, s: String) =>
              Ok("Payment successful!")
              eventRepo.update(requestEvent.get, requestEvent.get.setUserPayment(user, paid = true, Some(s)))
          }
        case Some(s: String) =>
          NotImplemented
        //TODO more payment types?
      }
    }
  }
  //add new payment option
  post("/:id/payments") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to do that")
    if(requestEvent.get.payments.exists(x => x.payType == parsedBody.\("payType").extract[PaymentType]))
      halt(400, "You cannot add a payment type more than once.")
    val ep = EventPayment(requestEvent.get,
      parsedBody.\("payType").extract[PaymentType],
      parsedBody.\("secretKey").extractOpt[String],
      parsedBody.\("publicKey").extractOpt[String],
      parsedBody.\("address").extractOpt[String],
      parsedBody.\("amount").extract[Double])
    val updated = eventRepo.update(requestEvent.get, requestEvent.get.addPayment(ep))
    Ok(updated.payments.filter(x => x.isEnabled))
  }
  //Change details of a payment option
  post("/:id/payments/:payId") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to do that")
    val ep = EventPayment(requestEvent.get,
      parsedBody.\("payType").extract[PaymentType],
      parsedBody.\("secretKey").extractOpt[String],
      parsedBody.\("publicKey").extractOpt[String],
      parsedBody.\("address").extractOpt[String],
      parsedBody.\("amount").extract[Double],
      parsedBody.\("isEnabled").extract[Boolean],
      parsedBody.\("id").extract[Int])
    val updated = eventRepo.update(requestEvent.get, requestEvent.get.changePayment(params("payId").toInt, ep))
    Ok(updated.payments.filter(x => x.isEnabled))
  }
  //delete a payment option
  delete("/:id/payments/:payId") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to do that")
    val updated = eventRepo.update(requestEvent.get, requestEvent.get.removePayment(params("payId").toInt))
    Ok(updated.payments.filter(x => x.isEnabled))
  }
  //change privacy settings
  post("/:id/privacy") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to do that")
    eventRepo.update(requestEvent.get, requestEvent.get.copy(joinType = parsedBody.\("privacy").extract[JoinType]))
    Ok()
  }
}
