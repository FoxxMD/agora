package com.esports.gtplatform.business.services

import ScalaBrackets.Participant
import com.esports.gtplatform.business.{GTSerializers, MongoBracketRepo, TeamRepo, UserRepo}
import com.esports.gtplatform.models.Bracket
import models.User
import org.json4s.{DefaultFormats, Formats, Extraction}
import org.json4s.JsonAST.JObject

/**
 * Created by Matthew on 12/17/2014.
 */
class BracketService(val userRepo: UserRepo, val teamRepo: TeamRepo, val tournamentService: TournamentServiceT, val userService: UserServiceT, val mongoBracketRepo: MongoBracketRepo) extends BracketServiceT {


    //def canCreate(user: User, id: Int): Boolean
    override def canDelete(user: User, obj: Bracket): Boolean = hasElevatedAdminPermissions(user, obj)

    //def canModify(user: User, id: Int): Boolean
    override def canCreate(user: User, obj: Bracket): Boolean = hasElevatedAdminPermissions(user, obj)

    //def canRead(user: User, id: Int): Boolean
    override def canModify(user: User, obj: Bracket): Boolean = hasElevatedModPermissions(user, obj)

    override def hasAdminPermissions(user: User, id: Int): Boolean = tournamentService.hasAdminPermissions(user, id)

    override def hasModeratorPermissions(user: User, id: Int): Boolean = tournamentService.hasModeratorPermissions(user, id)

    override def isTeamPlay(obj: Bracket): Boolean = obj.teamPlay

    override def createParticipant(obj: Bracket, id: Int): Participant = {
        implicit val jsonFormats: Formats = DefaultFormats + new com.esports.gtplatform.json.DateSerializer ++ GTSerializers.mapperSerializers
        if(isTeamPlay(obj)){
            val team = teamRepo.get(id).getOrElse(throw new Exception("No team with the Id " + id + " exists"))
            Participant(id, Option(JObject(("name", Extraction.decompose(team.name)))))
        }
        else{
            val user = userRepo.get(id).getOrElse(throw new Exception("No user with the Id " + id + " exists"))
            Participant(user.id.get, Option(JObject(("name", Extraction.decompose(user.globalHandle)))))
        }
    }

    protected def hasElevatedModPermissions(user: User, bracket: Bracket) = {
        (bracket.ownerId.isDefined && user.id == bracket.ownerId || userService.hasModeratorPermissions(user)) ||
        bracket.tournamentId.isDefined && hasModeratorPermissions(user, bracket.tournamentId.get)
    }
    protected def hasElevatedAdminPermissions(user: User, bracket: Bracket) = {
        (bracket.ownerId.isDefined && user.id == bracket.ownerId || userService.hasAdminPermissions(user)) ||
            bracket.tournamentId.isDefined && hasAdminPermissions(user, bracket.tournamentId.get)
    }
}
