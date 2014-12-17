package com.esports.gtplatform.business.services

import ScalaBrackets.Participant
import com.esports.gtplatform.business._
import com.esports.gtplatform.models.{Bracket, PasswordToken, Team}
import models._

/**
 * Created by Matthew on 8/26/2014.
 */
trait GenericService[T] {
  def isUnique(obj: T): Boolean
}

trait RegistrationServiceT {
    protected def inactiveUserRepo: NonActiveUserRepo
    protected def inactiveIdentRepo: NonActiveUserIdentityRepo
    protected def userRepo: UserRepo
    protected def userIdentRepo: UserIdentityRepo
    protected def confirmTokenRepo: ConfirmationTokenRepo
    protected def eventUserRepo: EventUserRepo

    def isUniqueEmail(user: User): Boolean
    def isUniqueHandle(user: User): Boolean
    def createInactiveUser(user: User, password: String, eventId: Option[Int] = None): String
    def createActiveUser(user: User, password: String,  eventId: Option[Int] = None): Unit
    def confirmInactiveUser(token: String): Option[Int]
}

trait AuthorizationSupport[T] {
    def canRead(user: User, obj: T): Boolean
    //def canRead(user: User, id: Int): Boolean
    def canModify(user: User, obj: T): Boolean
    //def canModify(user: User, id: Int): Boolean
    def canCreate(user: User, obj: T): Boolean
    //def canCreate(user: User, id: Int): Boolean
    def canDelete(user: User, obj: T): Boolean
    //def canDelete(user: User, id: Int): Boolean
}
trait RoleSupport[T] {
    //def hasAdminPermissions(user: User, obj: T): Boolean
    //def hasModeratorPermissions(user: User, obj: T): Boolean

    def hasAdminPermissions(user: User, id: Int): Boolean
    def hasModeratorPermissions(user: User, id: Int): Boolean
}

trait TournamentServiceT extends GenericService[Tournament] with AuthorizationSupport[Tournament] with RoleSupport[Tournament] {
    protected def tournamentRepo: TournamentRepo
    protected def tournamentUserRepo: TournamentUserRepo
    protected def eventService: EventServiceT

    def canModifyRoster(user: User, obj: Tournament): Boolean
}

trait TeamServiceT extends GenericService[Team] with AuthorizationSupport[Team] {
   protected def teamUserRepo: TeamUserRepo
   protected def teamRepo: TeamRepo
   protected def tournamentService: TournamentServiceT
   protected def eventService: EventServiceT
   protected def tournamentRepo: TournamentRepo
    //protected def isCaptain(user: User, obj: Team): Boolean
}

trait RosterServiceT {
    protected def eventService: EventServiceT
    protected def teamRepo: TeamRepo
    protected def teamUserRepo: TeamUserRepo
    protected def tournamentUserRepo: TournamentUserRepo
    protected def tournamentRepo: TournamentRepo

    def canJoin(tu: TeamUser): Boolean
    def canJoin(teamUsers: List[TeamUser]): Boolean
    def canJoin(tuser: TournamentUser): Boolean
}

trait EventServiceT extends GenericService[Event] with AuthorizationSupport[Event] with RoleSupport[Event] {
    protected def eventRepo: EventRepo
    protected def eventUserRepo: EventUserRepo
    protected def teamRepo: TeamRepo
    protected def tournamentRepo: TournamentRepo
    protected def userService: UserServiceT

    def hasPaid(user: User, event: Event): Boolean
    def getGroups(event: Event): List[Team]
    def canJoin(user: User) = true
}
trait GuildServiceT extends GenericService[Guild] with AuthorizationSupport[Guild] {
    protected def guildRepo: GuildRepo
    protected def guildUserRepo: GuildUserRepo
    protected def userService: UserServiceT

    def canJoin(gu: GuildUser): Boolean
}

trait UserServiceT extends GenericService[User] {
    protected def userRepo: UserRepo

    def hasAdminPermissions(user: User): Boolean
    def hasModeratorPermissions(user: User): Boolean
}

trait AccountServiceT {
    protected def passwordTokenRepo: PasswordTokenRepo
    protected def webTokenRepo: WebTokenRepo
    protected def userIdentRepo: UserIdentityRepo

    def generatePasswordToken(user: User): String
    def resetPassword(password: String, token: Option[PasswordToken] = None, user: Option[User] = None)

    def generateWebToken(ident: UserIdentity): String
}

trait BracketServiceT extends AuthorizationSupport[Bracket] with RoleSupport[Bracket] {
    protected def userRepo: UserRepo
    protected def teamRepo: TeamRepo
    protected def tournamentService: TournamentServiceT
    protected def mongoBracketRepo: MongoBracketRepo
    protected def userService: UserServiceT

    def canRead(user: User, obj: Bracket): Boolean = true
    def isTeamPlay(obj: Bracket): Boolean
    def createParticipant(obj: Bracket, id: Int): Participant
}
