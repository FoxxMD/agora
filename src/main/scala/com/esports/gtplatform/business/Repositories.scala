package com.esports.gtplatform.business

import com.escalatesoft.subcut.inject.{AutoInjectable, BindingModule, Injectable}
import com.esports.gtplatform.dao.slick.{TablesWithCustomQueries, SchemaTables, Schema}
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Query._
import io.strongtyped.active.slick.models.Identifiable
import io.strongtyped.active.slick.{ActiveSlick, TableQueries, Tables}
import models.Tournament
import org.slf4j.LoggerFactory
import scala.reflect.ClassTag
import scala.slick.driver.JdbcDriver
import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.JdbcBackend.{SessionDef, Database}
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import models._

/**
 * Created by Matthew on 7/29/2014.
 */


trait SqlAccessRepository extends SqlAccess{

  def lowLevelQuery[T](query: String, args: List[Any]): List[T] = Q.queryNA[T](query).list
  def lowLevelUpdate(query: String, args: List[Any]): Unit = Q.updateNA(query).execute
}
/*trait GenericComponent extends CrudComponent{outer: Profile =>
    import simple._

}*/
//scala.slick.profile.RelationalTableComponent.Table[M]
class GenericSlickRepository[M <: Identifiable[M], T <: io.strongtyped.active.slick.Tables#EntityTable](implicit val bindingModule: BindingModule, implicit val session: JdbcBackend#Session) extends GenericRepo[M] with SqlAccessRepository with Injectable with TablesWithCustomQueries {
    this: ActiveSlick with Schema =>


    val entity = new EntityTableQuery[M, T](tag => new T(tag))

    def get(id: Int) = entity.findOptionById(id)
    def getPaginated(pageNo: Int, pageSize: Int = 50) = entity.pagedList((pageNo-1)*pageSize,pageSize)
    def getAll = entity.fetchAll
    def update(obj: M): M = entity.save(obj)
    def create(obj: M): M = {entity.withId(obj,entity.add(obj))}
    def delete(id: Int) = entity.deleteById(id)
    def delete(obj: M) = entity.delete(obj)

    val db = inject[JdbcBackend#Database]

}

class SlickGameRepository extends GenericSlickRepository[Game, SchemaTables.Games] with GameRepo{

    import JdbcDriver.simple._
/*    override def get(id: Int): Option[Game] = {
        db.withSession {
            implicit session =>
                Tables.Games.filter(e => e.id === id).hydrated.headOption
        }

    }
   override def getAll: List[Game] = {
        db.withSession {
            implicit session =>
                Tables.Games.hydrated
        }
    }
    def getByName(name: String): Option[Game] = {
        db.withDynSession {
            Tables.Games.filter(e => e.name === name).hydrated.headOption
        }
    }*/
    override def get(id: Int) = entity.findById(id).withRelationships.headOption
    override def getAll = entity.fetchAll.flatMap(x => x.withRelationships)
    override def create(obj: Game)  = entity.save(obj).withRelationships.head


}

class SlickUserRepository extends GenericSlickRepository[User, SchemaTables.Users] with UserRepo
{
    this: ActiveSlick with Schema =>
    import JdbcDriver.simple._


    override def get(id: Int) = entity.findOptionById(id).flatMap(x => x.withRelationships.headOption)
  def getByEmail(email: String) = entity.filter(_.email === email).firstOption.flatMap(x => x.withRelationships.headOption)
  def getByHandle(handle: String) = entity.filter(_.globalhandle === handle).firstOption.flatMap(x => x.withRelationships.headOption)

  //def getByEvent(id: Int): List[User] = queryDao.query(select from EventUserEntity where EventUserEntity.event.id === id)
}


class SlickUserIdentityRepository extends GenericSlickRepository[UserIdentity, SchemaTables.UsersIdentity] with UserIdentityRepo
{
    this: ActiveSlick with Schema =>
    import JdbcDriver.simple._

    def getByUser(user: User): List[UserIdentity] = {
        entity.filter(_.usersId === user.id.get).list
    }
}


class SlickGuildRepository extends GenericSlickRepository[Guild, SchemaTables.Guilds] with GuildRepo{
    his: ActiveSlick with Schema =>
    import JdbcDriver.simple._

  def getByName(name: String): Option[Guild] = entity.filter(_.name === name).firstOption
}



class SlickEventRepository extends GenericSlickRepository[Event, SchemaTables.Events] with EventRepo{
    his: ActiveSlick with Schema =>
    import JdbcDriver.simple._

  def getByName(name: String): Option[Event] = {
      entity.filter(e => e.name === name).firstOption
  }
}


class SlickEventUserRepository extends GenericSlickRepository[EventUser, SchemaTables.EventsUsers] with EventUserRepo{
    his: ActiveSlick with Schema =>
    import JdbcDriver.simple._

  def getByUser(u: User): List[EventUser] = {
    entity.filter(_.usersId === u.id).list
  }
}


class SlickTournamentRepository extends GenericSlickRepository[Tournament, SchemaTables.Tournaments] with TournamentRepo{
    his: ActiveSlick with Schema =>
    import JdbcDriver.simple._

  def getByName(name: String): Option[Tournament] = ???
}


class SlickTeamRepository extends GenericSlickRepository[Team, SchemaTables.Teams] with TeamRepo {
    his: ActiveSlick with Schema =>
    import JdbcDriver.simple._

    def getByName(name: String): Option[Team] = entity.filter(_.name === name).firstOption
    def getByGuild(id: Int): List[Team] = ???

}

class SlickTeamUserRepository extends GenericSlickRepository[TeamUser, SchemaTables.TeamsUsers] with TeamUserRepo {
    this: ActiveSlick with Schema =>
    import JdbcDriver.simple._

    def getByUser(user: User): List[TeamUser] = {
        entity.filter(_.usersId === user.id).list
    }
}


class SlickTournamentUserRepository extends GenericSlickRepository[TournamentUser, SchemaTables.TournamentsUsers] with TournamentUserRepo{
    this: ActiveSlick with Schema =>
    import JdbcDriver.simple._

  def getByUser(u: User): List[TournamentUser] = {
      entity.filter(_.usersId === u.id).list
  }
}


class SlickNonActiveUserIdentityRepository extends UserIdentityRepository with NonActiveUserIdentityRepo {
    this: ActiveSlick with Schema =>
}
class SlickNonActiveUserRepository extends UserRepository with NonActiveUserRepo {
    this: ActiveSlick with Schema =>
}

