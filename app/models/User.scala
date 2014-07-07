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


class User(val email: String, val createdDate: DateTime, val role: String, val firstName: Option[String], val lastName: Option[String],
           val globalHandle: Option[String],  val identities: Set[UserIdentity], val gameProfiles: Set[UserPlatformProfile],
           val teams: Set[TeamUser], val events: Set[EventUser], val tournaments: Set[TournamentUser]) {

  val id: Option[Int] = None
}

class UserIdentity(val user: User, var userId: String,  var providerId: String, var email: Option[String], val aMethod: AuthenticationMethod, var oauth: Option[OAuth2Info],
                   var pwInfo: Option[PasswordInfo]) extends Identity {

  override def identityId = new IdentityId(userId, providerId)

  override def firstName: String = user.firstName.getOrElse(???)

  override def lastName: String = user.lastName.getOrElse(???)

  override def fullName: String = user.firstName + " " + user.lastName

  override def oAuth1Info: Option[OAuth1Info] = ???

  override def oAuth2Info: Option[OAuth2Info] = oauth

  override def avatarUrl: Option[String] = ???

  override def authMethod: AuthenticationMethod = authMethod

  override def passwordInfo: Option[PasswordInfo] = pwInfo
}

class UserPlatformProfile(val user: User, val platform: Platform.Value, val identifier: String)
