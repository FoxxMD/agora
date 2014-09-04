package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.{GenericMRepo, UserRepo}
import com.googlecode.mapperdao.jdbc.Transaction
import models.JoinType.JoinType
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
    val newEvent: Event = Event(parsedBody.\("name").extract[String],parsedBody.\("joinType").extract[JoinType])
    if (eventRepo.getByName(newEvent.name).isDefined)
      halt(400, "Event with this name already exists.")

    val tx = inject[Transaction]
    val eventUserRepo = inject[GenericMRepo[EventUser]]
    val inserted = tx { () =>

      val insertedEvent = eventRepo.create(newEvent)
      val eventDetails = EventDetails(insertedEvent, timeStart = parsedBody.\("details").\("timeStart").extractOpt[DateTime],timeEnd = parsedBody.\("details").\("timeEnd").extractOpt[DateTime])
      eventRepo.update(insertedEvent, insertedEvent.setDetails(eventDetails))
      val userEvent = EventUser(insertedEvent, user, isPresent = false, isAdmin = true, isModerator = true)

      eventUserRepo.create(userEvent)
    }
    Ok(inserted.event.id)
  }

  get("/:id") {
    Ok(requestEvent.get)
  }
  post("/:id") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to edit this event.")

    eventRepo.update(requestEvent.get, requestEvent.get.copy(name = parsedBody.\("name").extract[String],
      joinType = parsedBody.\("eventType").extract[JoinType]))
  }
  post("/:id/description") {
    auth()
    if(!requestEvent.get.isAdmin(user) && user.role != "admin")
      halt(403, "You do not have permission to edit this event.")
    val newEvent = requestEvent.get.setDescription(parsedBody.\("description").extract[String])
    eventRepo.update(requestEvent.get, requestEvent.get.setDescription(parsedBody.\("description").extract[String]))
    Ok()
  }
  get("/:id/teams") {
    Ok(requestEvent.get.tournaments.flatMap(x => x.teams).map(t => t.team))
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
