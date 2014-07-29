package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericRepo
import models.Game
import org.json4s.jackson.Serialization.write
import org.scalatra.Ok

/**
 * Created by Matthew on 7/24/2014.
 */
class GameController(implicit val bindingModule: BindingModule) extends StandardController {

  val gameRepo = inject[GenericRepo[Game]]


get("/") {

  val anyuser = authOptToken()


  val games = gameRepo.getAll

  Ok(games.map(x => x.copy()))
}

post("/") {
 val newgame = parsedBody.extract[Game]

  val newInserted = gameRepo.create(newgame)

  Ok(write(newInserted.copy()))
}

}
