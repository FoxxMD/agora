package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.GameRepo
import models.Game
import org.json4s.JsonDSL._
import org.json4s.Extraction
import org.scalatra.Ok
import scaldi.Injector

class GameController(val gameRepo: GameRepo) extends StandardController with GameControllerT {
  get("/") {
    //Full path is "/games/" because of relative mounting
    Ok(gameRepo.getAll)
  }

  post("/") {
    auth()
    if(user.role != "admin")
      halt(403, "You don not have permissions to create new Games.")
    //Extracting JSON to a domain object ONLY WORKS IF THE OBJECT IS A CASE CLASS. Otherwise we must provide a custom serializer.
    //Just another reason to make all domain objects case classes.
    Ok(gameRepo.create(parsedBody.extract[Game]).id)
  }
  get("/:id") {
      Ok(gameRepo.get(paramId.get))
  }
  post("/:id") {
    auth()
    if(user.role != "admin")
      halt(403, "You don not have permissions to create new Games.")
    gameRepo.update(requestGame.get)
    Ok()
  }
}
