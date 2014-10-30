package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.Utilities.{PasswordSecurity, Mailer}
import com.esports.gtplatform.business.{EventRepo, GenericMRepo, UserRepo}
import com.esports.gtplatform.business.services.NewUserService
import com.esports.gtplatform.dao.mapperdao.{UserIdentityEntity, Daos}
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
import models.{UserIdentity, User}
import Daos._
import com.googlecode.mapperdao.Query._
import org.scalatra.{InternalServerError, BadRequest, Ok}
import org.springframework.jdbc.BadSqlGrammarException

/**
 * Created by Matthew on 7/29/2014.
 */

/* I would suggest reading the comments in all other files as these comments only make sense once
 * you've got a grasp on the other components
 * */

//Mounted at /
class UserManagementController(implicit val bindingModule: BindingModule) extends StandardController {

    get("/login") {
        //Use the UserPasswordStrategy to authenticate the user and attach the token to response headers
        response.addHeader("ignoreError", "true")
        authUserPass()
        if(isAuthenticated)
            logger.info("[Phone Home] authentication succeeded for User " + user.id)
        contentType = formats("txt")
        Ok(response.getHeader("Authorization"))
    }
    post("/register") {
        val noConfirm = parsedBody.\("noconfirm").extractOpt[Boolean]
        val nuservice = new NewUserService
        //you can get any parameters in the queryString of a POST or GET using params("key")

        //Use this method to extract values from a JSON object in the request
        val email = parsedBody.\("email").extract[String]
        val password = parsedBody.\("password").extract[String]
        val handle = parsedBody.\("handle").extract[String]
        val eventId = parsedBody.\("eventId").extractOpt[String]

        val newuser = nuservice.newUserPass(handle, email, password)
        val m = new Mailer()
        nuservice.isUnique(newuser) match {
            case Some(s: String) =>
                if (s == "handle")
                    BadRequest("Username is already taken.")
                else if (s == "email") {
                    m.sendAlreadyRegistered(email)
                    /* This method is safer than notifying the user that an email address is already in use. We don't want to
                    * give away that information so instead we say "OK sent." and then notify that email address that it is already
                    * registered.
                    */
                    Ok()
                }
            case None =>
                if(noConfirm.isDefined && eventId.isDefined && noConfirm.get)
                {
                    auth()
                    val eventRepo = inject[EventRepo]
                    val event = eventRepo.get(eventId.get.toInt)
                    if(event.get.getAdmins.exists(x => x.id == user.id) || user.role == "admin")
                    {
                        logger.info("[Admin] ("+user.id+") attempting On-Site registration")
                        val status = nuservice.create(newuser,noConfirm = true, event)
                        if(status == "ok")
                        {
                            logger.info("[Admin] ("+user.id+") On-Site registration complete")
                            Ok()
                        }
                        else
                            InternalServerError("Problem creating new user with no confirmation.")
                    }
                    else{
                        halt(403, "You do not have permission to create users")
                    }

                }
                else{
                    val token = nuservice.create(newuser)
                    if (eventId.isDefined)
                        nuservice.associateEvent(token, eventId.get.toInt)
                    logger.info("Non-active user successfully created: " + token)
                    m.sendConfirm(email, handle, token)
                    Ok()
                }
        }
    }
    get("/confirmRegistration") {
        val nuservice = new NewUserService
        val token = request.parameters.get("token")
        token match {
            case Some(t: String) =>
                val confirmed = nuservice.confirmNewUser(t)
                if (confirmed._1)
                    Ok(confirmed._2)
                else
                    BadRequest("Could not find the provided token.")
            case None =>
                BadRequest("No token provided.")
        }
    }
    post("/forgotPassword") {
        val email = parsedBody.\("email").extractOrElse[String](halt(400, "No email address specified."))
        val userRepo = inject[UserRepo]
        val m = new Mailer()
        val newToken = java.util.UUID.randomUUID.toString

        userRepo.getByEmail(email) match {
            case Some(u: User with Persisted) =>
                logger.info("Generating password reset request token for User " + u.id)
                jdbc.queryForMap(
                    """
                      |select t.*
                      |from passwordtokens t
                      |where t.id=?
                    """.stripMargin
                    , u.id).map { m => m.string("token")} match {
                    case Some(token: String) =>
                        logger.info("User already has a password token, generating new one.")
                        jdbc.update(
                            """
                              |UPDATE passwordtokens p
                              |SET token="""+newToken+"""
                              |where p.id=?
                            """.stripMargin, u.id)
                    case None =>
                        jdbc.update(
                            """
                              |INSERT INTO passwordtokens VALUES(?,?)
                            """.stripMargin, List(u.id, newToken))
                }

                m.sendForgotPassword(u.email, newToken)

            case None =>
                logger.warn("Request for password recovery with email address not in DB: " + email)
            case _ => logger.warn("No match found!")
        }
        Ok()
    }
    get("/passwordReset") {
        val p = params.get("token")
        val token: String = params.getOrElse("token",halt(400,"No token received."))
        val pp = params("token")
        jdbc.queryForMap(
            """
              |select t.*
              |from passwordtokens t
              |where t.token=?
            """.stripMargin
            , token).map { m => m.string("token")} match {
            case Some(t: String) =>
            Ok()
            case None =>
            BadRequest("Token is invalid. Please make sure it is entered correctly.")
        }
    }
    post("/passwordReset") {
        val token = parsedBody.\("token").extractOrElse[String](halt(400,"No token specified"))
        val password = parsedBody.\("password").extractOrElse[String](halt(400,"No password specified"))
        val userRepo = inject[UserRepo]
        val identRepo = inject[GenericMRepo[UserIdentity]]
        val tx = inject[Transaction]

        val uie = UserIdentityEntity
            val userId = jdbc.queryForMap(
                """
              |select t.*
              |from passwordtokens t
              |where t.token=?
            """.
                    stripMargin
            , token).map { m => m.int("id")} match {
            case Some(t: Int) =>
                t
            case None =>
                halt(400,"Token is invalid.")
        }

        val identity = queryDao.querySingleResult(select from uie where uie.user === userRepo.get(userId).get)

        val salted = PasswordSecurity.createHash(password)

        val inserted = tx { trans =>
            try {
            identRepo.update(identity.get)
          val update = jdbc.update(
            """
              |DELETE FROM passwordtokens
              |where id=?
            """.stripMargin, userId)
          if(update.rowsAffected != 1)
          {
              logger.error("Problem during password recovery process, no rows were updated in passwordtokens!")
              trans.setRollbackOnly()
              InternalServerError("There was a problem resetting the password. No changes were committed.")
          }
          else{
              Ok()
          }
            }
          catch {
              case m:BadSqlGrammarException =>
                  logger.error("Problem during password recovery process",m)
                  trans.setRollbackOnly()
                  InternalServerError("There was a problem resetting the password. No changes were committed.")
          }
        }
    }
}
