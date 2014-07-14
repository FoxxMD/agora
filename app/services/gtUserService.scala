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
import play.api.Logger
import securesocial.core.providers.MailToken
import securesocial.core.services.{SaveMode, UserService}
import securesocial.core.{BasicProfile, PasswordInfo}

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

    val result = Daos.queryDao.querySingleResult(select from uie where uie.providerId === providerId and uie.userId === userId)
/*    val result = for (
      user <- users.values ;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.userId == userId)
    ) yield {
      basicProfile
    }*/
    result match {
      case Some(user) => Future.successful(Option(user.getBasicProfile))
      case None => Future.successful(None)
    }
/*    result match {
      case Some(user: UserIdentity) => Future.successful(Option(user.getBasicProfile))
      case None => Future.successful(None)
    }*/
    //Future.successful(result.headOption..getBasicProfile)
    //Future.successful(Option(result.head))
    //result.headOption
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    if ( logger.isDebugEnabled ) {
      logger.debug("users = %s".format(users))
    }

    val result = Daos.queryDao.querySingleResult(select from uie where uie.providerId === providerId and uie.email === email)

    result match {
      case Some(user) =>
        Future.successful(Option(result.head.getBasicProfile))
      case None =>
        Future.successful(None)
    }
  }

  def save(profile: BasicProfile, mode: SaveMode): Future[User] = {

    val maybeUser = Daos.queryDao.querySingleResult(select from uie where uie.providerId === profile.providerId and uie.userId === profile.userId)

    mode match {
      case SaveMode.SignUp =>
        val newuser = Daos.userDao.create(new User(profile.email.get,"user",profile.firstName,profile.lastName,None,None))

        val added = new UserIdentity(
          newuser,
          profile.providerId,
          profile.userId,
          profile.firstName,
          profile.lastName,
          None,
          profile.email,
          None,
          profile.authMethod,
          None,
          profile.oAuth2Info,
          profile.passwordInfo)


        val inserted = Daos.userIdentityDao.create(added)
        Future.successful(inserted.user)

      case SaveMode.LoggedIn =>
        maybeUser match {
          case Some(existingUser) =>
            Future.successful(existingUser.user)
          case None =>
            Future.successful(maybeUser.get.user)
        }
    }
  }

  def link(current: User, to: BasicProfile): Future[User] = {

    val added = new UserIdentity(
      current,
      to.providerId,
      to.userId,
      to.firstName,
      to.lastName,
      None,
      to.email,
      None,
      to.authMethod,
      None,
      to.oAuth2Info,
      to.passwordInfo)
      val linked = Daos.userIdentityDao.create(added)

      Future.successful(linked.user)
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

  //TODO These...
  override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = ???
  override def passwordInfoFor(user: User): Future[Option[PasswordInfo]] = ???
  /*override def updatePasswordInfo(user: User, info: PasswordInfo): Future[Option[BasicProfile]] = {
    Future.successful {
      for (
        //found <- users.values.find(_ == user);
        identityWithPasswordInfo <- user.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        //val idx = user.identities.indexOf(identityWithPasswordInfo)
        val updated = identityWithPasswordInfo.copy(passwordInfo = Some(info)) //(passwordInfo = Some(info))
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
  }*/
}


