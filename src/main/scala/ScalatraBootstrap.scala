import javax.servlet.ServletContext

import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services._
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.data.DatabaseInit
import com.googlecode.mapperdao.jdbc.Transaction
import org.scalatra.LifeCycle

/* This is where we bootstrap the back-end service -- it's basically a "Global" file in any other context. Not much
*  needs to be changed here on a daily basis as it mostly for wiring up controllers and dependency injection.
*
*  */

class ScalatraBootstrap extends LifeCycle with DatabaseInit with scaldi.Module {

    //  This object acts as the standard configuration for DI with Subcut.
/*    object StandardConfiguration extends NewBindingModule(module => {

        /* Each repository Trait that will be used gets wired up here to a concrete implementation(class) that actually does the work.
       */
        //TODO move database configuration and init into this module
        implicit val session = module.bind[JdbcBackend.Session] toSingle Database.forDataSource(cpds).createSession()
        module.bind[JdbcBackend.Database] toSingle Database.forDataSource(cpds)
        module.bind[SqlAccess] toSingle new SqlAccessRepository
        module.bind[GameRepo] toSingle new SlickGameRepository
        module.bind[UserIdentityRepo] toSingle new SlickUserIdentityRepository
        module.bind[GuildRepo] toSingle new SlickGuildRepository
        module.bind[GuildUserRepo] toSingle new GenericMRepository[GuildUser](GuildUserEntity)
        module.bind[UserRepo] toSingle new UserRepository
        module.bind[TournamentRepo] toSingle new GenericMRepository[Tournament](TournamentEntity)
        //Created a separate set of tables/repositories for non-confirmed users.
        module.bind[NonActiveUserIdentityRepo] toSingle new NonActiveUserIdentityRepository
        module.bind[NonActiveUserRepo] toSingle new NonActiveUserRepository
        module.bind[EventRepo] toSingle new EventRepository(EventEntity)(module)
        module.bind[EventUserRepo] toSingle new EventUserRepository
        module.bind[TournamentUserRepo] toSingle new GenericMRepository[TournamentUser](TournamentUserEntity)
        module.bind[TournamentUserRepo] toSingle new TournamentUserRepository
        module.bind[TournamentTypeRepo] toSingle new GenericMRepository[TournamentType](TournamentTypeEntity)
        module.bind[TeamRepo] toSingle new GenericMRepository[Team](TeamEntity)
        module.bind[TeamUserRepo] toSingle new TeamUserRepository

        module.bind[Transaction] toSingle {
            Transaction.default(Transaction.transactionManager(jdbc))
        }
    })*/

    bind[GameRepo] to None
    bind[GameTTLinkRepo] to None
    bind[GameTTLinkRepo] to None
    bind[SqlAccess] to None
    bind[UserIdentityRepo] to None
    bind[GuildRepo] to None
    bind[GuildUserRepo] to None
    bind[GuildGameLinkRepo] to None
    bind[UserRepo] to None
    bind[TournamentRepo] to None
    //Created a separate set of tables/repositories for non-confirmed users.
    bind[NonActiveUserIdentityRepo] to None
    bind[NonActiveUserRepo] to None
    bind[EventRepo] to None
    bind[EventUserRepo] to None
    bind[TournamentUserRepo] to None
    bind[TournamentUserRepo] to None
    bind[TournamentTypeRepo] to None
    bind[TeamRepo] to None
    bind[TeamUserRepo] to None
    bind[Transaction] to None


    override def init(context: ServletContext) {
        configureDb()
        //val db = Database.forDataSource(cpds)

        //Here we assign our Standard Configuration for DI with subcut to the variable that will inject into our controllers
        //implicit val bindingModule = StandardConfiguration

        //This is how we mount individual controllers to a route. Each controller's url argument is relative to this path.
        context.mount(new UserManagementController(
            userRepo = inject [UserRepo],
            userIdentRepo = inject[UserIdentityRepo],
        eventUserRepo = inject[EventUserRepo],
        registrationService = inject[RegistrationService],
        userService = inject[UserServiceT],
        eventService = inject[EventServiceT],
        confirmTokenRepo = inject[ConfirmationTokenRepo],
        accountService = inject[AccountServiceT],
        passwordTokenRepo = inject[PasswordTokenRepo]
        ), "/api/")

        context.mount(new GameController(
            gameRepo = inject[GameRepo],
            gameTTLinkRepo = inject[GameTTLinkRepo]
        ), "/api/games")

        context.mount(new GuildController(
            guildRepo = inject[GuildRepo],
            guildUserRepo = inject[GuildUserRepo],
            userRepo = inject[UserRepo],
        guildService = inject[GuildServiceT],
        guildGameRepo = inject[GuildGameLinkRepo]
        ), "/api/guilds")

        context.mount(new UserController(
            userRepo = inject[UserRepo],
            userIdentRepo = inject[UserIdentityRepo],
        userPlatformRepo = inject[UserPlatformRepo]
            ), "/api/users")

        val tourController = new TournamentController(
            tournamentRepo = inject[TournamentRepo],
            tournamentUserRepo = inject[TournamentUserRepo],
            tournamentDetailsRepo = inject[TournamentDetailsRepo],
            tournamentService = inject[TournamentServiceT],
        rosterService = inject[RosterServiceT]
        )

        context.mount(tourController, "/api/tournaments")

        context.mount(new EventController(
            eventRepo = inject[EventRepo],
            eventUserRepo = inject[EventUserRepo],
            tournamentRepo = inject[TournamentRepo],
            userRepo = inject[UserRepo],
            ttRepo = inject[TournamentTypeRepo],
        eventService = inject[EventServiceT],
        eventDetailRepo = inject[EventDetailRepo],
        eventPaymentRepo = inject[EventPaymentRepo]
        ), "/api/events")

    }

    override def destroy(context: ServletContext) {
        closeDbConnection()
    }
}
