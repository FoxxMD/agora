package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import org.scalatra.Ok

/**
 * Created by Matthew on 8/6/2014.
 */
class UserController(implicit val bindingModule: BindingModule) extends StandardController {

  get("/me") {
    auth()
/*    val events = render("events" -> user.getEvents(inject[EventUserRepo]).map(
      x => ("name" -> x.name) ~ ("id" -> x.id)
      )
    )*/
    //val u = Extraction.decompose(user) merge events // merge write(user.getEvents) //("events" -> Extraction.decompose(user.getEvents))
    Ok(user)
  }
}
