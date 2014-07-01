package dao

import play.db._
import com.googlecode.mapperdao.utils.Setup
import com.googlecode.mapperdao.jdbc.Transaction
import com.googlecode.mapperdao.utils.{SurrogateIntIdAll, TransactionalSurrogateIntIdCRUD}
import com.googlecode.mapperdao.{NoId, QueryDao, MapperDao}
import models._

object Daos {
  val dataSource = play.db.DB.getDataSource("default")
/*  val (jbdc, mapperDao, queryDao, txManager) = Setup.mysql(dataSource, List(UserEntity, UserDetailsEntity, TeamEntity, TeamUserEntity,
    GameEntity, TournamentEntity, TournamentDetailsEntity, TournamentTeamEntity, TournamentUserEntity, EventEntity,
  EventUserEntity, EventDetailsEntity))*/
val (jbdc, mapperDao, queryDao, txManager) = Setup.mysql(dataSource, List(UserEntity, TeamEntity, TeamUserEntity,
  GameEntity, TournamentEntity, TournamentTeamEntity, TournamentUserEntity, EventEntity,
  EventUserEntity))

  //val txManager = Transaction.transactionManager(jbdc)

  val userDao = new UserDao {
    val entity = UserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

/*  val userDetailsDao = new UserDetailsDao {
    val entity = UserDetailsEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }*/

  val teamDao = new TeamDao {
    val entity = TeamEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val teamUserDao = new TeamUserDao {
    val entity = TeamUserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val gameDao = new GameDao {
    val entity = GameEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val tournamentDao = new TournamentDao {
    val entity = TournamentEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

/*  val tournamentDetailsDao = new TournamentDetailsDao {
    val entity = TournamentDetailsEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }*/

  val tournamentUserDao = new TournamentUserDao {
    val entity = TournamentUserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val tournamentTeamDao = new TournamentTeamDao {
    val entity = TournamentTeamEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val eventDao = new EventDao {
    val entity = EventEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

/*  val eventDetailsDao = new EventDetailsDao {
    val entity = EventDetailsEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }*/

  val eventUserDao = new EventUserDao {
    val entity = EventUserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }
}

abstract class UserDao extends TransactionalSurrogateIntIdCRUD[User] with SurrogateIntIdAll[User]
//abstract class UserDetailsDao extends TransactionalSurrogateIntIdCRUD[UserDetails] with NoId[UserDetail]
abstract class TeamDao extends TransactionalSurrogateIntIdCRUD[Team] with SurrogateIntIdAll[Team]
abstract class TeamUserDao extends TransactionalSurrogateIntIdCRUD[TeamUser] with SurrogateIntIdAll[TeamUser]
abstract class GameDao extends TransactionalSurrogateIntIdCRUD[Game] with SurrogateIntIdAll[Game]
abstract class TournamentDao extends TransactionalSurrogateIntIdCRUD[Tournament] with SurrogateIntIdAll[Tournament]
//abstract class TournamentDetailsDao extends TransactionalSurrogateIntIdCRUD[TournamentDetails] with SurrogateIntIdAll[TournamentDetails]
abstract class TournamentUserDao extends TransactionalSurrogateIntIdCRUD[TournamentUser] with SurrogateIntIdAll[TournamentUser]
abstract class TournamentTeamDao extends TransactionalSurrogateIntIdCRUD[TournamentTeam] with SurrogateIntIdAll[TournamentTeam]
abstract class EventDao extends TransactionalSurrogateIntIdCRUD[Event] with SurrogateIntIdAll[Event]
//abstract class EventDetailsDao extends TransactionalSurrogateIntIdCRUD[EventDetails] with SurrogateIntIdAll[EventDetails]
abstract class EventUserDao extends TransactionalSurrogateIntIdCRUD[EventUser] with SurrogateIntIdAll[EventUser]
