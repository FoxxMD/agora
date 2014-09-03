package com.esports.gtplatform.business.services

/**
 * Created by Matthew on 8/20/2014.
 */

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business._
import models.{User, UserIdentity}
import org.slf4j.LoggerFactory
import com.googlecode.mapperdao.jdbc.Transaction

class NewUserService(implicit val bindingModule: BindingModule) extends Injectable with GenericService[UserIdentity] {

  val logger = LoggerFactory.getLogger(getClass)
  private val userRepo = inject[UserRepo]
  private val nonActiveUserRepo = inject[NonActiveUserRepo]
  private val nonActiveUserIdentRepo = inject[NonActiveUserIdentityRepo]
  private val sql = inject[SqlAccess]
  private val tx = inject[Transaction]

  def newUserPass(handle: String, email: String, password: String): UserIdentity = {
    val newu = User(email, "user", None, None, handle, None)
    val salted = PasswordSecurity.createHash(password)
    UserIdentity(newu, "userpass", email, None, None, None, Option(email), None, salted)
  }

  def isUnique(obj: UserIdentity): Option[String] = {
    if (!userRepo.getByEmail(obj.user.email).isDefined && !nonActiveUserRepo.getByEmail(obj.user.email).isDefined) {
      if (!userRepo.getByHandle(obj.user.globalHandle).isDefined && !nonActiveUserRepo.getByHandle(obj.user.globalHandle).isDefined)
        None
      else
        Some("handle")
    }
    else {
      Some("email")
    }
  }

  def create(obj: UserIdentity): String = {
    val token = java.util.UUID.randomUUID.toString

    val tx = inject[Transaction]

    tx { () =>
      val inserted = nonActiveUserIdentRepo.create(obj)
      sql.lowLevelUpdate("insert into confirmationtokens values(?,?,?)", List(inserted.id, token, null))
    }
    logger.info("Successfully inserted confirmation token " + token + "into db.")
    token
  }

  def associateEvent(token: String, eventId: Int) = {
    sql.lowLevelUpdate("update confirmationtokens set eventId=? where token=?", List(eventId, token))
    logger.info("Associated user with eventId " + eventId)
  }

  def confirmNewUser(token: String): (Boolean, Option[Int]) = {
    val row = sql.lowLevelQuery("select * from confirmationtokens where token=?", List(token))

    row.map { m => m.apply("userIdentId") } match {
      case Some(t: Int) =>
        logger.info("User is confirming registration with valid token")
        val returnedUserIdentity = nonActiveUserIdentRepo.get(t).get
        val inactiveUserId = returnedUserIdentity.user.id
        //need to return clean objects with IDs or else mapperDao gets cranky
        val temp = returnedUserIdentity.copy()
        val newuserIdentity = temp.copy(user = temp.user.copy())
        val realUserIdentRepo = inject[GenericMRepo[UserIdentity]]

        tx { () =>

          realUserIdentRepo.create(newuserIdentity)



          nonActiveUserIdentRepo.delete(returnedUserIdentity)
          nonActiveUserRepo.delete(inactiveUserId)
          sql.lowLevelUpdate("delete from confirmationtokens where token=?", List(token))

          logger.info("Successfully transferred user as confirmed.")
        }
        //check if they have an eventId associate (so they registered for a certain event)
        row.map { e => e.apply("eventId")} match {
          case Some(e: Int) =>
            //TODO use UserService to register them for this event
            logger.info("Event Id found with token, registering the user for an event. EventId:"+ e)
            (true, Option(e))
          case _ =>
            logger.info("No event Id found with token, not registering the user for an event.")
            (true, None)
        }
      //case None => (false, None)
      case _ => (false, None)
    }

  }
}