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

case class Event(name: String, eventType: JoinType.Value, details: EventDetails, users: Set[EventUser],
            tournaments: Set[Tournament],id: Int = 0) {

}

/* Rules and prizes will work very similar to the old GameFest -- they will be stored as JSON strings in the DB
 *
 * I would like to have description be Markdown, but will need to implement a system where the DB stores both the raw Markdown and a copy of the formatted HTML
 * so it doesn't have to be rendered on every page load.
 * */
case class EventDetails(event: Event, address: Option[String], city: Option[String], state: Option[String], description: Option[String], rules: Option[String], prizes: Option[String],
                   streams: Option[String], servers: Option[String], timeStart: DateTime, timeEnd: DateTime)
{

}
