package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericMRepo
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.scalatra.{BadRequest, Forbidden, Ok}

/**
 * Created by Matthew on 7/29/2014.
 */
class TeamController(implicit val bindingModule: BindingModule) extends APIController with TeamControllerT {
  get("/?") {
    val teams = teamRepo.getPaginated(params.getOrElse("page", "1").toLong)
    Ok(teams)
  }
  post("/") {
   auth()
    val newteam = parsedBody.extract[Team]
    val teamUserRepo = inject[GenericMRepo[TeamUser]]
    var userFT: Option[User] = None

    if(teamRepo.getByName(newteam.name).isDefined)
      halt(400,"Team name is already in use!")

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
      val insertedTeam = teamRepo.create(newteam)
      val tu = TeamUser(insertedTeam, userFT.get, isCaptain = true)
      teamUserRepo.create(tu)
      insertedTeam.id
    }
    Ok(tid)
  }
 /*
  * Team Functionality
  *
  */
  get("/:id") {
    Ok(requestTeam.get)
  }
  post("/:id") {
    auth()
    if(requestTeam.get.getCaptain == user || user.role == "admin") {
      teamRepo.update(requestTeam.get, requestTeam.get.copy(name = parsedBody.\("name").extract[String]))
      Ok()
    }
    else
      Forbidden("You don't have rights to modify this team.")
  }
  delete("/:id") {
    auth()
    if(requestTeam.get.getCaptain == user || user.role == "admin") {
      teamRepo.delete(requestTeam.get)
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
    Ok(requestTeam.get.teamPlayers)
  }
  post("/:id/members") {
    val addingUser = parsedBody.\("userId").extractOrElse[Int](halt(401, "User Id parameter is missing"))
    auth()
    if (user.role == "admin" || requestTeam.get.getCaptain == user) {
      val userRepo = inject[GenericMRepo[User]]
      userRepo.get(addingUser) match {
        case Some(u: User with Persisted) =>
          if(!requestTeam.get.teamPlayers.exists(x => x.user == u)) {
            teamRepo.update(requestTeam.get, requestTeam.get.addUser(u))
            Ok()
          }
          else
            BadRequest("User is already on this Team.")
        case None => BadRequest("No user with that Id exists.")
      }
    }
    else {
      Forbidden("You don't have rights to modify this team.")
    }
  }
  delete("/:id/members") {
    val removingUser = parsedBody.\("userId").extractOrElse[Int](halt(400, "User Id parameter is missing"))
    auth()
    if (user.role == "admin" || requestTeam.get.getCaptain == user) {
      val userRepo = inject[GenericMRepo[User]]
      userRepo.get(removingUser) match {
        case Some(u: User) =>
          if(requestTeam.get.teamPlayers.exists(x => x.user == u)) {
            teamRepo.update(requestTeam.get, requestTeam.get.removeUser(u))
          }
          else{
            BadRequest("User with that Id is not on this team")
          }
        case None => BadRequest("No user with that Id exists.")
      }
    }
    else {
      Forbidden("You don't have rights to modify this team.")
    }
  }
  /*
   * Game Functionality
   *
   */
  get("/:id/games") {
    Ok(requestTeam.get.games)
  }
  post("/:id/games") {
    val gameId = parsedBody.\("gameId").extractOrElse[Int](halt(400, "Game Id parameter is missing"))
    auth()
    if (user.role == "admin" || requestTeam.get.getCaptain == user) {
      val gameRepo = inject[GenericMRepo[Game]]
      gameRepo.get(gameId) match {
        case Some(g: Game) =>
          if(!requestTeam.get.games.contains(g))
            teamRepo.update(requestTeam.get, requestTeam.get.addGame(g))
          else
            BadRequest("Team already plays this game.")
        case None =>
          BadRequest("Game with that Id does not exist.")
      }
    }
    else
      Forbidden("you don't have rights to modify this team.")
  }
  delete("/:id/games") {
    val gameId = parsedBody.\("gameId").extractOrElse[Int](halt(400, "Game Id parameter is missing"))
    auth()
    if (user.role == "admin" || requestTeam.get.getCaptain == user) {
      val gameRepo = inject[GenericMRepo[Game]]
      gameRepo.get(gameId) match {
        case Some(g: Game) =>
          if(requestTeam.get.games.contains(g))
            teamRepo.update(requestTeam.get, requestTeam.get.removeGame(g))
          else
            BadRequest("Team does not play this game.")
        case None =>
          BadRequest("Game with that Id does not exist.")
      }
    }
    else
      Forbidden("you don't have rights to modify this team.")
  }
}
