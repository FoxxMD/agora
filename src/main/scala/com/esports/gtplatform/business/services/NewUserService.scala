package com.esports.gtplatform.business.services

/**
 * Created by Matthew on 8/20/2014.
 */
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business._
import models.{UserIdentity, User}
import org.slf4j.LoggerFactory

class NewUserService(implicit val bindingModule: BindingModule) extends Injectable with GenericService[UserIdentity] {

  val logger = LoggerFactory.getLogger(getClass)
  private val userRepo = inject[UserRepo]
  private val nonActiveUserRepo = inject[NonActiveUserRepo]
  private val nonActiveUserIdentRepo = inject[NonActiveUserIdentityRepo]
  private val sql = inject[SqlAccess]

  def newUserPass(handle: String, email: String, password: String): UserIdentity = {
    val newu = User(email, "user", None, None, None, None)
    val salted = PasswordSecurity.createHash(password)
    UserIdentity(newu, "userpass", email, None, None, None, Option(email), None, salted)
  }

  def isUnique(obj: UserIdentity) : Boolean = {
    !userRepo.getByEmail(obj.user.email).isDefined && !nonActiveUserRepo.getByEmail(obj.user.email).isDefined
  }

  def create(obj: UserIdentity): String = {
    val token = java.util.UUID.randomUUID.toString
    val inserted = nonActiveUserIdentRepo.create(obj)
    sql.lowLevelUpdate("insert into confirmationtokens values(?,?)",List(inserted.id,token))
    logger.info("Successfully inserted confirmation token " + token + "into db.")
    token
  }

  def confirmNewUser(token: String): Boolean = {
    val userid = sql.lowLevelQuery("select * from confirmationtokens where token=?",List(token)).map {m => m.int("userIdentId")}
    userid match {
     case Some(t: Int) =>
       logger.info("User is confirming registration with valid token")
       val returnedUserIdentity = nonActiveUserIdentRepo.get(t).get
       //haven't checked to see if I can delete just one of the objects
       val inactiveUserId = returnedUserIdentity.user.id
       val inactiveIdentId = returnedUserIdentity.id
       //need to return clean objects with IDs or else mapperDao gets cranky
       val temp = returnedUserIdentity.copy()
       val newuserIdentity = temp.copy(user = temp.user.copy())
       val realUserIdentRepo = inject[GenericRepo[UserIdentity]]
       realUserIdentRepo.create(newuserIdentity)
       nonActiveUserIdentRepo.delete(inactiveIdentId)
       nonActiveUserRepo.delete(inactiveUserId)
       sql.lowLevelUpdate("delete from confirmationtokens where token=?",List(token))
       logger.info("Successfully transferred user as confirmed.")
       true
     case None => false
    }
  }

}