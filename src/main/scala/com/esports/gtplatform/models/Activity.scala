package com.esports.gtplatform.models

import org.joda.time.DateTime

/**
 * Created by Matthew on 8/18/2014.
 */

abstract class ActivityT[T] {
  val Author: T
  val Time: DateTime
}

abstract class AppliedActivityT[T, U] extends ActivityT[T] {
  val Receiver: U
}