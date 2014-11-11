package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.services.{RosterServiceT, TeamServiceT}
import com.esports.gtplatform.business.{TeamUserRepo, TeamRepo}
import com.esports.gtplatform.models.Team
import models.TeamUser
import org.scalatra.{BadRequest, Ok, UrlGeneratorSupport}

/**
 * Created by Matthew on 11/5/2014.
 */
class TeamController(val teamRepo: TeamRepo, val teamUserRepo: TeamUserRepo, val teamService: TeamServiceT, val rosterService: RosterServiceT) extends TeamT with UrlGeneratorSupport {
    get("/") {
        params.get("tournament") match {
            case Some(p: String) =>
                Ok(teamRepo.getByTournament(p.toInt))
            case None =>
                BadRequest("No tournament Id specified.")
        }
    }
    get("/:id") {
        Ok(requestTeam)
    }

    post("/") {
        auth()
        val extractedTeam = parsedBody.extract[Team]
        //TODO Validate guildonly has guildId
        //TODO Validate tournamentId
        if (!teamService.isUnique(extractedTeam) || !teamService.canCreate(user, extractedTeam))
            halt(403, "This team is not unique or you do not have permission to create teams for this tournament.")

        val newTeam = teamRepo.create(extractedTeam)
        var extractedPlayers = parsedBody.\("teamPlayers").extract[List[TeamUser]]
        //TODO Validate userIds
        if (!extractedPlayers.exists(x => x.userId == user.id.get)) {
            extractedPlayers = extractedPlayers.::(TeamUser(teamId = newTeam.id, userId = user.id.get, isCaptain = true))
        }
        if (!rosterService.canJoin(extractedPlayers)) {
            halt(400, "One of more users for this team is already participating in this tournament.")
            logger.warn("[Tournament] (" + newTeam.tournamentId + ") Attempted Team creation with existing users in roster")
        }

        for (tu <- extractedPlayers) {
            teamUserRepo.create(tu)
        }

        logger.info("[Tournament] (" + newTeam.tournamentId + ") New Team \"" + newTeam.name + "\" created.")
        Ok()
    }

    patch("/:id") {
        auth()
        if (!teamService.canModify(user, requestTeam))
            halt(403, "You do not have permission to edit this team.")
        teamRepo.update(parsedBody.extract[Team])
        Ok()
    }
    delete("/:id"){
        auth()
        if (!teamService.canDelete(user, requestTeam))
            halt(403, "You do not have permission to delete this team.")
        teamRepo.delete(requestTeam)
        Ok()
    }
    post("/:id/users") {
        auth()
        if (!teamService.canModify(user, requestTeam))
            halt(403, "You do not have permission to edit this team.")

        val newPlayers = parsedBody.extractOpt[TeamUser].fold(parsedBody.extract[List[TeamUser]])(x => List(x))
        //TODO Validate userIds
        if (!rosterService.canJoin(newPlayers)) {
            halt(400, "One of more users for this team is already participating in this tournament.")
            logger.warn("[Team] (" + requestTeam.id.get + ") Attempted adding users existing on roster")
        }

        for (x <- newPlayers) {
            teamUserRepo.create(x)
        }

        Ok()
    }

    delete("/:id/users/:teamUserId") {
        auth()
        if (!teamService.canModify(user, requestTeam))
            halt(403, "You do not have permission to edit this team.")
        //TODO Validate teamuser exists and teamUserId is an Int
        teamUserRepo.delete(params("teamUserId").toInt)
        Ok()
    }


}
