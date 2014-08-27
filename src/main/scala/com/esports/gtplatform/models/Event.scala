package models

import com.esports.gtplatform.models.{MeetingT, Requestable, Inviteable}
import monocle.SimpleLens
import monocle.syntax._
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
            tournaments: Set[Tournament],id: Int = 0) extends Inviteable with Requestable with MeetingT[Event] {

  private[this] val UserListLens: SimpleLens[Event, Set[EventUser]] = SimpleLens[Event](_.users)((e, newUsers) => e.copy(users = newUsers))

  def getModerators: Set[User] = this.users.filter(x => x.isModerator).map(u => u.user)
  def getAdmins: Set[User] = this.users.filter(x => x.isAdmin).map(u => u.user)
  def getPresentUsers: Set[User] = this.users.filter(x => x.isPresent).map(u => u.user)
  def isModerator(u: User): Boolean = this.users.exists(x => (x.isModerator || x.isAdmin) && x.user == u)
  def isAdmin(u: User): Boolean = this.users.exists(x => x.isAdmin && x.user == u)
  def isPresent(u: User): Boolean = this.users.exists(x => x.isPresent && x.user == u)

  override def addUser(u: User): Event = this applyLens UserListLens modify (_.+(EventUser(this,u,false,false,false)))

  override def removeUser(u: User): Event = this applyLens UserListLens modify (_.filter(x => x.user != u))
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
