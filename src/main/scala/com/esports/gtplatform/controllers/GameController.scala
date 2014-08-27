package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import models.Game
import org.scalatra.Ok

class GameController(implicit val bindingModule: BindingModule) extends StandardController with GameControllerT {
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
    Ok(requestGame.get)
  }
  post("/:id") {
    auth()
    if(user.role != "admin")
      halt(403, "You don not have permissions to create new Games.")
    gameRepo.update(requestGame.get, parsedBody.extract[Game])
    Ok()
  }
}
