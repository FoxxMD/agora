package com.esports.gtplatform.business.services

/**
 * Created by Matthew on 8/26/2014.
 */
trait GenericService[T] {
  def isUnique(obj: T): Option[String]
  def create(obj: T): Any
}
