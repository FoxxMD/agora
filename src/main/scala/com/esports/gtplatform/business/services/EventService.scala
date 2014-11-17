package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.{EventRepo, TournamentRepo, TeamRepo, EventUserRepo}
import com.esports.gtplatform.models.Team
import models.{EventUser, Event, User}

/**
 * Created by Matthew on 11/17/2014.
 */
class EventService(val eventUserRepo: EventUserRepo, val teamRepo: TeamRepo, val tournamentRepo: TournamentRepo, val userService: UserServiceT, val eventRepo: EventRepo) extends EventServiceT {

    override def hasPaid(user: User, event: Event): Boolean = eventUserRepo.getByEventAndUser(event.id.get, user.id.get).fold(false)(x => x.hasPaid)

    override def getGroups(event: Event): List[Team] = event.tournaments.flatMap(x => teamRepo.getByTournament(x.id.get))

    override def isUnique(obj: Event): Boolean = eventRepo.getByName(obj.name).isEmpty

    //def canCreate(user: User, id: Int): Boolean
    override def canDelete(user: User, obj: Event): Boolean = hasAdminPermissions(user, obj.id.get)

    override def canRead(user: User, obj: Event): Boolean = true

    //def canModify(user: User, id: Int): Boolean
    override def canCreate(user: User, obj: Event): Boolean = true

    //def canRead(user: User, id: Int): Boolean
    override def canModify(user: User, obj: Event): Boolean = hasAdminPermissions(user, obj.id.get)

    override def hasModeratorPermissions(user: User, id: Int): Boolean = {
       eventUserRepo.getByEventAndUser(id, user.id.get).fold(false){ x =>
           x.isAdmin || x.isModerator
       } || userService.hasModeratorPermissions(user)
    }
    override def hasAdminPermissions(user: User, id: Int): Boolean = {
        eventUserRepo.getByEventAndUser(id, user.id.get).fold(false){ x =>
            x.isAdmin
        } || userService.hasAdminPermissions(user)
    }
}
