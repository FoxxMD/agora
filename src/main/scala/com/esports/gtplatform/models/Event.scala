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

case class Event(name: String, joinType: JoinType.Value, details: Option[EventDetails] = None, payments: Set[EventPayment] = Set(), users: Set[EventUser] = Set(),
            tournaments: Set[Tournament] = Set(),id: Int = 0) extends Inviteable with Requestable with MeetingT[Event] {

  private[this] val UserListLens: SimpleLens[Event, Set[EventUser]] = SimpleLens[Event](_.users)((e, newUsers) => e.copy(users = newUsers))
  private[this] val DetailsLens: SimpleLens[Event, Option[EventDetails]] = SimpleLens[Event](_.details)((e, newDetails) => e.copy(details = newDetails))
  private[this] val PaymentListLens: SimpleLens[Event, Set[EventPayment]] = SimpleLens[Event](_.payments)((e, newPayments) => e.copy(payments = newPayments))
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

  override def addUser(u: User): Event = this applyLens UserListLens modify (_.+(EventUser(this,u,isPresent = false,isAdmin = false,isModerator = false)))
  override def removeUser(u: User): Event = this applyLens UserListLens modify (_.filter(x => x.user != u))
  def setUserPayment(u: User, paid: Boolean, receipt: Option[String]): Event = {
    val ue = this.users.find(x => x.user == u).getOrElse(throw new NoSuchElementException("User " + u.id + " does not exist in the event " + this.id))
    this applyLens UserListLens set this.users - ue + ue.copy(hasPaid = paid, receiptId = receipt)
  }
  def addPayment(e: EventPayment): Event = this applyLens PaymentListLens modify(_+ e)
  def removePayment(e: EventPayment): Event = this applyLens PaymentListLens modify(_.filter(x => x != e))
  def removePayment(id: Int): Event = this applyLens PaymentListLens modify(_.filter(x => x.id != id))
  def changePayment(id: Int, e: EventPayment): Event = this.removePayment(id).addPayment(e)


  def setDetails(e: EventDetails): Event = this applyLens DetailsLens set Option(e)
  def setDescription(desc: String): Event = this applyLens DetailsLens composeLens DetailsDescriptionLens set Some(desc)
  //def setRules(rules: String): Event = this applyLens DetailsLens composeLens DetailsRulesLens set Some(rules)
}

/* Rules and prizes will work very similar to the old GameFest -- they will be stored as JSON strings in the DB
 *
 * */

case class EventDetails(event: Event, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None,
                        streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None)
{

}

object PaymentType extends Enumeration {
  type PaymentType = Value
  val Stripe, Bitcoin, Dogecoin, Paypal = Value

  def toString(j: PaymentType) = j match {
    case Stripe => "Stripe"
    case Bitcoin => "Bitcoin"
    case Dogecoin => "Dogecoin"
    case Paypal => "Paypal"
  }

  def fromString(j: String): PaymentType = j match {
    case "Stripe" => Stripe
    case "Bitcoin" => Bitcoin
    case "Dogecoin" => Dogecoin
    case "Paypal" => Paypal
  }
}

case class EventPayment(event: Event, payType: PaymentType.Value, secretKey: Option[String], publicKey: Option[String], address: Option[String], amount: Double, isEnabled: Boolean = true, id: Int = 0)
