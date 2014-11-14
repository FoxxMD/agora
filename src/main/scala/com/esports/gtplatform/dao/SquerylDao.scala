package com.esports.gtplatform.dao

import com.esports.gtplatform.models.{ApiKey, ConfirmationToken, WebToken, DomainEntity}
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

/*    on(table[DomainEntity[_]])(x => declare(
    x.id is (unique, autoIncremented, indexed)
    ))*/

    val webTokens = table[WebToken]("tokens")
    val confirmationTokens = table[ConfirmationToken]
    val apiKeys = table[ApiKey]

    val users = table[User]("users")
    on(users)(u => declare(
    u.id is (unique,autoIncremented,indexed)
    ))

    val userIdents = table[UserIdentity]("users_identity")
    on(userIdents)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

    val userPlatformProfiles = table[UserPlatformProfile]("users_platform_profile")
    on(userPlatformProfiles)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

}
