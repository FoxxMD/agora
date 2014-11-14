package com.esports.gtplatform.business

import com.esports.gtplatform.dao.SquerylDao
import com.esports.gtplatform.dao.SquerylDao._
import com.esports.gtplatform.models._
import models._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntityDef, Table}



/**
 * Created by Matthew on 7/29/2014.
 */

class SquerylTransaction extends TransactionSupport {
    def transaction[A](a: =>A): A = inTransaction {
        a
    }
}

class GenericSquerylRepository[T <: DomainEntity[T]](theTable: Table[T]) extends GenericRepo[T] {
    import com.esports.gtplatform.dao.Squreyl._
    def table = theTable

    def create(entity: T) = inTransaction (table.insert(entity))

    def update(entity: T) = {
        inTransaction (table.update(entity))
        entity
    }

    def getAll: List[T] = inTransaction (table.allRows.toList)

    def delete(entity: T) = inTransaction(table.delete(entity.id.get))

    def findById(id: Int): Option[T] = inTransaction(table.where(f => f.id.get === id).singleOption)

    override def get(id: Int): Option[T] = inTransaction {table.lookup(id)}

    override def delete(id: Int): Unit = inTransaction (table.delete(id))

    override def getPaginated(pageNo: Int, pageSize: Int): List[T] = inTransaction (from(table)(a => select(a) orderBy(a.id asc)).page(pageNo, pageSize).toList)
}

class GameRepository extends GenericSquerylRepository[Game](games) with GameRepo {

    override def getByName(name: String) = inTransaction (from(games)(g => where(g.name === name) select g).singleOption)
}
class GameTTLinkRepository extends GenericSquerylRepository[GameTournamentType](gamettLink) with GameTTLinkRepo

class WebTokenRepository extends GenericSquerylRepository[WebToken](webTokens) with WebTokenRepo {
    override def getByToken(token: String): Option[WebToken] = inTransaction (webTokens.where(w => w.token === token).singleOption)
    override def getByUser(id: Int): Option[WebToken] = inTransaction (webTokens.where(w => w.userId === id).singleOption)
}
class ApiKeyRepository extends GenericSquerylRepository[ApiKey](apiKeys) with ApiKeyRepo
class UserRepository extends GenericSquerylRepository[User](users) with UserRepo{

    override def getByEmail(email: String): Option[User] = inTransaction (users.where(u => u.email === email).singleOption)

    override def getByHandle(handle: String): Option[User] = inTransaction (users.where(u => u.globalHandle === handle).singleOption)
}
class UserIdentityRepository extends GenericSquerylRepository[UserIdentity](userIdents) with UserIdentityRepo{

    override def getByUser(user: User): List[UserIdentity] = inTransaction (getByUser(user.id.get))

    override def getByUser(id: Int): List[UserIdentity] = inTransaction (userIdents.where(u => u.userId === id).toList)

    override def getByUserPass(email: String): Option[UserIdentity] = inTransaction (userIdents.where(u => u.email === email).singleOption)
}
class UserPlatformRepository extends GenericSquerylRepository[UserPlatformProfile](userPlatformProfiles) with UserPlatformRepo

class GuildRepository extends GenericSquerylRepository[Guild](guilds) with GuildRepo {

    override def getByName(name: String): Option[Guild] = inTransaction (guilds.where(x => x.name === name).singleOption)
}

class GuildUserRepository extends GenericSquerylRepository[GuildUser](guildUsers) with GuildUserRepo {

    override def getByGuild(id: Int): List[GuildUser] = inTransaction (guildUsers.where(x => x.guildId === id).toList)
}
class GuildGameRepository extends GenericSquerylRepository[GuildGame](guildGames) with GuildGameLinkRepo
