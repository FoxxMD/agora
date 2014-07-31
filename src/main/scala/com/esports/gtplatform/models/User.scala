package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

/* I would like User to be a case class but I'm not sure how unwieldy it will be. Once I start implementing business logic for Users
 * it will be easier to tell if this can be a case class or if it must stay mutable.
 *
 * The main user class holds only a small amount of information in order to facilitate decoupling between related objects.
 */
case class User(
                 email: String,
                 role: String,
                 firstName: Option[String],
                 lastName: Option[String],
                 globalHandle: Option[String],
                 cDate: Option[DateTime],
                 id: Int = 0,
                 teams: List[TeamUser] = List()) {
  val createdDate: DateTime = cDate.getOrElse(DateTime.now())
  var gameProfiles = List[UserPlatformProfile]()

  /* You'll notice on almost all classes that related objects will be accessed through a method rather than as child/parent objects.
  * This is intentional as it prevents cyclical dependencies and works better with immutable data structures.
  * */

  def getTournaments: List[Tournament] = ???

  def getEvents: List[Event] = ???

  //TODO work on related entities
  //def getTeams: List[TeamUser] = queryDao.query(select from TeamUserEntity where TeamUserEntity.user. === this.id).map(x => x.team)
}


/* UserIdentity is a descriptor for a user's login credentials. It's separated from the main user because blah blah decoupling.
*
* I've left fields OAuth implementation needs.*/
case class UserIdentity(
                         user: User,
                         providerId: String,
                         userId: String,
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         email: Option[String],
                         avatarUrl: Option[String],
                         password: String,
                         salt: String,
                         id: Int = 0)

/* Right now more of a placeholder than anything. This will eventually serve as a list linked popular game profiles for this user
* EX Steam, Battle.NET, etc. etc. */
case class UserPlatformProfile(user: User, platform: Platform.Value, identifier: String)

object Platform extends Enumeration {
  type Platform = Value
  val Steam, Battlenet, Riot = Value

  def toString(j: Platform) = j match {
    case Steam => "Steam"
    case Battlenet => "Battle.net"
    case Riot => "Riot"
  }

  def fromString(j: String): Platform = j match {
    case "Steam" => Steam
    case "Battle.net" => Battlenet
    case "Riot" => Riot
  }
}
