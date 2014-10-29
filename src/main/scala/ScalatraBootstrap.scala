import javax.servlet.ServletContext

import com.escalatesoft.subcut.inject.NewBindingModule
import com.esports.gtplatform.business._
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.dao.mapperdao._
import com.esports.gtplatform.data.DatabaseInit
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.{Entity, Persisted}
import com.googlecode.mapperdao.jdbc.{JdbcMap, Transaction}
import com.googlecode.mapperdao.queries.v2.WithQueryInfo
import com.mchange.v2.c3p0.ComboPooledDataSource
import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.JdbcBackend.Database
import Daos._
import dao._
import models._
import org.scalatra.LifeCycle

/* This is where we bootstrap the back-end service -- it's basically a "Global" file in any other context. Not much
*  needs to be changed here on a daily basis as it mostly for wiring up controllers and dependency injection.
*
*  */

class ScalatraBootstrap extends LifeCycle with DatabaseInit {

    //  This object acts as the standard configuration for DI with Subcut.
    object StandardConfiguration extends NewBindingModule(module => {

        /* Each repository Trait that will be used gets wired up here to a concrete implementation(class) that actually does the work.
       *  GenericRepo is the Trait we're injecting, with a Generic parameter for the type of Domain Object being injected.
       *  GenericMDaoTypedRepository is the concrete implementation with Game as the generic parameter -- we also pass the corresponding mapperDao entity so it knows what to do later
       */
        //TODO move database configuration and init into this module
        module.bind[JdbcBackend.DatabaseDef] toSingle Database.forDataSource(cpds)
        module.bind[SqlAccess] toSingle new SqlAccessRepository
        module.bind[GenericMRepo[Game]] toSingle new GenericMRepository[Game](GameEntity)
        module.bind[GameRepo] toSingle new GameRepository(GameEntity)
        module.bind[GamesRowRepo] toSingle new GamesRowRepository()(module)
        module.bind[GenericMRepo[UserIdentity]] toSingle new GenericMRepository[UserIdentity](UserIdentityEntity)
        module.bind[UserIdentityRepo] toSingle new UserIdentityRepository(UserIdentityEntity)
        module.bind[GenericMRepo[Guild]] toSingle new GenericMRepository[Guild](GuildEntity)
        module.bind[GuildRepo] toSingle new GuildRepository(GuildEntity)
        module.bind[GenericMRepo[GuildUser]] toSingle new GenericMRepository[GuildUser](GuildUserEntity)
        module.bind[GenericMRepo[User]] toSingle new GenericMRepository[User](UserEntity)
        module.bind[UserRepo] toSingle new UserRepository(UserEntity)
        module.bind[GenericMRepo[Tournament]] toSingle new GenericMRepository[Tournament](TournamentEntity)
        //Created a separate set of tables/repositories for non-confirmed users.
        module.bind[NonActiveUserIdentityRepo] toSingle new NonActiveUserIdentityRepository
        module.bind[NonActiveUserRepo] toSingle new NonActiveUserRepository
        module.bind[EventRepo] toSingle new EventRepository(EventEntity)(module)
        module.bind[EventUserRepo] toSingle new EventUserRepository
        module.bind[GenericMRepo[EventUser]] toSingle new GenericMRepository[EventUser](EventUserEntity)
        module.bind[GenericMRepo[TournamentUser]] toSingle new GenericMRepository[TournamentUser](TournamentUserEntity)
        module.bind[TournamentUserRepo] toSingle new TournamentUserRepository
        module.bind[GenericMRepo[TournamentType]] toSingle new GenericMRepository[TournamentType](TournamentTypeEntity)
        module.bind[GenericMRepo[Tournament]] toSingle new GenericMRepository[Tournament](TournamentEntity)
        module.bind[GenericMRepo[Team]] toSingle new GenericMRepository[Team](TeamEntity)
        module.bind[TeamUserRepo] toSingle new TeamUserRepository

        module.bind[Transaction] toSingle {
            Transaction.default(Transaction.transactionManager(jdbc))
        }
    }

    )

    //Eventually there will be different configurations depending on environment, such as for testing with mock repos.
    //object TestingConfiguration extends NewBindingModule(module =>)


    override def init(context: ServletContext) {
        configureDb()
        //val db = Database.forDataSource(cpds)

        //Here we assign our Standard Configuration for DI with subcut to the variable that will inject into our controllers
        implicit val bindingModule = StandardConfiguration

        //This is how we mount individual controllers to a route. Each controller's url argument is relative to this path.
        context.mount(new UserManagementController, "/api/")
        context.mount(new GameController, "/api/games")
        context.mount(new GuildController, "/api/guilds")
        context.mount(new UserController, "/api/users")
        context.mount(new EventController, "/api/events")

    }

    override def destroy(context: ServletContext) {
        closeDbConnection()
    }
}
