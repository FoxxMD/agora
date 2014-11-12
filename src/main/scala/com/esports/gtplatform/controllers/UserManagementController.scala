package com.esports.gtplatform.controllers

import com.esports.gtplatform.Utilities.Mailer
import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services._
import com.esports.gtplatform.models.ConfirmationToken
import models.User
import org.scalatra.{BadRequest, Ok}
import scaldi.Injector

/**
 * Created by Matthew on 7/29/2014.
 */

/* I would suggest reading the comments in all other files as these comments only make sense once
 * you've got a grasp on the other components
 * */

//Mounted at /
class UserManagementController(override val userRepo: UserRepo,
                               override val userIdentRepo: UserIdentityRepo,
                               val eventUserRepo: EventUserRepo,
                               val registrationService: RegistrationServiceT,
                               val userService: UserServiceT,
                                  val eventService: EventServiceT,
                                  val confirmTokenRepo: ConfirmationTokenRepo,
                                  val accountService: AccountServiceT,
                                  val passwordTokenRepo: PasswordTokenRepo)(implicit val inj: Injector) extends BaseController with StandardController {

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
        authOpt()
        val noConfirm = parsedBody.\("noconfirm").extractOpt[Boolean]
        //you can get any parameters in the queryString of a POST or GET using params("key")

        //Use this method to extract values from a JSON object in the request
        val email = parsedBody.\("email").extract[String]
        val password = parsedBody.\("password").extract[String]
        val handle = parsedBody.\("handle").extract[String]
        val eventId = parsedBody.\("eventId").extractOpt[Int]

        val newuser = User(email = email, globalHandle = handle)
        val m = new Mailer()

        if(!registrationService.isUniqueHandle(newuser)) {
            halt(400, "Username is already taken.")
        }
            /*
            * We don't want to let the user know if an email is registered or not because that could be a potential
            * attack vector. Instead we send the address an email telling them someone has tried to use their address to register
            * and let the user know that an email was sent.
            * */
        else if(!registrationService.isUniqueEmail(newuser))
        {
            //TODO Need to generate a new confirmation token
            m.sendAlreadyRegistered(email)
            Ok()
        }
        else if(noConfirm.isDefined && noConfirm.get && (userService.hasAdminPermissions(user) || (eventId.isDefined && eventService.hasAdminPermissions(user, eventId.get))) ){
            registrationService.createActiveUser(user, password, eventId)
            Ok()
        }
        else{
            val token = registrationService.createInactiveUser(user, password, eventId)
            m.sendConfirm(email,handle, token)
            Ok()
        }
    }
    get("/confirmRegistration") {
        request.parameters.get("token") match {
            case Some(t: String) =>
                try {
                    Ok(registrationService.confirmInactiveUser(t))
                }
                catch{
                    case x: ConfirmationException =>
                        BadRequest("Confirmation token was not found! Have you already activated your account?")
                }
            case None =>
                BadRequest("No token provided.")
        }
    }
    post("/forgotPassword") {
        val email = parsedBody.\("email").extractOrElse[String](halt(400, "No email address specified."))
        val m = new Mailer()

        userRepo.getByEmail(email) match {
            case Some(u: User) =>
               val token = accountService.generatePasswordToken(u)
                m.sendForgotPassword(u.email, token)
            case None =>
                logger.warn("Request for password recovery with email address not in DB: " + email)
            case _ => logger.warn("No match found!")
        }
        Ok()
    }
    get("/passwordReset") {
        val token: String = params.getOrElse("token",halt(400,"No token received."))

        confirmTokenRepo.getByToken(token) match {
            case Some(c: ConfirmationToken) =>
                Ok()
            case None =>
                BadRequest("Token is invalid. Please make sure it is entered correctly.")
        }
    }
    post("/passwordReset") {
        val token = parsedBody.\("token").extractOrElse[String](halt(400,"No token specified"))
        val password = parsedBody.\("password").extractOrElse[String](halt(400,"No password specified"))

       accountService.resetPassword(password,Option(passwordTokenRepo.getByToken(token).getOrElse(halt(400, "Token is invalid."))))
    }
}
