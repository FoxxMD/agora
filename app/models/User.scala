package models

import org.joda.time.DateTime
import securesocial.core._

/**
 * Created by Matthew on 6/30/2014.
 */

trait UserT {

  var email:String
  val createdDate: DateTime
  var role: String
  var firstName: Option[String]
  var lastName: Option[String]
  var globalHandle: Option[String]
  var gameProfiles: List[UserPlatformProfile]

  def getTournaments: List[Tournament]
  def getEvents: List[Event]
  def getTeams: List[Team]
}

class User(
           var email: String,
           var role: String,
           var firstName: Option[String],
           var lastName: Option[String],
           var globalHandle: Option[String],
           val cDate: Option[DateTime])
  extends UserT {

  val createdDate: DateTime = cDate.getOrElse(DateTime.now())
  var gameProfiles = List[UserPlatformProfile]()

  def getTournaments: List[Tournament] = ???
  def getEvents: List[Event] = ???
  def getTeams: List[Team] = ???
}

case class UserIdentity(
                         user: User,
                         providerId: String,
                         userId: String,
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         email: Option[String],
                         avatarUrl: Option[String],
                         authMethod: AuthenticationMethod,
                         oAuth1Info: Option[OAuth1Info] = None,
                         oAuth2Info: Option[OAuth2Info] = None,
                         passwordInfo: Option[PasswordInfo] = None)
{

  def getBasicProfile: BasicProfile = new BasicProfile(providerId, userId, user.firstName, user.lastName,
    Option(user.firstName + " " + user.lastName), email, None, authMethod, None, oAuth2Info, passwordInfo)
}

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
