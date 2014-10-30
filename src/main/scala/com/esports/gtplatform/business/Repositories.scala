package com.esports.gtplatform.business

import com.escalatesoft.subcut.inject.{AutoInjectable, BindingModule, Injectable}
import com.esports.gtplatform.business
import com.esports.gtplatform.dao.slick.CrudComponent.Crud
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.Query._
import com.mysql.jdbc
import com.googlecode.mapperdao.queries.v2.WithQueryInfo
import models.Tournament
import org.slf4j.LoggerFactory
import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.JdbcBackend.{SessionDef, Database}
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import models._
import com.esports.gtplatform.dao.slick._
import scala.slick.lifted.TableQuery
import scala.slick.model.Table
import scala.slick.driver._

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

class GenericSlickRepository[T,E](implicit val bindingModule: BindingModule, implicit val session: JdbcBackend.Session) extends Crud[E,T,Int] with GenericRepo[T] with SqlAccessRepository with Injectable with Profile {
    override val profile = super.profile
    override val query: this.simple.TableQuery[E] = _
    val db = inject[JdbcBackend.Database]

}

class GameRepository extends GenericSlickRepository[Game, Tables.Games] with GameRepo{
    import profile.simple._

    override def get(id: Int): Option[Game] = {
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
    }
}

class UserRepository extends GenericSlickRepository[User, Tables.Users] with UserRepo
{

  def getByEmail(email: String):Option[User] = queryDao.querySingleResult(select from UserEntity where UserEntity.email === email)
  def getByHandle(handle: String): Option[User] = queryDao.querySingleResult(select from UserEntity where UserEntity.globalHandle === handle)
  //def getByEvent(id: Int): List[User] = queryDao.query(select from EventUserEntity where EventUserEntity.event.id === id)
}


class UserIdentityRepository(returnEntity: Entity[Int,Persisted, UserIdentity]) extends GenericMRepository[UserIdentity](returnEntity) with UserIdentityRepo
{
    def getByUser(user: User with Persisted): List[UserIdentity with Persisted] = {
        val uie = UserIdentityEntity
        val ue = UserEntity

       queryDao.query(select from uie join(uie, uie.user, ue) where ue.id === user.id)
    }
}


class GuildRepository(returnEntity: Entity[Int,Persisted, Guild]) extends GenericMRepository[Guild](returnEntity) with GuildRepo{
  def getByName(name: String): Option[Guild] = queryDao.querySingleResult(select from GuildEntity where GuildEntity.name === name)
}



class EventRepository(returnEntity: Entity[Int,Persisted, Event])(implicit val bindingModule: BindingModule) extends GenericMRepository[Event](returnEntity) with EventRepo with Injectable{
    val db = inject[JdbcBackend.DatabaseDef]
    import dao.Tables._
    import dao.Tables.profile.simple._
  def getByName(name: String): Option[Event] = {
      //Events.filter(e => e.name === name).firstOption
      queryDao.querySingleResult(select from EventEntity where EventEntity.name === name)
  }
    override def get(id: Int): Option[Event with Persisted] = {
        val logger = LoggerFactory.getLogger(getClass)
       val a = db.withSession {
            implicit sessions =>
            Events.filter(e => e.id === id).firstOption
        }

       val e = mapperDao.select(returnEntity, id)
        e
    }
}


class EventUserRepository extends GenericMRepository[EventUser](EventUserEntity) with EventUserRepo{
    val eve = EventUserEntity
    val ue = UserEntity
  def getByUser(u: User): List[EventUser with Persisted] = {
    queryDao.query(select from eve join (eve, eve.user, ue) where ue.id === u.id)
  }
}


class TournamentRepository extends GenericMRepository[Tournament](TournamentEntity) with TournamentRepo{
  //def getByTeam(t: Team): List[Tournament] = queryDao.query(select from TournamentEntity where TournamentEntity.teams)
}


class TeamRepository extends GenericMRepository[Team](TeamEntity) with TeamRepo {
    //def getByEvent: List[Team] = queryDao.query(select from TeamEntity where TeamEntity.tournament.)
    def getByGuild(id: Int): List[Team] = {
        val te = TeamEntity
        val ge = GuildEntity
        //queryDao.query(select from te join (te, te.guild, ge) where ge.id === id)
        queryDao.query(select from te where te.guildId === id)
    }

}

class TeamUserRepository extends GenericMRepository[TeamUser](TeamUserEntity) with TeamUserRepo {

    def getByUser(id: Int): List[TeamUser] = {
        val tu = TeamUserEntity
        val ue = UserEntity
        queryDao.query(select from tu join (tu, tu.user, ue) where ue.id === id)
    }
}


class TournamentUserRepository extends GenericMRepository[TournamentUser](TournamentUserEntity) with TournamentUserRepo{
  def getByUser(u: User): List[TournamentUser] = {
      val tu = TournamentUserEntity
      val uentity = UserEntity
      queryDao.query(select from tu join (tu, tu.user, uentity) where uentity.id === u.id)
  }
}

trait NonActiveUserIdentityRepo extends GenericMRepo[UserIdentity]
trait NonActiveUserRepo extends UserRepo

class NonActiveUserIdentityRepository extends GenericMRepository(NonActiveUserIdentityEntity) with NonActiveUserIdentityRepo
class NonActiveUserRepository extends UserRepository(NonActiveUserEntity) with NonActiveUserRepo

