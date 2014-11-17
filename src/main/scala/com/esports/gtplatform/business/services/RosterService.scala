package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.{TournamentRepo, TournamentUserRepo, TeamUserRepo, TeamRepo}
import models.{TeamUser, TournamentUser}

/**
 * Created by Matthew on 11/17/2014.
 */
class RosterService(val eventService: EventServiceT, val tournamentUserRepo: TournamentUserRepo,val teamUserRepo: TeamUserRepo, val tournamentRepo: TournamentRepo, val teamRepo: TeamRepo) extends RosterServiceT {

    override def canJoin(tu: TeamUser): Boolean = {
        val tour = tournamentRepo.get(tu.team.tournamentId).get

        if(tour.details.isDefined && tour.details.get.teamMaxSize.isDefined)
            !teamUserRepo.getByTournament(tour.id.get).exists(x => x.userId == tu.userId) && tu.team.teamPlayers.size < tour.details.get.teamMaxSize.get
        else
            !teamUserRepo.getByTournament(tour.id.get).exists(x => x.userId == tu.userId)
    }

    override def canJoin(teamUsers: List[TeamUser]): Boolean = {
        val tour = tournamentRepo.get(teamUsers.head.team.tournamentId).get

        if(tour.details.isDefined && tour.details.get.teamMaxSize.isDefined)
            teamUsers.forall(tu => !teamUserRepo.getByTournament(tour.id.get).exists(x => x.userId == tu.userId) && tu.team.teamPlayers.size < tour.details.get.teamMaxSize.get)
        else
            teamUsers.forall(tu => !teamUserRepo.getByTournament(tour.id.get).exists(x => x.userId == tu.userId))
    }

    override def canJoin(tuser: TournamentUser): Boolean = {
        val tour = tournamentRepo.get(tuser.tournamentId).get

        if(tour.details.isDefined && tour.details.get.playerMaxSize.isDefined)
            !tournamentUserRepo.getByTournament(tour).contains(tuser) && tour.details.get.playerMaxSize.get < tour.users.size
        else
            !tournamentUserRepo.getByTournament(tour).contains(tuser)
    }
}
