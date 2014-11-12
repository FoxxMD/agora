package models

import org.joda.time.DateTime


case class Event(name: String = "A New Event", joinType: String = "Public", id: Option[Int] = None) {

    var details: Option[EventDetail] = None
    var payments: Set[EventPayment] = Set()
    var users: Set[EventUser] = Set()
    var tournaments: Set[Tournament] = Set()


  def getModerators: Set[User] = this.users.filter(x => x.isModerator).map(u => u.user.get)
  def getAdmins: Set[User] = this.users.filter(x => x.isAdmin).map(u => u.user.get)
  def getPresentUsers: Set[User] = this.users.filter(x => x.isPresent).map(u => u.user.get)
  def isModerator(u: User): Boolean = this.users.exists(x => (x.isModerator || x.isAdmin) && x.userId == u.id.get)
  def isAdmin(u: User): Boolean = this.users.exists(x => x.isAdmin && x.userId == u.id.get)
  def isPresent(u: User): Boolean = this.users.exists(x => x.isPresent && x.userId == u.id.get)
}


case class EventDetail(locationName: Option[String] = None, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None, scheduledEvents: Option[String] = None, credits: Option[String] = None, faq: Option[String] = None, eventId: Option[Int] = None)
case class EventPayment(eventsId: Int, payType: String, secretKey: Option[String], publicKey: Option[String], address: Option[String], amount: Double, isEnabled: Boolean = true, id: Option[Int] = None)
