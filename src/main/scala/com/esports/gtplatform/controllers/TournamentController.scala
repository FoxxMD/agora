package com.esports.gtplatform.controllers

import ScalaBrackets.Bracket.ElimTour
import ScalaBrackets.{Participant, SingleElimination}
import com.esports.gtplatform.business.services.{RosterServiceT, TournamentServiceT}
import com.esports.gtplatform.business._
import models.{Tournament, TournamentDetail, TournamentUser}
import org.json4s
import org.json4s.Extraction
import org.json4s.JsonAST.JObject
import org.scalatra.{NotImplemented, Ok}
import scaldi.Injector

/**
 * Created by Matthew on 11/5/2014.
 */
class TournamentController(val tournamentRepo: TournamentRepo,
                           val tournamentUserRepo: TournamentUserRepo,
                           val tournamentDetailsRepo: TournamentDetailsRepo,
                            val mongoBracketRepo: MongoBracketRepo,
                           val tournamentService: TournamentServiceT,
                           val rosterService: RosterServiceT)(implicit val inj: Injector) extends BaseController with TournamentT {

    /*
    * Collection
    * */
    get("/tournaments") {
        params.get("page") match {
            case Some(p: String) =>
                Ok(tournamentRepo.getPaginated(p.toInt))
            case None =>
                Ok(tournamentRepo.getPaginated(1))
        }
    }
    post("/") {
        auth()
        if (!tournamentService.canCreate(user, requestTournament))
            halt(403, "You do not have permission to create tournaments for this event.")

        val newTournament = tournamentRepo.create(parsedBody.extract[Tournament])
        if (parsedBody.\("details").extractOpt[TournamentDetail].isDefined)
            tournamentDetailsRepo.create(extractDetails(parsedBody.\("details")).copy(tournamentId = newTournament.id))

        logger.info("[Tournament] (" + newTournament.id.get + ") New Tournament created for Event " + newTournament.eventId)
        Ok(newTournament)
    }

    /*
    * Top-level Object
    * */
    get("/:id") {
        /*val jsonTour = Extraction.decompose(requestTournament)
            .replace(List("users"), Extraction.decompose(requestTournament.users))
            .replace(List("teams"), Extraction.decompose(requestTournament.teams))
        Ok(jsonTour)*/
        Ok(requestTournament)
    }
    get("/:id/bracket") {

        if(requestTournament.tournamentType.name.toLowerCase.contains("elimination") && !requestTournament.tournamentType.name.toLowerCase.contains("swiss"))
        {
            val players = if(requestTournament.tournamentType.teamPlay){
                for(x <- requestTournament.teams.take(8)) yield { Participant(x.id.get, Option(JObject(("name", Extraction.decompose(x.name)))))}
            }
            else{
                for(x <- requestTournament.users.take(8)) yield {
                    Participant(x.id.get, Option(JObject(("name", Extraction.decompose(x.user.globalHandle)))))}
            }
            val tour = SingleElimination.generate8.seed(Option(players.toSet))
            val id = mongoBracketRepo.create(tour)
            Ok(tour.outputToJBracket)
        }
        else
            NotImplemented()

    }
    patch("/:id") {
        auth()
        if (!tournamentService.canModify(user, requestTournament)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ")Non-Admin User " + user.id.get + " attempted to modify Tournament.")
            halt(403, "You do not have permission to edit this tournament.")
        }

        tournamentRepo.update(parsedBody.extract[Tournament])
    }
    delete("/:id") {
        if (!tournamentService.canDelete(user, requestTournament)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ")Non-Admin User " + user.id.get + " attempted to delete Tournament.")
            halt(403, "You do not have permission to delete this tournament.")
        }
        tournamentRepo.delete(requestTournament)
        Ok()
    }

    /*
    * Details
    */
    get("/:id/details") {
        Ok(tournamentDetailsRepo.getByTournament(paramId.get))
    }

    patch("/:id/details") {
        if (!tournamentService.canModify(user, requestTournament))
            halt(403, "You do not have permission to edit this tournament.")
        tournamentDetailsRepo.update(extractDetails(parsedBody))
        Ok()
    }

    get("/:id/teams") {
        Ok(requestTournament.teams)
    }

    /*
    * Tournament Users
    * */

    get("/:id/users") {
        Ok(requestTournament.users)
    }

    post("/:id/users") {
        auth()
        //TODO Validate userId
        val extractedUser = parsedBody.extract[TournamentUser]
        if (!rosterService.canJoin(extractedUser)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ") User " + user.id.get + " attempted to join but failed validation.")
            halt(400, "Cannot join tournament. Check that you are not already on the roster and have payed for registration.")
        }

        tournamentUserRepo.create(extractedUser)
        Ok()
    }

    patch("/:id/users/:tuId") {
        auth()
        if (requestTournamentUser.get.userId != user.id.get && tournamentService.canModifyRoster(user, requestTournament)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ") User " + user.id.get + " attempted to modify Tournament User" + requestTournamentUser.get.id.get + " without correct permissions")
            halt(403, "You do not have permission to modify this user.")
        }

        val extractedTU = parsedBody.extractOrElse[TournamentUser](halt(400, "Could not extract Tournament User"))

        if ((extractedTU.isAdmin != requestTournamentUser.get.isAdmin || extractedTU.isModerator != requestTournamentUser.get.isModerator)
            && !tournamentService.hasAdminPermissions(user, requestTournament.id.get)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ") User " + user.id.get + " attempted to change role for Tournament User" + requestTournamentUser.get.id.get + " without correct permissions")
            halt(403, "You do not have permission to edit this user's role")
        }

        tournamentUserRepo.update(extractedTU)
        Ok()

    }

    delete("/:id/users/:tuId"){
        auth()
        if (requestTournamentUser.get.userId != user.id.get && tournamentService.canModifyRoster(user, requestTournament)) {
            logger.warn("[Tournament] (" + requestTournament.id.get + ") User " + user.id.get + " attempted to delete Tournament User" + requestTournamentUser.get.id.get + " without correct permissions")
            halt(403, "You do not have permission to delete this user.")
        }
        tournamentUserRepo.delete(requestTournamentUser.get)
        Ok()
    }


    def extractDetails(obj: json4s.JValue) = {
        obj.extract[TournamentDetail].copy(
            servers = Option(compact(render(obj.\("servers")))),
            rules = Option(compact(render(obj.\("rules")))),
            streams = Option(compact(render(obj.\("streams")))),
            prizes = Option(compact(render(obj.\("prizes")))),
            description = Option(compact(render(obj.\("description"))))
        )
    }

}
