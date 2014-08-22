package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericMRepo
import com.googlecode.mapperdao.jdbc.Transaction
import models.{EventUser, Event}
import org.scalatra.{Ok, NotImplemented}

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(implicit val bindingModule: BindingModule) extends APIController {

  private[this] val eventRepo = inject[GenericMRepo[Event]]


  get("/") {
    NotImplemented
  }
  get("/:id") {
    val eventId = params.getOrElse("id", halt(401, "Missing event id"))

    eventRepo.get(eventId.toInt)
    match {
      case Some(e: Event) => Ok(e)
      case None => halt(401, "An event with that Id does not exist.")
    }
  }
  get("/:id/teams") {
    // Not sure I want to implement this.
    // It doesn't make business sense to have non-tangible teams have a relationship to a tangle event.
    NotImplemented
  }
  get("/:id/users") {
    val eventId = params.getOrElse("id", halt(401, "Missing event id"))

    eventRepo.get(eventId.toInt) match {
      case Some(e: Event) =>

        params.get("pageNo") match {
          case Some(p: String) =>
            Ok(e.users.slice(pageSize * (p.toInt - 1), pageSize * p.toInt))
          case None =>
            Ok(e.users.take(pageSize))
        }

      case None => halt(401, "An event with that Id does not exist.")
    }
  }

  get("/:id/tournaments"){
    val eventId = params.getOrElse("id", halt(401, "Missing event id"))

    eventRepo.get(eventId.toInt) match {
      case Some(e: Event) =>

        params.get("pageNo") match {
          case Some(p: String) =>
            Ok(e.tournaments)
          case None =>
            Ok(e.tournaments)
        }

      case None => halt(401, "An event with that Id does not exist.")
    }
  }
  post("/") {
    auth()
    val newEvent = parsedBody.extract[Event]
    val userEvent = EventUser(newEvent, user, isPresent = false, isAdmin = true, isModerator = true)
    val tx = inject[Transaction]
    val eventUserRepo = inject[GenericMRepo[EventUser]]

    val inserted = tx { () =>
      eventUserRepo.create(userEvent)
    }
    Ok(inserted.event.id)
  }

}
