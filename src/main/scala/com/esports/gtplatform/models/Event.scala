package models

import com.esports.gtplatform.models.{MeetingT, Requestable, Inviteable}
import monocle.{Lenser, SimpleLens}
import monocle.syntax._
import org.joda.time.DateTime

object JoinType extends Enumeration {
  type JoinType = Value
  val Invite, Public, Hidden = Value

  def toString(j: JoinType) = j match {
    case Invite => "Invite"
    case Public => "Public"
    case Hidden => "Hidden"
  }

  def fromString(j: String): JoinType = j match {
    case "Invite" => Invite
    case "Public" => Public
    case "Hidden" => Hidden
  }
}

case class Event(name: String, joinType: JoinType.Value, details: Option[EventDetails] = None, users: Set[EventUser] = Set(),
            tournaments: Set[Tournament] = Set(),id: Int = 0) extends Inviteable with Requestable with MeetingT[Event] {

  private[this] val UserListLens: SimpleLens[Event, Set[EventUser]] = SimpleLens[Event](_.users)((e, newUsers) => e.copy(users = newUsers))
  private[this] val DetailsLens: SimpleLens[Event, Option[EventDetails]] = SimpleLens[Event](_.details)((e, newDetails) => e.copy(details = newDetails))
 // private[this] val DetailsDescriptionLens: SimpleLens[EventDetails, Option[String]] = SimpleLens[EventDetails](_.description)((ev, newDesc) => ev.copy(description = newDesc))
  private[this] val DetailsLenser = Lenser[EventDetails]
  private[this] val DetailsDescriptionLens: SimpleLens[Option[EventDetails], Option[String]] = SimpleLens[Option[EventDetails]](_.get.description)((ev, newDesc) => Option(ev.get.copy(description = newDesc))) //DetailsLenser(_.rules)
  //private[this] val DetailsDescriptionLens = DetailsLenser(_.description)

  def getModerators: Set[User] = this.users.filter(x => x.isModerator).map(u => u.user)
  def getAdmins: Set[User] = this.users.filter(x => x.isAdmin).map(u => u.user)
  def getPresentUsers: Set[User] = this.users.filter(x => x.isPresent).map(u => u.user)
  def isModerator(u: User): Boolean = this.users.exists(x => (x.isModerator || x.isAdmin) && x.user == u)
  def isAdmin(u: User): Boolean = this.users.exists(x => x.isAdmin && x.user == u)
  def isPresent(u: User): Boolean = this.users.exists(x => x.isPresent && x.user == u)

  override def addUser(u: User): Event = this applyLens UserListLens modify (_.+(EventUser(this,u,false,false,false)))

  override def removeUser(u: User): Event = this applyLens UserListLens modify (_.filter(x => x.user != u))

  def setDetails(e: EventDetails): Event = this applyLens DetailsLens set Some(e)
  def setDescription(desc: String): Event = this applyLens DetailsLens composeLens DetailsDescriptionLens set Some(desc)
  //def setRules(rules: String): Event = this applyLens DetailsLens composeLens DetailsRulesLens set Some(rules)
}

/* Rules and prizes will work very similar to the old GameFest -- they will be stored as JSON strings in the DB
 *
 * I would like to have description be Markdown, but will need to implement a system where the DB stores both the raw Markdown and a copy of the formatted HTML
 * so it doesn't have to be rendered on every page load.
 * */

case class EventDetails(event: Event, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None,
                        streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None)
{

}/*case class EventDetails(address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None,
                   streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None, id: Int = 0)
{

}*/
