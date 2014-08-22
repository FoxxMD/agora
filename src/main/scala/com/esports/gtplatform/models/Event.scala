package models

import com.esports.gtplatform.models.{Requestable, Inviteable}
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
            tournaments: Set[Tournament],id: Int = 0) extends Inviteable with Requestable {

}

/* Rules and prizes will work very similar to the old GameFest -- they will be stored as JSON strings in the DB
 *
 * I would like to have description be Markdown, but will need to implement a system where the DB stores both the raw Markdown and a copy of the formatted HTML
 * so it doesn't have to be rendered on every page load.
 * */
case class EventDetails(event: Event, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None,
                   streams: Option[String] = None, servers: Option[String] = None, timeStart: DateTime, timeEnd: DateTime)
{

}
