package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.Utilities.Mailer
import com.esports.gtplatform.business.services.NewUserService
import org.scalatra.{BadRequest, Ok}

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
    Ok(response.getHeader("Authorization"))
  }
  post("/register") {
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
        if(s == "handle")
          BadRequest("Username is already taken.")
        else if(s == "email")
        {
          m.sendAlreadyRegistered(email)
          /* This method is safer than notifying the user that an email address is already in use. We don't want to
          * give away that information so instead we say "OK sent." and then notify that email address that it is already
          * registered.
          */
          Ok("Registration successful. Please check your email for a confirmation link.")
        }
      case None =>
      val token = nuservice.create(newuser)
        if(eventId.isDefined)
          nuservice.associateEvent(token, eventId.get.toInt)
      logger.info("Non-active user successfully created: " + email)
      m.sendConfirm("matt.duncan13@gmail.com",handle, token)
      Ok("Registration successful. Please check your email for a confirmation link.")
    }
  }
  get("/confirmRegistration"){
    val nuservice = new NewUserService
    val token = request.parameters.get("token")
    token match {
      case Some(t: String) =>
        val confirmed = nuservice.confirmNewUser(t)
        if(confirmed._1)
          Ok(confirmed._2)
        else
          BadRequest("Could not find the provided token.")
      case None =>
        BadRequest("No token provided.")
    }
  }
}
