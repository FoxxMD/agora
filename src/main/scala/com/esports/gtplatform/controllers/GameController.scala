package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.{GameTTLinkRepo, GameRepo}
import models.{GameTournamentType, TournamentType, Game}
import org.json4s.JsonDSL._
import org.json4s.Extraction
import org.scalatra.Ok
import scaldi.Injector

class GameController(val gameRepo: GameRepo, val gameTTLinkRepo: GameTTLinkRepo) extends StandardController with GameControllerT {
  get("/") {
    //Full path is "/games/" because of relative mounting
    Ok(gameRepo.getAll)
  }

  post("/") {
    auth()
    if(user.role != "admin")
      halt(403, "You don not have permissions to create new Games.")

      //TODO Game Creation - Validate uniqueness
      //TODO Game Creation - transaction support
      val newGame = gameRepo.create(parsedBody.extract[Game])
      val tourTypes = parsedBody.\("tournamentTypes").extract[List[TournamentType]]
      for(t <- tourTypes){
          gameTTLinkRepo.create(GameTournamentType(gameId = newGame.id.get,t.id.get))
      }
    Ok(newGame.id.get)
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
