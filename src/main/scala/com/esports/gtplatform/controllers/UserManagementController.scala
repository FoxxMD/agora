package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business.{GenericRepo, UserRepo}
import models.{User, UserIdentity}
import org.scalatra.Ok

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
    //you can get any parameters in the queryString of a POST or GET using params("key")

    //creating a user repository so we can check if this email address is already in use
    val userRepo = inject[UserRepo]

    //Use this method to extract values from a JSON object in the request
    val email = parsedBody.\("email").extract[String]
    val password = parsedBody.\("password").extract[String]

    //Dont't create a new user if they already exist.
    if (!userRepo.getByEmail(email).isDefined) {
      val newuser = User(email, "user", None, None, None, None)

      /*Option is Scala's way of dealing with empty results or null. Rather than returning nothing or null you can
       * return an Option type. Option contains either Some(data) or None. You can also wrap a value in Option()
       * */
      val userIdent = UserIdentity(newuser, "userpass", email, None, None, None, Option(email), None, PasswordSecurity.createHash(password))

      //inject the GenericRepo trait with a concrete implementation of a repository for UserIdentity(MapperDao in this instance)
      val userIdentRepo = inject[GenericRepo[UserIdentity]]
      userIdentRepo.create(userIdent)

      //TODO add email confirmation.
      Ok("Registration successful. Please check your email for a confirmation link.")
    }
  }
}
