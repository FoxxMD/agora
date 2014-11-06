package com.esports.gtplatform.business

import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.JdbcMap
import models._

/**
 * Created by Matthew on 10/30/2014.
 */
trait SqlAccess {

    def lowLevelQuery[T](query: String, args: List[Any]): List[T]
    def lowLevelUpdate(query: String, args: List[Any]): Unit
}

trait GenericRepo[T] extends SqlAccess {

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
}
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
}
trait EventPaymentRepo extends GenericRepo[EventPayment]{
    def getByEvent(id: Int): List[EventPayment]
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


