package com.esports.gtplatform.json

import java.util.TimeZone

import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.ISODateTimeFormat
import org.json4s._

class DateSerializer extends CustomSerializer[DateTime](format => (
  {
    case JString(s) =>
      ISODateTimeFormat.dateTime().parseDateTime(s).toDateTime(DateTimeZone.UTC)
  },
  {
    case x: DateTime =>
      JString(x.toDateTimeISO.toString)
  }
))
