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
    Ok(requestGuild.get)
  }
  post("/:id") {
    auth()
    if (requestGuild.get.getCaptain == user || user.role == "admin") {
      guildRepo.update(requestGuild.get, requestGuild.get.copy(name = parsedBody.\("name").extract[String]))
      Ok()
    }
    else
      Forbidden("You don't have rights to modify this team.")
  }
  delete("/:id") {
    auth()
    if (requestGuild.get.getCaptain == user || user.role == "admin") {
      guildRepo.delete(requestGuild.get)
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
    Ok(requestGuild.get.members)
  }
  post("/:id/members") {
    val addingUser = parsedBody.\("userId").extractOrElse[Int](halt(401, "User Id parameter is missing"))
    auth()
    if (user.role != "admin" && requestGuild.get.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")

    val userRepo = inject[GenericMRepo[User]]
    userRepo.get(addingUser) match {
      case Some(u: User with Persisted) =>
        if (!requestGuild.get.members.exists(x => x.user == u)) {
          guildRepo.update(requestGuild.get, requestGuild.get.addUser(u))
          Ok()
        }
        else
          BadRequest("User is already on this Team.")
      case None => BadRequest("No user with that Id exists.")
    }
  }
  delete("/:id/members/:userId") {
    val removingUser = params("userId").toInt
    auth()
    if (user.role != "admin" && requestGuild.get.getCaptain != user)
      halt(403, "You don't have permission to edit this team")

    val userRepo = inject[GenericMRepo[User]]
    userRepo.get(removingUser) match {
      case Some(u: User) =>
        if (requestGuild.get.members.exists(x => x.user == u)) {
          guildRepo.update(requestGuild.get, requestGuild.get.removeUser(u))
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
    Ok(requestGuild.get.games)
  }
  post("/:id/games") {
    val gameId = parsedBody.\("gameId").extractOrElse[Int](halt(400, "Game Id parameter is missing"))
    auth()
    if (user.role != "admin" && requestGuild.get.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")
      val gameRepo = inject[GenericMRepo[Game]]
      gameRepo.get(gameId) match {
        case Some(g: Game) =>
          if (!requestGuild.get.games.contains(g))
            guildRepo.update(requestGuild.get, requestGuild.get.addGame(g))
          else
            BadRequest("Team already plays this game.")
        case None =>
          BadRequest("Game with that Id does not exist.")
      }
  }
  delete("/:id/games/:gameId") {
    val gameId = params("gameId").toInt
    auth()
    if (user.role != "admin" && requestGuild.get.getCaptain != user)
      halt(403, "You don't have permission to edit this team.")
    val gameRepo = inject[GenericMRepo[Game]]
    gameRepo.get(gameId) match {
      case Some(g: Game) =>
        if (requestGuild.get.games.contains(g))
          guildRepo.update(requestGuild.get, requestGuild.get.removeGame(g))
        else
          BadRequest("Team does not play this game.")
      case None =>
        BadRequest("Game with that Id does not exist.")
    }
  }
}
