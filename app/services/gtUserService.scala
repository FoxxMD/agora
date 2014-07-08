/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package service

import com.googlecode.mapperdao.Query._
import dao._
import models._
import org.joda.time.DateTime
import play.api.{Logger}
import securesocial.core.{BasicProfile, PasswordInfo}
import securesocial.core.providers.{UsernamePasswordProvider, MailToken}
import securesocial.core.services.{UserService, SaveMode}

import scala.concurrent.Future


class gtUserService extends UserService[User] {
  val logger = Logger("application.controllers.gtUserService")
  val uie = UserIdentityEntity
  //
  var users = Map[(String, String), User]()
  //private var identities = Map[String, BasicProfile]()
  private var tokens = Map[String, MailToken]()

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
/*    if ( logger.isDebugEnabled ) {
      logger.debug("users = %s".format(users))
    }*/

    val result = Daos.queryDao.query(select from uie where uie.providerId === providerId and uie.userId === userId)

/*    val result = for (
      user <- users.values ;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.userId == userId)
    ) yield {
      basicProfile
    }*/
    Future.successful(Option(result.head.getBasicProfile))
    //Future.successful(Option(result.head))
    //result.headOption
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    if ( logger.isDebugEnabled ) {
      logger.debug("users = %s".format(users))
    }
    //val someEmail = Some(email)
    val result = Daos.queryDao.querySingleResult(select from uie where uie.providerId === providerId and uie.email === email)
/*    val result = for (
      user <- users.values ;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.email == someEmail)
    ) yield {
      basicProfile
    }*/
    result match {
      case Some(user) =>
        Future.successful(Option(result.head.getBasicProfile))
      case None =>
        Future.successful(None)
    }

    //Future.successful(Option(result.head))
    //result.headOption
  }

  def save(profile: BasicProfile, mode: SaveMode): Future[User] = {
/*    isSignUp match {
      case true =>
        val newUser = user
        users = users + ((user.identities.last.providerId, user.id.toString) -> newUser)
      case false =>

    }*/
    // first see if there is a user with this BasicProfile already.
/*    val maybeUser = users.find {
      case (key, value) if Daos.userIdentityDao.queryDao.query(select from uie where uie.providerId === user.providerId and uie.userId === user.userId) => true //value.identities.exists(su => su.providerId == user.providerId && su.userId == user.userId ) => true
      case _ => false
    }*/
    val maybeUser = Daos.queryDao.querySingleResult(select from uie where uie.providerId === profile.providerId and uie.userId === profile.userId)

    maybeUser match {
      case Some(existingUser) =>
        existingUser.passwordInfo = profile.passwordInfo
        existingUser.oAuth2Info = profile.oAuth2Info

        val updated = Daos.userIdentityDao.update(existingUser)
/*        val identities = existingUser._2.identities
        val updatedList = identities.patch( identities.indexWhere( i => i.providerId == user.providerId && i.userId == user.userId ), Seq(user), 1)
        val updatedUser = existingUser._2.copy(identities = updatedList)
        users = users + (existingUser._1 -> updatedUser)*/
        Future.successful(updated.user)
        //updated.user

      case None =>
            val newUser = new User(
              profile.email.get,
              DateTime.now,
              "user", profile.firstName,
              profile.lastName,
              None,
            List[UserIdentity](),
            List[UserPlatformProfile](),
            List[TeamUser](),
            List[EventUser](),
            List[TournamentUser]())

            val added = new UserIdentity(newUser,profile.providerId, profile.userId, profile.firstName, profile.lastName, None, profile.email, None, profile.authMethod, None, profile.oAuth2Info, profile.passwordInfo) :: newUser.identities
            newUser.copy(identities = added)

            val inserted = Daos.userDao.create(newUser)
            Future.successful(inserted)
            //inserted.user

        //val inserted = Daos.userIdentityDao.create(user)
        //val newUser = DemoUser(user, List(user))
        //users = users + ((user.providerId, user.userId) -> newUser)

    }
  }

  def link(current: User, to: BasicProfile): Future[User] = {
    if ( current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
      Future.successful(current)
    } else {
      val added = to :: current.identities
      //val updatedUser = current.copy(identities = added)
      //users = users + ((current.main.providerId, current.main.userId) -> updatedUser)
      Future.successful(current)
    }
  }

  def saveToken(token: MailToken): Future[MailToken] = {
    Future.successful {
      tokens += (token.uuid -> token)
      token
    }
  }

  def findToken(token: String): Future[Option[MailToken]] = {
    Future.successful { tokens.get(token) }
  }

  def deleteToken(uuid: String): Future[Option[MailToken]] = {
    Future.successful {
      tokens.get(uuid) match {
        case Some(token) =>
          tokens -= uuid
          Some(token)
        case None => None
      }
    }
  }

//  def deleteTokens(): Future {
//    tokens = Map()
//  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }

  override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = {
    Future.successful {
      for (
        //found <- users.values.find(_ == user);
        identityWithPasswordInfo <- user.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        //val idx = user.identities.indexOf(identityWithPasswordInfo)
        val updated = identityWithPasswordInfo.passwordInfo = Some(info) //(passwordInfo = Some(info))
        //Daos.queryDao.update(user)
        //val updatedIdentities = user.identities.patch(idx, Seq(updated), 1)
        //user.copy(identities = updatedIdentities)
        identityWithPasswordInfo.getBasicProfile
      }
    }
  }

  override def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = {
    Future.successful {
      for (
        found <- users.values.find(_ == user);
        identityWithPasswordInfo <- found.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        identityWithPasswordInfo.passwordInfo.get
      }
    }
  }
}


