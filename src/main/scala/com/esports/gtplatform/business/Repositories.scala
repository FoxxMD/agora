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

    def create(entity: T) = table.insert(entity)

    def update(entity: T) = {
        table.update(entity)
        entity
    }

    def getAll: List[T] = from(table)(a => select(a) orderBy(a.id.get asc)).toList

    def delete(entity: T) = table.delete(entity.id.get)

    def findById(id: Int): Option[T] = {
        try{
            Some(table.where(f => f.id.get === id).single)
        }catch{
            case e: Exception => return None
        }
    }

    override def get(id: Int): Option[T] = table.lookup(id)

    override def delete(id: Int): Unit = table.delete(id)

    override def getPaginated(pageNo: Int, pageSize: Int): List[T] = from(table)(a => select(a) orderBy(a.id asc)).page(pageNo, pageSize).toList
}

class GameRepository extends GenericSquerylRepository[Game](games) with GameRepo {

    override def getByName(name: String) = from(games)(g => where(g.name === name) select g).singleOption
}
class GameTTLinkRepository extends GenericSquerylRepository[GameTournamentType](gamettLink) with GameTTLinkRepo{

}
class WebTokenRepository extends GenericSquerylRepository[WebToken](webTokens) with WebTokenRepo {
    override def getByToken(token: String): Option[WebToken] = webTokens.where(w => w.token === token).singleOption
}
class ApiKeyRepository extends GenericSquerylRepository[ApiKey](apiKeys) with ApiKeyRepo
class UserRepository extends GenericSquerylRepository[User](users) with UserRepo{

    override def getByEmail(email: String): Option[User] = users.where(u => u.email === email).singleOption

    override def getByHandle(handle: String): Option[User] = users.where(u => u.globalHandle === handle).singleOption
}
class UserIdentityRepository extends GenericSquerylRepository[UserIdentity](userIdents) with UserIdentityRepo{

    override def getByUser(user: User): List[UserIdentity] = getByUser(user.id.get)

    override def getByUser(id: Int): List[UserIdentity] = userIdents.where(u => u.userId === id).toList

    override def getByUserPass(email: String): Option[UserIdentity] = userIdents.where(u => u.email === email).singleOption
}
