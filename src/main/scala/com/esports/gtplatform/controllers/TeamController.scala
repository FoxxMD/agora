package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericMRepo
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.scalatra.Ok

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
    var userFT:User = user
    params.get("captainId") match {
      case Some(a:String) =>
        if (user.role == "admin") {
          val personRepo = inject[GenericMRepo[User]]
          val thisUser = personRepo.get(a.toInt)
          if (thisUser == None)
            halt(400, "No user found with that Id")
          userFT = thisUser.get
        } else
          halt(401)
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
}
