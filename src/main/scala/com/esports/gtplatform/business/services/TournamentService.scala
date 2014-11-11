package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.{EventRepo, TournamentUserRepo, TournamentRepo}
import models.{User, Tournament}

/**
 * Created by Matthew on 11/5/2014.
 */
class TournamentService(val tournamentRepo: TournamentRepo, val tournamentUserRepo: TournamentUserRepo, val eventService: EventServiceT) extends TournamentServiceT {
    override def isUnique(obj: Tournament): Boolean = {

        !tournamentRepo.getByEvent(obj.eventId).exists { x =>

            val uniqueId = x.id.get == obj.id.get
            val uniqueName = if (x.details.isDefined && obj.details.get.name.isDefined) {
                val f = for (
                    y <- x.details
                    if y.name.isDefined
                ) yield y.name.get == obj.details.get.name.get
                f.isEmpty || (f.isDefined && f.get)
            } else false
            uniqueId || uniqueName
        }
    }

    def hasAdminPermissions(user: User, obj: Tournament): Boolean = {
        if (obj.id.isDefined) {
            val users = tournamentUserRepo.getByTournament(obj.id.get)
            users.exists(x => x.userId == user.id.get && x.isAdmin) ||
                eventService.hasAdminPermissions(user, obj.eventId)
        }
        else {
            eventService.hasAdminPermissions(user, obj.eventId)
        }

    }

    def hasModeratorPermissions(user: User, obj: Tournament): Boolean = {
        if (obj.id.isDefined) {
            obj.users.exists(x => x.userId == user.id.get && (x.isModerator || x.isAdmin)) ||
                eventService.hasModeratorPermissions(user, obj.eventId)
        }
        else {
            eventService.hasModeratorPermissions(user, obj.eventId)
        }

    }

    def hasAdminPermissions(user: User, id: Int): Boolean = {
        val tourney = tournamentRepo.get(id).get
        val users = tournamentUserRepo.getByTournament(tourney.id.get)
        users.exists(x => x.userId == user.id.get && x.isAdmin) ||
            eventService.hasAdminPermissions(user, tourney.eventId)
    }

    def hasModeratorPermissions(user: User, id: Int): Boolean = {
        val tourney = tournamentRepo.get(id).get
        val users = tournamentUserRepo.getByTournament(tourney.id.get)
        users.exists(x => x.userId == user.id.get && (x.isModerator || x.isAdmin)) ||
            eventService.hasModeratorPermissions(user, tourney.eventId)
    }

    override def canModifyRoster(user: User, obj: Tournament): Boolean = hasModeratorPermissions(user, obj)

    override def canDelete(user: User, obj: Tournament): Boolean = hasAdminPermissions(user, obj)

    override def canRead(user: User, obj: Tournament): Boolean = true

    override def canCreate(user: User, obj: Tournament): Boolean = hasAdminPermissions(user, obj)

    override def canModify(user: User, obj: Tournament): Boolean = hasAdminPermissions(user, obj)
}
