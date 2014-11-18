package models

import com.esports.gtplatform.business.{EventDetailRepo, EventDetailsRepository}
import com.esports.gtplatform.models.DomainEntity
import org.joda.time.DateTime


case class Event(name: String = "A New Event", joinType: String = "Public", id: Option[Int] = None) extends DomainEntity[Event] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    private[this] val eventDetailsRepo: EventDetailRepo = new EventDetailsRepository

    //private[this] var _details: Option[EventDetail] = None
    //private[this] var _detailInit: Boolean = false
    private[this] var _users: Option[List[EventUser]] = None
    private[this] var _tournaments: Option[List[Tournament]] = None

def details: Option[EventDetail] =  eventDetailsRepo.get(id.get)
/*    def details: Option[EventDetail] = {
        if(_detailInit)
             _details
        else{
           this._details = eventDetailsRepo.get(id.get)
            this._detailInit = true
            this._details
        }
    }
    def details(e: EventDetail) = {
        this._details = Option(e)
        this._detailInit = true
    }*/
    def payments: List[EventPayment] = inTransaction (eventToPayments.left(this).iterator.toList)
    def users: List[EventUser] = _users.getOrElse[List[EventUser]]{
        this._users = Option(inTransaction (eventToUsers.left(this).associations.iterator.toList))
        this._users.get
    }
    def users(eu: List[EventUser]) = {
        this._users = Option(eu)
    }
    def tournaments: List[Tournament] = _tournaments.getOrElse[List[Tournament]]{
        this._tournaments = Option(inTransaction (eventToTournaments.left(this).iterator.toList))
        this._tournaments.get
    }
    def tournaments(t: List[Tournament]) ={
        this._tournaments = Option(t)
    }


  def getModerators: List[User] = this.users.filter(x => x.isModerator).map(u => u.user.get)
  def getAdmins: List[User] = this.users.filter(x => x.isAdmin).map(u => u.user.get)
  def getPresentUsers: List[User] = this.users.filter(x => x.isPresent).map(u => u.user.get)
  def isModerator(u: User): Boolean = this.users.exists(x => (x.isModerator || x.isAdmin) && x.userId == u.id.get)
  def isAdmin(u: User): Boolean = this.users.exists(x => x.isAdmin && x.userId == u.id.get)
  def isPresent(u: User): Boolean = this.users.exists(x => x.isPresent && x.userId == u.id.get)

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(name = "",joinType = "",id = Some(0))
}


case class EventDetail(locationName: Option[String] = None, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None, scheduledEvents: Option[String] = None, credits: Option[String] = None, faq: Option[String] = None, eventId: Option[Int] = None) extends DomainEntity[EventDetail]{
    def id = eventId
    def this() = this(locationName = Some(""), address = Some(""), city = Some(""), state = Some(""), description = Some(""), rules = Some(""), prizes = Some(""), streams = Some(""), servers = Some(""), timeStart = Some(DateTime.now), timeEnd = Some(DateTime.now), scheduledEvents = Some(""), credits = Some(""), faq = Some(""), eventId = Some(0))
}
case class EventPayment(eventId: Int, payType: String, secretKey: Option[String], publicKey: Option[String], address: Option[String], amount: Double, isEnabled: Boolean = true, id: Option[Int] = None) extends DomainEntity[EventPayment]{
    def this() = this(eventId = 0, payType = "", secretKey = Some(""), publicKey = Some(""), address = Some(""), amount = 0.0, isEnabled = true, id = Some(0))
}
