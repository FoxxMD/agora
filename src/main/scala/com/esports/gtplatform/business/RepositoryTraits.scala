package com.esports.gtplatform.business

import com.esports.gtplatform.models._
import models._

/**
 * Created by Matthew on 10/30/2014.
 */
trait TransactionSupport {
    def transaction[A](a: =>A): A
}

trait SqlAccess {

    def lowLevelQuery[T](query: String, args: List[Any]): List[T]
    def lowLevelUpdate(query: String, args: List[Any]): Unit
}

trait GenericRepo[T] {

    def get(id: Int) : Option[T]
    def getPaginated(pageNo: Int, pageSize: Int = 50): List[T]
    def getAll : List[T]
    def update(obj: T): T
    def create(obj: T) : T
    def delete(id: Int): Unit
    def delete(obj: T): Unit

}

/*trait GenericAttendingRepo[T] extends GenericRepo[T] {
    def getByEvent(id: Int): List[T]
    def getByTournament(id: Int): List[T]
}*/

trait GenericEntityRepo[T] extends GenericRepo[T]{
    def getByName(name: String): Option[T]
}
trait GenericUserLinkRepo[T] extends GenericRepo[T]{
    def getByUser(u: User): List[T]
}
trait UserRepo extends GenericRepo[User] {
    def getByEmail(email: String): Option[User]
    def getByHandle(handle: String): Option[User]
}
trait UserIdentityRepo extends GenericRepo[UserIdentity] {
    def getByUser(user: User): List[UserIdentity]
    def getByUser(id: Int): List[UserIdentity]
    def getByUserPass(email: String): Option[UserIdentity]
}
trait UserPlatformRepo extends GenericRepo[UserPlatformProfile]
trait TeamRepo extends GenericEntityRepo[Team]
{
    //def getByEvent: List[Team]
    def getByGuild(id: Int): List[Team]
    def getByTournament(id: Int): List[Team]
}

trait GuildRepo extends GenericEntityRepo[Guild]
trait GuildGameLinkRepo extends GenericRepo[GuildGame]
trait GuildUserRepo extends GenericEntityRepo[GuildUser] {
    def getByGuild(id: Int): List[GuildUser]
}
trait GameRepo extends GenericEntityRepo[Game]
trait GameTTLinkRepo extends GenericRepo[GameTournamentType]
trait EventRepo extends GenericEntityRepo[Event]
trait EventDetailRepo extends GenericEntityRepo[EventDetail]
trait TournamentRepo extends GenericEntityRepo[Tournament] {
    def getByEvent(id: Int): List[Tournament]
}
trait TournamentDetailsRepo extends GenericRepo[TournamentDetail] {
    def getByTournament(id: Int): TournamentDetail
    def getByTournament(e: Event): TournamentDetail
}
trait EventUserRepo extends GenericUserLinkRepo[EventUser] {
    def getByEvent(id: Int): List[EventUser]
    def getByEventAndUser(eventId: Int, userId: Int): Option[EventUser]
}
trait EventPaymentRepo extends GenericRepo[EventPayment]{
    def getByEvent(id: Int): List[EventPayment]
    def getBySecret(key: String): List[EventPayment]
}
trait TeamUserRepo extends GenericUserLinkRepo[TeamUser] {
    def getByTournament(id: Int): List[TeamUser]
    def getByEvent(id: Int): List[TeamUser]
}
trait TournamentUserRepo extends GenericUserLinkRepo[TournamentUser] {
    def getByTournament(id: Int): List[TournamentUser]
    def getByTournament(tournament: Tournament): List[TournamentUser]
}
trait TournamentTypeRepo extends GenericRepo[TournamentType]
trait NonActiveUserIdentityRepo extends UserIdentityRepo
trait NonActiveUserRepo extends UserRepo

trait ApiKeyRepo extends GenericRepo[ApiKey]
trait ConfirmationTokenRepo extends GenericRepo[ConfirmationToken] {
    def getByToken(token: String): Option[ConfirmationToken]
    def getByUser(user: User): Option[ConfirmationToken]
}
trait PasswordTokenRepo extends GenericRepo[PasswordToken]{
    def getByToken(token: String): Option[PasswordToken]
}
trait WebTokenRepo extends GenericRepo[WebToken] {
    def getByToken(token: String): Option[WebToken]
    def getByUser(id: Int): Option[WebToken]
}

