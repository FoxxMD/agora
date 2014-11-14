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
        def isPersisted(g: DomainEntity[_]) = g.id.isDefined
        def idPropertyName = "id"
    }
/*    implicit object gameKED extends KeyedEntityDef[Game, Int] {
        def getId(g: Game) = g.id.get
        def isPersisted(g: Game) = g.id.isDefined
        def idPropertyName = "id"
    }
    implicit object gameTTKED extends KeyedEntityDef[GameTournamentType, Int] {
        def getId(g: GameTournamentType) = g.id.get
        def isPersisted(g: GameTournamentType) = g.id.isDefined
        def idPropertyName = "id"
    }*/

}
import com.esports.gtplatform.dao.Squreyl._
object SquerylDao extends Schema {


    val games = table[Game]("games")
    on(games)(g => declare(
    g.id is (unique,autoIncremented,indexed)
    ))

    val gamettLink = table[GameTournamentType]
    on(gamettLink)(g => declare(
    g.id is autoIncremented
    ))

    val webTokens = table[WebToken]
    val confirmationTokens = table[ConfirmationToken]
    val apiKeys = table[ApiKey]

    val users = table[User]
    on(users)(u => declare(
    u.id is (unique,autoIncremented,indexed)
    ))

    val userIdents = table[UserIdentity]
    on(userIdents)(u => declare(
        u.id is (unique,autoIncremented,indexed)
    ))

}
