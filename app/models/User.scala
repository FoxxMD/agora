package models

import org.joda.time.DateTime
import securesocial.core._

/**
 * Created by Matthew on 6/30/2014.
 */
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


case class User(
           var email: String,
           var createdDate: DateTime,
           var role: String,
           var firstName: Option[String],
           var lastName: Option[String],
           var globalHandle: Option[String],
           var identities: List[UserIdentity],
           var gameProfiles: List[UserPlatformProfile],
           var teams: List[TeamUser],
           var events: List[EventUser],
           var tournaments: List[TournamentUser]) {
}

/*case class UserIdentity(
                        user: User,
                        var userId: String,
                        providerId: String,
                        var email: Option[String],
                        aMethod: AuthenticationMethod,
                        var oauth: Option[OAuth2Info],
                        var passwordInfo: Option[PasswordInfo] = None) extends GenericProfile {

  override def firstName: Option[String] = user.firstName

  override def lastName: Option[String] = user.lastName

  override def fullName: Option[String] = Option(user.firstName + " " + user.lastName)

  override def oAuth1Info: Option[OAuth1Info] = ???

  override def oAuth2Info: Option[OAuth2Info] = oauth

  override def avatarUrl: Option[String] = ???

  override def authMethod: AuthenticationMethod = aMethod

  def getBasicProfile: BasicProfile = new BasicProfile(providerId, userId, user.firstName, user.lastName,
    Option(user.firstName + " " + user.lastName), email, None, aMethod, None, oauth, passwordInfo)
}*/

class UserIdentity(
                         val user: User,
                         var providerId: String,
                         var userId: String,
                         var firstName: Option[String],
                         var lastName: Option[String],
                         var fullName: Option[String],
                         var email: Option[String],
                         var avatarUrl: Option[String],
                         var authMethod: AuthenticationMethod,
                         var oAuth1Info: Option[OAuth1Info] = None,
                         var oAuth2Info: Option[OAuth2Info] = None,
                         var passwordInfo: Option[PasswordInfo] = None)
  extends GenericProfile {

  def getBasicProfile: BasicProfile = new BasicProfile(providerId, userId, user.firstName, user.lastName,
    Option(user.firstName + " " + user.lastName), email, None, authMethod, None, oAuth2Info, passwordInfo)
}

class UserPlatformProfile(val user: User, val platform: Platform.Value, val identifier: String)
