package com.esports.gtplatform.controllers

import com.esports.gtplatform.business.services.{TournamentServiceT, TournamentService}
import com.esports.gtplatform.business.{TournamentDetailsRepo, TournamentUserRepo, TournamentRepo}
import models.{TournamentDetail, Tournament}
import org.json4s
import org.json4s.Extraction
import org.scalatra.{UrlGeneratorSupport, Ok}

/**
 * Created by Matthew on 11/5/2014.
 */
class TournamentController(val tournamentRepo: TournamentRepo, val tournamentUserRepo: TournamentUserRepo, val tournamentDetailsRepo: TournamentDetailsRepo, val tournamentService: TournamentServiceT) extends TournamentT with UrlGeneratorSupport {

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
    post("/"){
        auth()
        if (!tournamentService.canCreate(user, requestTournament))
            halt(403, "You do not have permission to create tournaments for this event.")

        val newTournament = tournamentRepo.create(parsedBody.extract[Tournament])
        if(parsedBody.\("details").extractOpt[TournamentDetail].isDefined)
            tournamentDetailsRepo.create(extractDetails(parsedBody.\("details")).copy(tournamentId = newTournament.id))

        logger.info("[Tournament] (" + newTournament.id.get + ") New Tournament created for Event " + newTournament.eventId)
        Ok(newTournament)
    }

    /*
    * Top-level Object
    * */
    get("/:id") {
        val jsonTour = Extraction.decompose(requestTournament)
            .replace(List("users"), Extraction.decompose(requestTournament.users))
            .replace(List("teams"), Extraction.decompose(requestTournament.teams))
        Ok(jsonTour)
        //Ok(requestTournament)
    }
    patch("/:id") {
        auth()
        if (!tournamentService.canModify(user, requestTournament))
        {
            logger.warn("[Tournament] ("+ requestTournament.id +")Non-Admin User " + user.id + " attempted to modify Tournament.")
            halt(403, "You do not have permission to edit this tournament.")
        }

        tournamentRepo.update(parsedBody.extract[Tournament])
    }
    delete("/:id") {
        if (!tournamentService.canDelete(user, requestTournament))
        {
            logger.warn("[Tournament] ("+ requestTournament.id +")Non-Admin User " + user.id + " attempted to delete Tournament.")
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

    get("/:id/teams/") {
        redirect(url(getTeams))
    }
    val getTeams = get("/:id/teams") {
        Ok(requestTournament.teams)
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
