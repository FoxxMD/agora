package com.esports.gtplatform.controllers

import ScalaBrackets.Bracket.ElimTour
import ScalaBrackets.{DoubleElimination, SingleElimination, BracketException, Participant}
import com.esports.gtplatform.business.services.{TournamentServiceT, BracketServiceT}
import com.esports.gtplatform.business.{BracketTypeRepo, BracketRepo, MongoBracketRepo}
import com.esports.gtplatform.models.Bracket
import org.scalatra.{NotImplemented, NoContent, Ok}
import scaldi.Injector

class BracketController(val bracketRepo: BracketRepo, val mongoBracketRepo: MongoBracketRepo, val bracketService: BracketServiceT, val tournamentService: TournamentServiceT, val bracketTypeRepo: BracketTypeRepo)(implicit val inj: Injector) extends BaseController with StandardController with BracketControllerT {

    get("/:id") {
        auth()
        Ok(requestBracket)
    }
    post("/"){
        auth()

        val bracket = parsedBody.extract[Bracket]
        val seedSize = parsedBody.\("seedSize").extractOrElse[Int](halt(400, "Missing seed size!"))

        if(bracket.tournamentId.isEmpty)
            halt(501, "Bracket creation must be made in the context of a tournament! (For now...)")
        if(!tournamentService.hasModeratorPermissions(user, bracket.tournamentId.get))
            halt(401, "You do not have permission to add brackets to this tournament")

        var bid: Option[String] = None
        val bracketType = bracketTypeRepo.get(bracket.bracketTypeId).getOrElse(halt(400, "Not a valid bracket type id!"))
        val order = bracketRepo.getByTournament(bracket.tournamentId.get).size
        if (bracketType.name.toLowerCase.contains("elimination"))
        {
            if(bracketType.name.toLowerCase.contains("single"))
                bid = Option(mongoBracketRepo.create(SingleElimination.generate(seedSize)).id)
            else if(bracketType.name.toLowerCase.contains("double"))
                bid = Option(mongoBracketRepo.create(DoubleElimination.generate(seedSize)).id)
        }
        bracketRepo.create(bracket.copy(bracketId = bid))
        Ok()
    }
    get("/:id/participants") {
        auth()
        if(hasBracketData)
            Ok(requestBracket.data.get.participants)
        else
            NoContent()
    }
    post("/:id/participants"){
        auth()
        if(!bracketService.canModify(user, requestBracket))
            halt(403, "You don't have permission to edit this Bracket")

        if(requestBracket.bracketId.isEmpty)
            halt(400, "Brackets have not been implemented for this type of tournament yet!")
        val origScalaBracket = requestBracket.data.getOrElse[ElimTour](throw new Exception("Can't add a participant to a non-existent bracket!"))

        requestBracket.tournamentId.fold{
            val iParticipant = parsedBody.extract[Participant]
            mongoBracketRepo.update(origScalaBracket.addParticipant(iParticipant))
        }{ t =>
            val participant = bracketService.createParticipant(requestBracket, parsedBody.\("id").extract[Int])
            mongoBracketRepo.update(origScalaBracket.addParticipant(participant))
        }
        Ok()
    }
    patch("/:id/participants/:participantId") {
        auth()
        if(!bracketService.canModify(user, requestBracket))
            halt(403, "You don't have permission to edit this Bracket")

        val origScalaBracket = requestBracket.data.getOrElse[ElimTour](throw new Exception("Can't add a participant to a non-existent bracket!"))

        requestBracket.tournamentId.fold{
            val iParticipant = parsedBody.extract[Participant]
            mongoBracketRepo.update(origScalaBracket.updateParticipant(requestParId,iParticipant))
        }{ t =>
            val participant = bracketService.createParticipant(requestBracket, parsedBody.\("id").extract[Int])
            mongoBracketRepo.update(origScalaBracket.updateParticipant(requestParId, participant))
        }
        Ok()
    }
    delete("/:id/participants/:participantId"){
        auth()
        if(!bracketService.canDelete(user, requestBracket))
            halt(403, "You don't have permission to delete this Bracket")

        val origScalaBracket = requestBracket.data.getOrElse[ElimTour](throw new Exception("Can't add a participant to a non-existent bracket!"))

        try{
            mongoBracketRepo.update(origScalaBracket.removeParticipant(requestParId))
        }
        catch{
            case e: BracketException =>
                halt(400, e.getMessage)
        }
    }
    get("/:id/matches"){
        auth()
        if(hasBracketData)
            Ok(requestBracket.data.get.matches.sortBy(x => x.id))
        else
            NoContent()
    }
    //Updating one score at a time ATM
    patch("/:id/matches/:matchId"){
        auth()
        if(!bracketService.canModify(user, requestBracket))
            halt(403, "You don't have permission to edit this Bracket")

        val origScalaBracket = requestBracket.data.getOrElse[ElimTour](throw new Exception("Can't add a participant to a non-existent bracket!"))
        val participantId = parsedBody.\("participantId").extract[Int]
        val score = parsedBody.\("score").extract[Int]

        mongoBracketRepo.update(origScalaBracket.setScore(requestMatchId, participantId, score))
        Ok()
    }
    get("/:id/winner"){
        NotImplemented()
    }

}
