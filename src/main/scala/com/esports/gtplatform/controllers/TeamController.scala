package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.services.TeamServiceT
import com.esports.gtplatform.business.{TeamUserRepo, TeamRepo}
import com.esports.gtplatform.models.Team
import org.scalatra.{BadRequest, Ok, UrlGeneratorSupport}

/**
 * Created by Matthew on 11/5/2014.
 */
class TeamController(val teamRepo: TeamRepo, val teamUserRepo: TeamUserRepo, val teamService: TeamServiceT) extends TeamT with UrlGeneratorSupport {
    get("/"){
        params.get("tournament") match {
            case Some(p: String) =>
                Ok(teamRepo.getByTournament(p.toInt))
            case None =>
                BadRequest("No tournament Id specified.")
        }
    }
    get("/:id"){
        Ok(requestTeam)
    }

    post("/"){
        auth()
        val newTeam = parsedBody.extract[Team]
        if(!teamService.isUnique(newTeam) || !teamService.canCreate(user, newTeam))
            halt(403,"This team is not unique or you do not have permission to create teams for this tournament.")
    }


}
