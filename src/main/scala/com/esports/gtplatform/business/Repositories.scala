package com.esports.gtplatform.business

import com.googlecode.mapperdao.Query._
import com.googlecode.mapperdao.jdbc.JdbcMap
import com.googlecode.mapperdao.queries.v2.WithQueryInfo
import com.googlecode.mapperdao.{Entity, Persisted, QueryConfig}
import dao.Daos._
import dao.{NonActiveUserEntity, NonActiveUserIdentityEntity, UserEntity}
import models.{User, UserIdentity}

/**
 * Created by Matthew on 7/29/2014.
 */

/* More fun with traits!
*
* Here we are building traits for use with subcut for dependency injection. On the application layer our controllers
* will inject one of the traits below to receive a repository object. The traits below make heavy use of Generic Parameters and upper/lower bounds
* to build a generic repository implemented by MapperDao.
*
* Eventually more specific traits will have to be built for each domain object as they need more specific methods. For right now this covers most everything.
* Remember that in this instance a trait should be a descriptor of a method -- only describing the input and output of the method. Until you get to the
* concrete class implementation they should stay relatively agnostic so that they can be reused by (future) other implementations.
* */

trait SqlAccess {
  def lowLevelQuery(query: String, args: List[Any]): Option[JdbcMap]
  def lowLevelUpdate(query: String, args: List[Any]): Int
}

trait GenericRepo[T] extends SqlAccess {

  def get(id: Int) : Option[T]
  def getPaginated(pageNo: Long, pageSize: Long = 50): List[T]
  def getAll : List[T]
  def update[U <: T with Persisted](obj: U, newObj: T) : T with Persisted
  def updateMutable[U <: T with Persisted](obj: U) : T with Persisted
  def create(obj: T) : T
  def delete(id: Int)
  def delete[U <: T with Persisted](obj: U)

  //NEVER USE THESE
  def query[U <: T with Persisted](qi : WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted]
  def querySingle(qi : WithQueryInfo[Int,Persisted, T]): Option[T with Persisted]
  def queryPaginated[U <: T with Persisted](pageNo: Long, pageSize: Long, qi: WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted]
}

trait GenericAttendingRepo[T] extends GenericRepo[T] {
  def getByEvent(id: Int): List[T]
  def getByTournament(id: Int): List[T]
}

//This trait is implementing methods because it's a builder for the concrete class below. Think of it as building block for the class rather
//than a repository itself. (You will notice it is not injected in ScalatraBootstrap, only GenericRepo is)
trait GenericMRepo[T] extends GenericRepo[T]{

  val returnEntity: Entity[Int,Persisted,T]

  def get(id: Int) : Option[T with Persisted] = mapperDao.select(returnEntity, id)
  def getPaginated(pageNo: Long, pageSize: Long): List[T] = queryDao.query(QueryConfig.pagination(pageNo,pageSize), select from returnEntity)
  def getAll : List[T with Persisted] = queryDao.query(select from returnEntity)
  def update[U <: T with Persisted](obj: U, newObj: T) : T with Persisted with Persisted = mapperDao.update(returnEntity, obj, newObj)
  def updateMutable[U <: T with Persisted](obj: U) : T with Persisted with Persisted = mapperDao.update(returnEntity, obj)
  def create(obj: T) : T with Persisted = mapperDao.insert(returnEntity, obj)
  def delete(id: Int) = mapperDao.delete(returnEntity, id)
  def delete[U <: T with Persisted](obj: U) = { mapperDao.delete(returnEntity,obj)}

}

class SqlAccessRepository extends SqlAccess{
  def lowLevelQuery(query: String, args: List[Any]): Option[JdbcMap] = jdbc.queryForMap(query, args)
  def lowLevelUpdate(query: String, args: List[Any]): Int = jdbc.update(query, args).rowsAffected
}

class GenericMRepository[T](val returnEntity: Entity[Int,Persisted,T]) extends SqlAccessRepository with GenericMRepo[T] {

  def query[U <: T with Persisted](qi : WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted] = queryDao.query(qi)
  def querySingle(qi : WithQueryInfo[Int,Persisted, T]): Option[T with Persisted] = queryDao.querySingleResult(qi)
  def queryPaginated[U <: T with Persisted](pageNo: Long, pageSize: Long, qi: WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted] = queryDao.query(QueryConfig.pagination(pageNo, pageSize), qi)
}

/* We are creating interfaces for specific domain object repositories because we need more specialized queries/views for the data.
 * Best practice is to create generic methods which use only domain objects so that we can switch providers or change
 * implementation of the methods later without having to refactor much.
 *
 * The trick is finding a balance between making the method generic enough that it can provide broad functionality but
 * still perform the action you want. Eventually many of these specific methods will be refactored into an intermediate
 * generic trait that provides functionality for several domain objects. At which point we can factor out the specific
 * implementations in our classes to keep them clean and lean.
 *
 * EX
  * sendInvite(to: Invitable, from: Invitor, type: IType, msg: String) for use with Teams, Users, Events, and Tournaments
*/
trait UserRepo extends GenericMRepo[User] {
  def getByEmail(email: String): Option[User]
  def getByHandle(handle: String): Option[User]
}

class UserRepository(returnEntity: Entity[Int,Persisted, User]) extends GenericMRepository[User](returnEntity) with UserRepo
{

  def getByEmail(email: String):Option[User] = queryDao.querySingleResult(select from UserEntity where UserEntity.email === email)
  def getByHandle(handle: String): Option[User] = queryDao.querySingleResult(select from UserEntity where UserEntity.globalHandle === handle)
  //def getByEvent(id: Int): List[User] = queryDao.query(select from EventUserEntity where EventUserEntity.event.id === id)
}

trait NonActiveUserIdentityRepo extends GenericMRepo[UserIdentity]
trait NonActiveUserRepo extends UserRepo

class NonActiveUserIdentityRepository extends GenericMRepository(NonActiveUserIdentityEntity) with NonActiveUserIdentityRepo
class NonActiveUserRepository extends UserRepository(NonActiveUserEntity) with NonActiveUserRepo

