package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericMRepo
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.scalatra.{Forbidden, BadRequest, Ok}

/**
 * Created by Matthew on 7/29/2014.
 */
class TeamController(implicit val bindingModule: BindingModule) extends APIController {

  private[this] val teamRepo = inject[GenericMRepo[Team]]
  get("/") {
    val teams = teamRepo.getPaginated(params.getOrElse("page", "1").toLong)
    Ok(teams)
  }
  post("/") {
    auth()
    val newteam = parsedBody.extract[Team]
    val teamUserRepo = inject[GenericMRepo[TeamUser]]
    var userFT: User = user

    params.get("captainId") match {
      case Some(a: String) =>
        if (user.role == "admin") {
          val personRepo = inject[GenericMRepo[User]]
          val thisUser = personRepo.get(a.toInt)
          if (thisUser == None)
            halt(400, "No user found with that Id")
          userFT = thisUser.get
        } else
          halt(401)
      case None =>
    }
    val tx = inject[Transaction]
    val tid = tx { () =>
      val insertedTeam = teamRepo.create(newteam)
      val tu = TeamUser(insertedTeam, userFT, isCaptain = true)
      teamUserRepo.create(tu)
      insertedTeam.id
    }
    Ok(tid)
  }
  get("/:id") {
    val teamId = params.getOrElse("id", halt(401, "Id parameter is missing"))

    teamRepo.get(teamId.toInt) match {
      case Some(t: Team) => Ok(t)
      case None => BadRequest("No team exists with the Id " + teamId)
    }
  }
  post("/:id/addMember") {
    val teamId = params.getOrElse("id", halt(401, "Team Id parameter is missing"))
    val addingUser = parsedBody.\("userId").extractOrElse(halt(401, "User Id parameter is missing"))[String]
    auth()
    teamRepo.get(teamId.toInt) match {
      case Some(t: Team) =>
        if (user.role == "admin" || t.getCaptain == user) {
          val userRepo = inject[GenericMRepo[User]]
          userRepo.get(addingUser.toInt) match {
            case Some(u: User) =>
              val newTP = t.teamPlayers.+:(TeamUser(t, u, isCaptain = false)) //TODO use lensing
              val newt = t.copy(teamPlayers = newTP)
              teamRepo.update(t, newt)
            case None => BadRequest("No user with that Id exists.")
          }
        }
        else {
          Forbidden("You don't have rights to modify this team.")
        }
      case None => BadRequest("No team exists with the Id " + teamId)
    }
  }
  post("/:id/removeMember") {
    val teamId = params.getOrElse("id", halt(401, "Team Id parameter is missing"))
    val addingUser = parsedBody.\("userId").extractOrElse(halt(401, "User Id parameter is missing"))[Int]
    auth()
    teamRepo.get(teamId.toInt) match {
      case Some(t: Team) =>
        if (user.role == "admin" || t.getCaptain == user) {
          if(t.teamPlayers.exists(x => x.user.id == addingUser))
          {
            val newTP = t.teamPlayers.filter(x => x.user.id != addingUser)
            val newt = t.copy(teamPlayers = newTP)
            teamRepo.update(t, newt)
          }
          else{
            BadRequest("User with that Id is not on this team")
          }
        }
        else {
          Forbidden("You don't have rights to modify this team.")
        }
      case None => BadRequest("No team exists with the Id " + teamId)
    }
  }
}
