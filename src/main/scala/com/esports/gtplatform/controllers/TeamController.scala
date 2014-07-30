package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import models._
import org.scalatra.Ok

/**
 * Created by Matthew on 7/29/2014.
 */
class TeamController(implicit val bindingModule: BindingModule) extends StandardController {

  post("/") {
    val newteam =  parsedBody.extract[Team]
    Ok()
  }
}
