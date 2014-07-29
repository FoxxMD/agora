package com.esports.gtplatform.business
import com.googlecode.mapperdao._
import com.googlecode.mapperdao.Query._
import com.googlecode.mapperdao.queries.v2.WithQueryInfo
import dao.Daos._

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

trait GenericRepo[T] {

  def get(id: Int) : Option[T]
  def getAll : List[T]
  def update[U <: T with Persisted](obj: U) : T with Persisted
  def create(obj: T) : T
  def delete(id: Int)
  def delete[U <: T with Persisted](obj: U)

  //NEVER USE THESE
  def query[U <: T with Persisted](qi : WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted]
  def querySingle(qi : WithQueryInfo[Int,Persisted, T]): Option[T with Persisted]
}

//This trait is implementing methods because it's a builder for the concrete class below. Think of it as building block for the class rather
//than a repository itself. (You will notice it is not injected in ScalatraBootstrap, only GenericRepo is)
trait GenericMDaoTypedRepo[T] extends GenericRepo[T]{

  val returnEntity: Entity[Int,Persisted,T]

  def get(id: Int) : Option[T with Persisted] = mapperDao.select(returnEntity, id)
  def getAll : List[T with Persisted] = queryDao.query(select from returnEntity)
  def update[U <: T with Persisted](obj: U) : T with Persisted with Persisted = mapperDao.update(returnEntity, obj)
  def create(obj: T) : T with Persisted = mapperDao.insert(returnEntity, obj)
  def delete(id: Int) = mapperDao.delete(returnEntity, id)
  def delete[U <: T with Persisted](obj: U) = mapperDao.delete(returnEntity, obj)

}

class GenericMDaoTypedRepository[T](val returnEntity: Entity[Int,Persisted,T]) extends GenericMDaoTypedRepo[T] {

  def query[U <: T with Persisted](qi : WithQueryInfo[Int, T with Persisted, T]): List[T with Persisted] = queryDao.query(qi)
  def querySingle(qi : WithQueryInfo[Int,Persisted, T]): Option[T with Persisted] = queryDao.querySingleResult(qi)

}