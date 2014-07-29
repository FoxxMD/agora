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

class Event(val name: String, val eventType: JoinType.Value, val details: EventDetails, val users: Set[EventUser],
            val tournaments: Set[Tournament],val id: Int = 0) {

}

/* Rules and prizes will work very similar to the old GameFest -- they will be stored as JSON strings in the DB
 *
 * I would like to have description be Markdown, but will need to implement a system where the DB stores both the raw Markdown and a copy of the formatted HTML
 * so it doesn't have to be rendered on every page load.
 * */
class EventDetails(var event: Event, var address: Option[String], val city: Option[String], val state: Option[String], val description: Option[String], val rules: Option[String], val prizes: Option[String],
                   val streams: Option[String], val servers: Option[String], val timeStart: DateTime, val timeEnd: DateTime)
{

}
