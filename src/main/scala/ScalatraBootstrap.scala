import javax.servlet.ServletContext

import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services._
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.data.DatabaseInit
import org.scalatra.LifeCycle

/* This is where we bootstrap the back-end service -- it's basically a "Global" file in any other context. Not much
*  needs to be changed here on a daily basis as it mostly for wiring up controllers and dependency injection.
*
*  */

class ScalatraBootstrap extends LifeCycle with DatabaseInit with scaldi.Module {

    /*
    * Repository bindings
    * */
    bind[UserRepo] to new UserRepository
    bind[WebTokenRepo] to new WebTokenRepository
    bind[ApiKeyRepo] to new ApiKeyRepository
    bind[TransactionSupport] to new SquerylTransaction
    bind[GameRepo] to new GameRepository
    bind[GameTTLinkRepo] to new GameTTLinkRepository
    bind[UserIdentityRepo] to new UserIdentityRepository
    bind[UserPlatformRepo] to new UserPlatformRepository
    bind[GuildRepo] to new GuildRepository
    bind[GuildUserRepo] to new GuildUserRepository
    bind[GuildGameLinkRepo] to new GuildGameRepository

    bind[TournamentRepo] to new TournamentRepository
    //Created a separate set of tables/repositories for non-confirmed users.
    bind[NonActiveUserIdentityRepo] to new NonActiveUserIdentityRepository
    bind[NonActiveUserRepo] to new NonActiveUserRepository
    bind[EventRepo] to new EventRepository
    bind[EventUserRepo] to new EventUserRepository
    bind[EventPaymentRepo] to new EventPaymentRepository
    bind[EventDetailRepo] to new EventDetailsRepository
    bind[TournamentUserRepo] to new TournamentUserRepository
    bind[BracketTypeRepo] to new BracketTypesRepository
    bind[TournamentDetailsRepo] to new TournamentDetailRepository
    bind[TeamRepo] to new TeamRepository
    bind[TeamUserRepo] to new TeamUserRepository
    bind[ConfirmationTokenRepo] to new ConfirmationTokenRepository
    bind[PasswordTokenRepo] to new PasswordTokenRepository
    bind[MongoBracketRepo] to new MongoBracketRepository
    bind[BracketRepo] to new BracketRepository

    /*
    * Service Bindings
    * */
    bind[GuildServiceT] to injected[GuildService]
    bind[RegistrationServiceT] to injected[RegistrationService]
    bind[UserServiceT] to injected[UserService]
    bind[EventServiceT] to injected[EventService]
    bind[AccountServiceT] to injected[AccountService]
    bind[TournamentServiceT] to injected[TournamentService]
    bind[RosterServiceT] to injected[RosterService]
    bind[TeamServiceT] to injected[TeamService]
    bind[BracketServiceT] to inject[BracketService]


    override def init(context: ServletContext) {
        configureDb()

        context.mount(new GameController(
            gameRepo = inject[GameRepo],
            gameTTLinkRepo = inject[GameTTLinkRepo]
        ), "/api/games")


        context.mount(new UserManagementController(
            userRepo = inject [UserRepo],
            userIdentRepo = inject[UserIdentityRepo],
        eventUserRepo = inject[EventUserRepo],
        registrationService = inject[RegistrationServiceT],
        userService = inject[UserServiceT],
        eventService = inject[EventServiceT],
        confirmTokenRepo = inject[ConfirmationTokenRepo],
        accountService = inject[AccountServiceT],
        passwordTokenRepo = inject[PasswordTokenRepo]
        ), "/api/")

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
        mongoBracketRepo = inject[MongoBracketRepo],
            tournamentService = inject[TournamentServiceT],
        rosterService = inject[RosterServiceT]
        )

        context.mount(tourController, "/api/tournaments")

        context.mount(new EventController(
            eventRepo = inject[EventRepo],
            eventUserRepo = inject[EventUserRepo],
            tournamentRepo = inject[TournamentRepo],
            userRepo = inject[UserRepo],
            ttRepo = inject[BracketTypeRepo],
        eventService = inject[EventServiceT],
        eventDetailRepo = inject[EventDetailRepo],
        eventPaymentRepo = inject[EventPaymentRepo]
        ), "/api/events")

        context.mount(new TeamController(
        teamRepo = inject[TeamRepo],
        teamUserRepo = inject[TeamUserRepo],
        teamService = inject[TeamServiceT],
        rosterService = inject[RosterServiceT]
        ), "/api/teams")

        context.mount(new BracketController(
        bracketRepo = inject[BracketRepo],
        mongoBracketRepo = inject[MongoBracketRepo],
        bracketService = inject[BracketServiceT]
        ), "/api/brackets")

    }

    override def destroy(context: ServletContext) {
        closeDbConnection()
    }
}
