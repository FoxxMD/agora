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

trait GenericRepo[T, U] {

    def get(id: U) : Option[T]
    def getPaginated(pageNo: Int, pageSize: Int = 50): List[T]
    def getAll : List[T]
    def update(obj: T): T
    def create(obj: T) : T
    /*
    This one little trick will amaze you. Developer hate it, architects love it!
    The compiler was complaining about type erasure with both delete methods
    but stackoverflow to the rescue
    http://stackoverflow.com/a/3309490/1469797
    */
    def delete(id: => U): Unit
    def delete(obj: T): Unit

}

trait GenericRepoIntId[T] extends GenericRepo[T, Int]

/*trait GenericAttendingRepo[T] extends GenericRepo[T] {
    def getByEvent(id: Int): List[T]
    def getByTournament(id: Int): List[T]
}*/

trait GenericEntityRepo[T] extends GenericRepo[T, Int]{
    def getByName(name: String): Option[T]
}
trait GenericUserLinkRepo[T] extends GenericRepo[T, Int]{
    def getByUser(u: User): List[T]
}
trait UserRepo extends GenericRepoIntId[User] {
    def getByEmail(email: String): Option[User]
    def getByHandle(handle: String): Option[User]
    def getHydrated(id: Int): Option[User]
}
trait UserIdentityRepo extends GenericRepoIntId[UserIdentity] {
    def getByUser(user: User): List[UserIdentity]
    def getByUser(id: Int): List[UserIdentity]
    def getByUserPass(email: String): Option[UserIdentity]
}
trait UserPlatformRepo extends GenericRepoIntId[UserPlatformProfile]
trait TeamRepo extends GenericEntityRepo[Team]
{
    //def getByEvent: List[Team]
    def getByGuild(id: Int): List[Team]
    def getByTournament(id: Int): List[Team]
}

trait GuildRepo extends GenericEntityRepo[Guild]
trait GuildGameLinkRepo extends GenericRepoIntId[GuildGame]
trait GuildUserRepo extends GenericRepoIntId[GuildUser] {
    def getByGuild(id: Int): List[GuildUser]
}
trait GameRepo extends GenericEntityRepo[Game]
trait GameTTLinkRepo extends GenericRepoIntId[GameBracketType]
trait EventRepo extends GenericEntityRepo[Event] {
    def getHydrated(id: Int): Option[Event]
}
trait EventDetailRepo extends GenericRepoIntId[EventDetail]
trait TournamentRepo extends GenericEntityRepo[Tournament] {
    def getByEvent(id: Int): List[Tournament]
}
trait TournamentDetailsRepo extends GenericRepoIntId[TournamentDetail] {
    def getByTournament(id: Int): Option[TournamentDetail]
}
trait EventUserRepo extends GenericUserLinkRepo[EventUser] {
    def getByEvent(id: Int): List[EventUser]
    def getByEventHydrated(id: Int): List[EventUser]
    def getByUserHydrated(id: Int): List[EventUser]
    def getByEventAndUser(eventId: Int, userId: Int): Option[EventUser]
}
trait EventPaymentRepo extends GenericRepoIntId[EventPayment]{
    def getByEvent(id: Int): List[EventPayment]
    def getBySecret(key: String): List[EventPayment]
}
trait TeamUserRepo extends GenericUserLinkRepo[TeamUser] {
    def getByTeam(id: Int): List[TeamUser]
    def getByEvent(id: Int): List[TeamUser]
    def getByTournament(id: Int): List[TeamUser]
}
trait TournamentUserRepo extends GenericUserLinkRepo[TournamentUser] {
    def getByTournament(id: Int): List[TournamentUser]
    def getByTournament(tournament: Tournament): List[TournamentUser]
}
trait TournamentTypeRepo extends GenericRepoIntId[BracketType]

trait NonActiveUserRepo extends GenericRepoIntId[User] {
    def getByEmail(email: String): Option[User]
    def getByHandle(handle: String): Option[User]
}
trait NonActiveUserIdentityRepo extends GenericRepoIntId[UserIdentity] {
    def getByUser(user: User): List[UserIdentity]
    def getByUser(id: Int): List[UserIdentity]
    def getByUserPass(email: String): Option[UserIdentity]
}

trait ApiKeyRepo extends GenericRepoIntId[ApiKey]
trait ConfirmationTokenRepo extends GenericRepoIntId[ConfirmationToken] {
    def getByToken(token: String): Option[ConfirmationToken]
    //def getByUser(user: User): Option[ConfirmationToken]
}
trait PasswordTokenRepo extends GenericRepoIntId[PasswordToken]{
    def getByToken(token: String): Option[PasswordToken]
}
trait WebTokenRepo extends GenericRepoIntId[WebToken] {
    def getByToken(token: String): Option[WebToken]
    def getByUser(id: Int): Option[WebToken]
}

