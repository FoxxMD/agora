package models
import org.joda.time.DateTime

object JoinType extends Enumeration {
  type JoinType = Value
  val Invite, Public = Value

  def toString(j: JoinType) = j match {
    case Invite => "I"
    case Public => "P"
  }

  def fromString(j: String): JoinType = j match {
    case "I" => Invite
    case "P" => Public
  }
}

class Event(val id: Int, val name: String, val eventType: JoinType.Value, val details: EventDetails, val users: Set[EventUser],
            val tournaments: Set[Tournament]) {

}

class EventDetails(var event: Event, var address: Option[String], val city: Option[String], val state: Option[String], val description: Option[String], val rules: Option[String], val prizes: Option[String],
                   val streams: Option[String], val servers: Option[String], val timeStart: DateTime, val timeEnd: DateTime)
{

}
