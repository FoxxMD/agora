package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.{GameRepo, GenericMRepo}
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.scalatra.{BadRequest, Forbidden, Ok}

/**
 * Created by Matthew on 7/29/2014.
 */
class GuildController(implicit val bindingModule: BindingModule) extends APIController with GuildControllerT {
  get("/?") {
    val guilds = guildRepo.getPaginated(params.getOrElse("page", "1").toLong)
    Ok(guilds)
  }
  post("/") {
    auth()
    val gameRepo = inject[GameRepo]
    val guildUserRepo = inject[GenericMRepo[GuildUser]]

    val extractedGuild = parsedBody.extract[Guild]
    val persistedGames = extractedGuild.games.map { x => gameRepo.get(x.id).get}
    val newguild = extractedGuild.copy(games = persistedGames)

    var userFT: Option[User] = None

    if (guildRepo.getByName(newguild.name).isDefined)
      halt(400, "That name is already in use!")

    params.get("captainId") match {
      case Some(a: String) =>
        if (user.role == "admin") {
          val personRepo = inject[GenericMRepo[User]]
          val thisUser = personRepo.get(a.toInt)
          if (thisUser == None)
            halt(400, "No user found with that Id")
          userFT = thisUser
        } else
          halt(401)
      case None =>
        userFT = Some(user)
    }
    val tx = inject[Transaction]
    val tid = tx { () =>
      val insertedGuild = guildRepo.create(newguild)
      val tu = GuildUser(insertedGuild, userFT.get, isCaptain = true)
      guildUserRepo.create(tu)
      insertedGuild.id
    }
    Ok(tid)
  }
  /*
  * Team Functionality
  *
  */
  get("/:id") {
    Ok(requestGuild)
  }
  post("/:id") {
    auth()
    if (requestGuild.getCaptain == user || user.role == "admin") {
      guildRepo.update(requestGuild)
      Ok()
    }
    else
      Forbidden("You don't have rights to modify this team.")
  }
  delete("/:id") {
    auth()
    if (requestGuild.getCaptain == user || user.role == "admin") {
      guildRepo.delete(requestGuild)
      Ok()
    }
    else
      Forbidden("You don't have rights to modify this team.")
  }
  /*
   * Team Members Functionality
   *
   */
  get("/:id/members") {
    Ok(requestGuild.members)
  }
  post("/:id/members") {
    val addingUser = parsedBody.\("userId").extractOrElse[Int](halt(401, "User Id parameter is missing"))
    auth()
      if(user.role != "admin" && user.id != addingUser)
          halt(403, "You don't have permission to edit this team.")
/*    if (user.role != "admin" && requestGuild.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")*/ //TODO after getting invites working...

    val userRepo = inject[GenericMRepo[User]]
    userRepo.get(addingUser) match {
      case Some(u: User with Persisted) =>
        if (!requestGuild.members.exists(x => x.userId == u)) {
          guildRepo.update(requestGuild)
          Ok()
        }
        else
          BadRequest("User is already on this Team.")
      case None => BadRequest("No user with that Id exists.")
    }
  }
  delete("/:id/members") {
    val removingUser = params("userId").toInt
    auth()
      if(user.role != "admin" && user.id != removingUser)
          halt(403, "You don't have permission to edit this team.")
    /*if (user.role != "admin" && requestGuild.getCaptain != user)
      halt(403, "You don't have permission to edit this team")*/ //TODO after getting invites working...

    val userRepo = inject[GenericMRepo[User]]
    userRepo.get(removingUser) match {
      case Some(u: User) =>
        if (requestGuild.members.exists(x => x.userId.id == u.id)) {
          guildRepo.update(requestGuild)
        }
        else {
          BadRequest("User with that Id is not on this team")
        }
      case None => BadRequest("No user with that Id exists.")
    }
  }
  /*
   * Game Functionality
   *
   */
  get("/:id/games") {
    Ok(requestGuild.games)
  }
  post("/:id/games") {
    val gameId = parsedBody.\("gameId").extractOrElse[Int](halt(400, "Game Id parameter is missing"))
    auth()
    if (user.role != "admin" && requestGuild.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")
      val gameRepo = inject[GenericMRepo[Game]]
      gameRepo.get(gameId) match {
        case Some(g: Game) =>
          if (!requestGuild.games.contains(g))
            guildRepo.update(requestGuild)
          else
            BadRequest("Team already plays this game.")
        case None =>
          BadRequest("Game with that Id does not exist.")
      }
  }
  delete("/:id/games/:gameId") {
    val gameId = params("gameId").toInt
    auth()
    if (user.role != "admin" && requestGuild.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")
    val gameRepo = inject[GenericMRepo[Game]]
    gameRepo.get(gameId) match {
      case Some(g: Game) =>
        if (requestGuild.games.contains(g))
          guildRepo.update(requestGuild)
        else
          BadRequest("Team does not play this game.")
      case None =>
        BadRequest("Game with that Id does not exist.")
    }
  }
}
