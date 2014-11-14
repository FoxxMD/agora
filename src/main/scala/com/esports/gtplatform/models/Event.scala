package models

import com.esports.gtplatform.models.DomainEntity
import org.joda.time.DateTime


case class Event(name: String = "A New Event", joinType: String = "Public", id: Option[Int] = None) extends DomainEntity[Event] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    def details: Option[EventDetail] = eventDetails.where(x => x.eventId === this.id.get).singleOption
    lazy val payments: List[EventPayment] = eventToPayments.left(this).iterator.toList
    lazy val users: List[EventUser] = eventToUsers.left(this).associations.iterator.toList
    lazy val tournaments: List[Tournament] = eventToTournaments.left(this).iterator.toList


  def getModerators: List[User] = this.users.filter(x => x.isModerator).map(u => u.user.get)
  def getAdmins: List[User] = this.users.filter(x => x.isAdmin).map(u => u.user.get)
  def getPresentUsers: List[User] = this.users.filter(x => x.isPresent).map(u => u.user.get)
  def isModerator(u: User): Boolean = this.users.exists(x => (x.isModerator || x.isAdmin) && x.userId == u.id.get)
  def isAdmin(u: User): Boolean = this.users.exists(x => x.isAdmin && x.userId == u.id.get)
  def isPresent(u: User): Boolean = this.users.exists(x => x.isPresent && x.userId == u.id.get)

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(name = "",joinType = "",id = Some(0))
}


case class EventDetail(locationName: Option[String] = None, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None, scheduledEvents: Option[String] = None, credits: Option[String] = None, faq: Option[String] = None, eventId: Option[Int] = None){
    def this() = this(locationName = Some(""), address = Some(""), city = Some(""), state = Some(""), description = Some(""), rules = Some(""), prizes = Some(""), streams = Some(""), servers = Some(""), timeStart = Some(DateTime.now), timeEnd = Some(DateTime.now), scheduledEvents = Some(""), credits = Some(""), faq = Some(""), eventId = Some(0))
}
case class EventPayment(eventsId: Int, payType: String, secretKey: Option[String], publicKey: Option[String], address: Option[String], amount: Double, isEnabled: Boolean = true, id: Option[Int] = None) extends DomainEntity[EventPayment]{
    def this() = this(eventsId = 0, payType = "", secretKey = Some(""), publicKey = Some(""), address = Some(""), amount = 0.0, isEnabled = true, id = Some(0))
}
