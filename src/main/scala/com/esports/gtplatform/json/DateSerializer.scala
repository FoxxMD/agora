package com.esports.gtplatform.json

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s._

class DateSerializer extends CustomSerializer[DateTime](format => (
  {
    case JString(s) =>
      ISODateTimeFormat.dateTime().parseDateTime(s)
  },
  {
    case x: DateTime =>
      JString(x.toDateTimeISO.toString)
  }
))
