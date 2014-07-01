package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
abstract class ActivityDetails(val description: Option[String], val rules: Option[String], val prizes: Option[String],
                      val streams: Option[String], servers: Option[String], val timeStart: DateTime, val timeEnd: DateTime) {

}
