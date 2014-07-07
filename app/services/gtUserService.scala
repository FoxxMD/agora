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
import models.{User, UserIdentity}
import play.api.{Application, Logger}
import securesocial.core.providers.Token
import securesocial.core.{Identity, IdentityId, UserServicePlugin}


class gtUserService(application: Application) extends UserServicePlugin(application) {
  val logger = Logger("application.controllers.gtUserService")
  val uie = UserIdentityEntity
  //
  var users = Map[(String, String), User]()
  //private var identities = Map[String, BasicProfile]()
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[UserIdentity] = {
/*    if ( logger.isDebugEnabled ) {
      logger.debug("users = %s".format(users))
    }*/

    val result = Daos.queryDao.query(select from uie where uie.providerId === id.providerId and uie.userId === id.userId)

/*    val result = for (
      user <- users.values ;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.userId == userId)
    ) yield {
      basicProfile
    }*/
    //Future.successful(result.headOption)
    result.headOption
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[UserIdentity] = {
    if ( logger.isDebugEnabled ) {
      logger.debug("users = %s".format(users))
    }
    //val someEmail = Some(email)
    val result = Daos.queryDao.query(select from uie where uie.providerId === providerId and uie.email === email)
/*    val result = for (
      user <- users.values ;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.email == someEmail)
    ) yield {
      basicProfile
    }*/
    //Future.successful(result.headOption)
    result.headOption
  }

  def save(user: Identity): Identity = {
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
    val maybeUser = Daos.queryDao.query(select from uie where uie.providerId === user.identityId.providerId and uie.userId === user.identityId.userId).headOption

    maybeUser match {
      case Some(existingUser) =>
        existingUser.pwInfo = user.passwordInfo
        existingUser.oauth = user.oAuth2Info

        val updated = Daos.userIdentityDao.update(existingUser)
/*        val identities = existingUser._2.identities
        val updatedList = identities.patch( identities.indexWhere( i => i.providerId == user.providerId && i.userId == user.userId ), Seq(user), 1)
        val updatedUser = existingUser._2.copy(identities = updatedList)
        users = users + (existingUser._1 -> updatedUser)*/
        //Future.successful(updated)
        updated

      case None =>
        user match {
          case fuser: UserIdentity => {
            val inserted = Daos.userIdentityDao.create(fuser)
            //Future.successful(inserted)
            inserted
          }
        }
        //val inserted = Daos.userIdentityDao.create(user)
        //val newUser = DemoUser(user, List(user))
        //users = users + ((user.providerId, user.userId) -> newUser)

    }
  }

/*  def link(current: DemoUser, to: BasicProfile): Future[User] = {
    if ( current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
      Future.successful(current)
    } else {
      val added = to :: current.identities
      val updatedUser = current.copy(identities = added)
      users = users + ((current.main.providerId, current.main.userId) -> updatedUser)
      Future.successful(updatedUser)
    }
  }*/

  def save(token: Token) = {
    //Future.successful {
      tokens += (token.uuid -> token)
      //token
    //}
  }

  def findToken(token: String): Option[Token] = {
    //Future.successful { tokens.get(token) }
    tokens.get(token)
  }

  def deleteToken(uuid: String) = {
    //Future.successful {
      tokens.get(uuid) match {
        case Some(token) =>
          tokens -= uuid
          Some(token)
        case None => None
      }
    //}
  }

//  def deleteTokens(): Future {
//    tokens = Map()
//  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }

 /* override def updatePasswordInfo(user: UserIdentity, info: PasswordInfo): Future[Option[UserIdentity]] = {
    Future.successful {
      for (
        found <- users.values.find(_ == user);
        identityWithPasswordInfo <- found.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        val idx = found.identities.indexOf(identityWithPasswordInfo)
        val updated = identityWithPasswordInfo.copy(passwordInfo = Some(info))
        val updatedIdentities = found.identities.patch(idx, Seq(updated), 1)
        found.copy(identities = updatedIdentities)
        updated
      }
    }
  }

  override def passwordInfoFor(user: DemoUser): Future[Option[PasswordInfo]] = {
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


