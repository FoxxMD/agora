package com.esports.gtplatform.business.services

import com.esports.gtplatform.business._
import com.esports.gtplatform.models.Team
import models._
import org.slf4j.LoggerFactory

/**
 * Created by Matthew on 9/15/2014.
 */
class TeamService(val teamRepo: TeamRepo,
                  val teamUserRepo: TeamUserRepo,
                  val tournamentService: TournamentServiceT,
                  val eventService: EventServiceT,
                  val tournamentRepo: TournamentRepo,
                  val eventRepo: EventRepo) extends TeamServiceT {

  val logger = LoggerFactory.getLogger(getClass)

    private def isCaptain(user: User, obj: Team): Boolean = {
        teamUserRepo.getByTeam(obj.id.get).exists(x => x.userId == user.id.get && x.isCaptain)
    }

    override def canDelete(user: User, obj: Team): Boolean = {
        val tourney = tournamentRepo.get(obj.tournamentId).get
        isCaptain(user, obj) || tournamentService.canModifyRoster(user, tourney)
    }

    override def canRead(user: User, obj: Team): Boolean = true

    override def canCreate(user: User, obj: Team): Boolean = {
        val tourney = tournamentRepo.get(obj.tournamentId).get
        tournamentService.canModifyRoster(user, tourney) || eventService.hasPaid(user, eventRepo.get(tourney.eventId).get)
    }

    override def canModify(user: User, obj: Team): Boolean = {
        val tourney = tournamentRepo.get(obj.tournamentId).get
        isCaptain(user, obj) || tournamentService.canModifyRoster(user, tourney)
    }

    override def isUnique(obj: Team): Boolean = {
        !teamRepo.getByTournament(obj.tournamentId).exists(x => x.name == obj.name || obj.guildOnly && x.guildId == obj.guildId)
    }
}
