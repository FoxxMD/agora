package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.{GenericMRepo, UserRepo}
import com.googlecode.mapperdao.jdbc.Transaction
import models.JoinType.JoinType
import models._
import org.scalatra.{BadRequest, NotImplemented, Ok}

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(implicit val bindingModule: BindingModule) extends APIController with EventControllerT {

  get("/") {
    Ok(eventRepo.getPaginated(params.getOrElse("page", "1").toInt))
  }
  post("/") {
    auth()
    val newEvent = parsedBody.extract[Event]
    if (eventRepo.getByName(newEvent.name).isDefined)
      halt(400, "Event with this name already exists.")
    val userEvent = EventUser(newEvent, user, isPresent = false, isAdmin = true, isModerator = true)
    val tx = inject[Transaction]
    val eventUserRepo = inject[GenericMRepo[EventUser]]

    val inserted = tx { () =>
      eventUserRepo.create(userEvent)
    }
    Ok(inserted.event.id)
  }

  get("/:id") {
    Ok(requestEvent.get)
  }
  post("/:id") {
    auth()
    eventRepo.update(requestEvent.get, requestEvent.get.copy(name = parsedBody.\("name").extract[String],
      eventType = parsedBody.\("eventType").extract[JoinType]))
  }
  get("/:id/teams") {
    // Not sure I want to implement this.
    // It doesn't make business sense to have non-tangible teams have a relationship to a tangle event.
    NotImplemented
  }
  get("/:id/users") {
    params.get("pageNo") match {
      case Some(p: String) =>
        Ok(requestEvent.get.users.slice(pageSize * (p.toInt - 1), pageSize * p.toInt))
      case None =>
        Ok(requestEvent.get.users.take(pageSize))
    }
  }
  post("/:id/users") {
    val userId = parsedBody.\("userId").extractOpt[Int]
    auth()
    if (requestEvent.get.eventType == JoinType.Invite && user.role != "admin")
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
  delete("/:id/users") {
    val userId = parsedBody.\("userId").extractOpt[Int]
    auth()
    userId match {
      case Some(uid: Int) =>
        if (user.role != "admin" || !requestEvent.get.isModerator(user))
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
  }
}
