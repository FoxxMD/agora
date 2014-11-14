package com.esports.gtplatform.dao

import com.esports.gtplatform.models._
import org.squeryl.dsl._
import org.joda.time._
import models._
import org.squeryl.{KeyedEntityDef, PrimitiveTypeMode, Schema}



/**
 * Created by Matthew on 11/13/2014.
 */
/*
trait OptionallyKeyedEntity[K] extends KeyedEntity[K] {
    override def id: Option[K]
}*/

object Squreyl extends PrimitiveTypeMode {

/*   implicit object gameKED extends KeyedEntityDef[Game, Int] {
       def getId(g: Game) = g.id.get
       def isPersisted(g: Game) = g.id.isDefined
       def idPropertyName = "id"
    }*/
    implicit object genericKED extends KeyedEntityDef[DomainEntity[_], Int] {
        def getId(g: DomainEntity[_]) = g.id.get
        def isPersisted(g: DomainEntity[_]) = g.id.isDefined && g.id.get != 0
        def idPropertyName = "id"
    }

    implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Int, DateTime, TInt](intTEF, this) {

        /**
         * Here we implement functions fo convert to and from the native JDBC type
         */

        def convertFromJdbc(t: Int) = new DateTime(t)
        def convertToJdbc(t: DateTime) = (t.getMillis/1000).toInt
    }

    /**
     * We define this one here to allow working with Option of our new type, this allso
     * allows the 'nvl' function to work
     */
    implicit val optionJodaTimeTEF =
        new TypedExpressionFactory[Option[DateTime], TOptionInt]
            with DeOptionizer[Int, DateTime, TInt, Option[DateTime], TOptionInt] {

            val deOptionizer = jodaTimeTEF
        }

    /**
     * the following are necessary for the AST lifting
     */
    implicit def jodaTimeToTE(s: DateTime) = jodaTimeTEF.create(s)

    implicit def optionJodaTimeToTE(s: Option[DateTime]) = optionJodaTimeTEF.create(s)

}
import com.esports.gtplatform.dao.Squreyl._
object SquerylDao extends Schema {


    val games = table[Game]("games")
    on(games)(g => declare(
    g.id is (unique,autoIncremented,indexed)
    ))

    val gamettLink = table[GameTournamentType]("games_tournaments_types")
    on(gamettLink)(g => declare(
    g.id is autoIncremented
    ))

    val tournamentTypes = table[TournamentType]("tournaments_types")
    on(tournamentTypes)(t => declare(
    t.id is autoIncremented
    ))

    val gameTournamentRelation =
    manyToManyRelation(games, tournamentTypes).
    via[GameTournamentType]((g,tt,link) => (link.gameId === g.id, tt.id === link.tournamentTypeId))

    val users = table[User]("users")
    on(users)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

    val userIdents = table[UserIdentity]("users_identity")
    on(userIdents)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

    val userToIdents =
    oneToManyRelation(users, userIdents).
    via((u,i) => u.id === i.userId)

    val userPlatformProfiles = table[UserPlatformProfile]("users_platform_profile")
    on(userPlatformProfiles)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

    val userToPlatforms =
    oneToManyRelation(users, userPlatformProfiles).
    via((u,p) => u.id === p.userId)

    val tournaments = table[Tournament]("tournaments")
    on(tournaments)(t => declare(
    t.id is (unique, autoIncremented, indexed)
    ))

    val tournamentDetails = table[TournamentDetail]("tournaments_details")

    val tournamentUsers = table[TournamentUser]("tournaments_users")
    on(tournamentUsers)(tu => declare(
    tu.id is (unique, autoIncremented, indexed)
    ))

    val tournamentToTournamentUsers =
    manyToManyRelation(tournaments, users).
    via[TournamentUser]((t,u, tu) => (tu.tournamentId === t.id, u.id === tu.userId))

    val guilds = table[Guild]("guilds")
    on(guilds)(g => declare(
    g.id is (unique, autoIncremented, indexed)
    ))

    val guildUsers = table[GuildUser]("guilduser")
    on(guildUsers)(g => declare(
        g.id is (unique, autoIncremented, indexed)
    ))

    val guildGames = table[GuildGame]("guilds_games")
    on(guildGames)(g => declare(
        g.id is (unique, autoIncremented, indexed)
    ))

    val guildToUsers =
    manyToManyRelation(guilds, users).
    via[GuildUser]((g,u,gu) => (gu.guildId === g.id, u.id === gu.userId))

    val guildToGames =
    manyToManyRelation(guilds, games).
    via[GuildGame]((gu,ga,link) => (link.guildId === gu.id, ga.id === link.gameId))

    val teams = table[Team]("teams")
    on(teams)(t => declare(
    t.id is (unique, autoIncremented, indexed)
    ))

    val teamUsers = table[TeamUser]("teams_users")
    on(teamUsers)(tu => declare(
    tu.id is (unique, autoIncremented, indexed)
    ))

    val tournamentToTeams =
    oneToManyRelation(tournaments, teams).
    via((tu,te) => tu.id === te.tournamentId)

    val teamToUsers =
    manyToManyRelation(teams, users).
    via[TeamUser]((t,u,link) => (link.teamId === t.id, u.id === link.userId))

    val events = table[Event]("events")
    on(events)(e => declare(
    e.id is (unique, autoIncremented, indexed)
    ))

    val eventDetails = table[EventDetail]("events_details")

    val eventPayments = table[EventPayment]("events_payments")
    on(eventPayments)(ep => declare(
    ep.id is (unique, autoIncremented, indexed)
    ))

    val eventUsers = table[EventUser]("events_users")
    on(eventUsers)(eu => declare(
    eu.id is (unique, autoIncremented, indexed)
    ))

    val eventToPayments =
    oneToManyRelation(events, eventPayments).
    via((e,ep) => e.id === ep.eventsId)

    val eventToTournaments =
    oneToManyRelation(events, tournaments).
    via((e,t) => e.id === t.eventId)

    val eventToUsers =
    manyToManyRelation(events, users).
    via[EventUser]((e,u,eu) => (eu.eventId === e.id, u.id === eu.userId))

    val webTokens = table[WebToken]("tokens")
    on(webTokens)(w => declare(
    w.id is (unique,autoIncremented, indexed)
    ))

    val confirmationTokens = table[ConfirmationToken]("confirmationtokens")
    on(confirmationTokens)(c => declare(
    c.id is (unique, autoIncremented, indexed)
    ))

    val apiKeys = table[ApiKey]("apikeys")
    on(apiKeys)(a => declare(
    a.id is (unique, autoIncremented, indexed)
    ))

    val passwordTokens = table[PasswordToken]("passwordtokens")
    on(passwordTokens)(p => declare(
    p.id is (unique, autoIncremented, indexed)
    ))


}
