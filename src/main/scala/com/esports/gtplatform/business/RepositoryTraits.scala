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
}

trait GuildRepo extends GenericEntityRepo[Guild]
trait GameRepo extends GenericEntityRepo[Game]
trait EventRepo extends GenericEntityRepo[Event]
trait TournamentRepo extends GenericEntityRepo[Tournament]
trait EventUserRepo extends GenericUserLinkRepo[EventUser]
trait TeamUserRepo extends GenericUserLinkRepo[TeamUser]
trait TournamentUserRepo extends GenericUserLinkRepo[TournamentUser]
trait NonActiveUserIdentityRepo extends UserIdentityRepo
trait NonActiveUserRepo extends UserRepo


