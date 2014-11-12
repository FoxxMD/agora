package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.{GameRepo, GameTTLinkRepo}
import models.{Game, GameTournamentType, TournamentType}
import org.scalatra.Ok
import scaldi.Injector

class GameController(val gameRepo: GameRepo, val gameTTLinkRepo: GameTTLinkRepo)(implicit val inj: Injector) extends BaseController with StandardController with GameControllerT {
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
          gameTTLinkRepo.create(GameTournamentType(gameId = newGame.id.get,tournamentTypeId = t.id.get))
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
