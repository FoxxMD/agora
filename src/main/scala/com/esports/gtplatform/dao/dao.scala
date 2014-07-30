package dao

import java.util.Properties

import com.googlecode.mapperdao.utils._
import models._
import org.apache.commons.dbcp.BasicDataSourceFactory

/*Here we initialize the DAOs(Data Access Objects) we will use to interact with the database using MapperDao's DSL(domain specific language).
* It's basically a way to write queries/interactions with MySQL without having to write actual MySQL -- you use instead these objects and in application objects to construct queries.
* */
object Daos {

  val properties = new Properties
  properties.load(getClass.getResourceAsStream("/jdbc.mysql.properties")) //get datasource properties
  val dataSource = BasicDataSourceFactory.createDataSource(properties) //dat datasource

//Initialize components of MapperDao DAOs. Setup.mysql is the actual statement for opening a connection and forming objects.
val (jdbc, mapperDao, queryDao, txManager) = Setup.mysql(dataSource, List(UserEntity, TeamEntity, TeamUserEntity,
  GameEntity, TournamentEntity, TournamentTeamEntity, TournamentUserEntity, EventEntity,
  EventUserEntity, UserIdentityEntity)) //All entities must be listed here

  /*Still kind of figuring out how these totally work.
  * But basically you only need to use mapperDao or queryDao in the rest of the Application,
  * then specify the entity and MapperDao knows what Entity you are operating on.
  *
  * Each object is instantiating a new DAO with CRUD capabilities using the schema specified(Int, NaturalId, etc.) sepcified below,
  * then we are providing the implementation for those capabilities with Daos.*
  *
  * Only need one of these if the Entity has a Key(don't use with NoId entities)*/
  val userDao = new UserDao {
    val entity = UserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }

  val userIdentityDao = new UserIdentityDao {
    val entity = UserIdentityEntity
    val queryDao = Daos.queryDao
    val mapperDao = Daos.mapperDao
  }

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

  val eventUserDao = new EventUserDao {
    val entity = EventUserEntity
    val queryDao = Daos.queryDao
    val txManager = Daos.txManager
    val mapperDao = Daos.mapperDao
  }
}

/* Each of these abstract classes creates the DAO infrastructure for interacting with Entities from the DB.

* EX TransactionalSurrogateIntIdCRUD[User] -- Specifies we want to be able to use Transactions, the Id strategy is Surrogate,
* the Id type is Int, we want to implement CRUD(create,update,delete) capabilities, with the domain object User
* */
abstract class UserDao extends TransactionalSurrogateIntIdCRUD[User] with SurrogateIntIdAll[User]
//abstract class UserDetailsDao extends TransactionalSurrogateIntIdCRUD[UserDetails] with NoId[UserDetail]
abstract class UserIdentityDao extends SurrogateIntIdCRUD[UserIdentity] with SurrogateIntIdAll[UserIdentity]
abstract class TeamDao extends TransactionalSurrogateIntIdCRUD[Team] with SurrogateIntIdAll[Team]
abstract class TeamUserDao extends TransactionalSurrogateIntIdCRUD[TeamUser] with SurrogateIntIdAll[TeamUser]
//abstract class GameDao extends TransactionalSurrogateIntIdCRUD[Game] with SurrogateIntIdAll[Game]
abstract class GameDao extends TransactionalSurrogateIntIdCRUD[Game] with SurrogateIntIdAll[Game]
abstract class TournamentDao extends TransactionalSurrogateIntIdCRUD[Tournament] with SurrogateIntIdAll[Tournament]
//abstract class TournamentDetailsDao extends TransactionalSurrogateIntIdCRUD[TournamentDetails] with SurrogateIntIdAll[TournamentDetails]
abstract class TournamentUserDao extends TransactionalSurrogateIntIdCRUD[TournamentUser] with SurrogateIntIdAll[TournamentUser]
abstract class TournamentTeamDao extends TransactionalSurrogateIntIdCRUD[TournamentTeam] with SurrogateIntIdAll[TournamentTeam]
abstract class EventDao extends TransactionalSurrogateIntIdCRUD[Event] with SurrogateIntIdAll[Event]
//abstract class EventDetailsDao extends TransactionalSurrogateIntIdCRUD[EventDetails] with SurrogateIntIdAll[EventDetails]
abstract class EventUserDao extends TransactionalSurrogateIntIdCRUD[EventUser] with SurrogateIntIdAll[EventUser]
