package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericRepo
import models._
import org.scalatra.Ok

/**
 * Created by Matthew on 7/29/2014.
 */
class TeamController(implicit val bindingModule: BindingModule) extends APIController {

  private[this] val teamRepo = inject[GenericRepo[Team]]
  get("/") {
    Ok(teamRepo.getPaginated(params.getOrElse("page", "1").toLong))
  }
  post("/") {
    auth()
    val newteam = parsedBody.extract[Team]
    val teamUserRepo = inject[GenericRepo[TeamUser]]
    var userFT:User = ???
    var tid = 0
    params.get("captainId") match {
      case Some(a:String) =>
        if (user.role == "admin") {
          val personRepo = inject[GenericRepo[User]]
          val thisUser = personRepo.get(a.toInt)
          if (thisUser == None)
            halt(400, "No user found with that Id", reason = "No user with ID REASON")
          userFT = thisUser.get
          //val insertedTeam = teamRepo.create(newteam)
          //tid = insertedTeam.id
          //val tu = TeamUser(insertedTeam, thisUser.head, isCaptain = true)
          //teamUserRepo.create(tu)
        } else
          halt(401)
      case None =>
        userFT = user
        //val insertedTeam = teamRepo.create(newteam)
        //tid = insertedTeam.id
        //val tu = TeamUser(insertedTeam, user, isCaptain = true)
        //teamUserRepo.create(tu)
    }
    val insertedTeam = teamRepo.create(newteam)
    tid = insertedTeam.id
    val tu = TeamUser(insertedTeam, userFT, isCaptain = true)
    teamUserRepo.create(tu)
    Ok(teamRepo.get(tid).get)
  }
}
