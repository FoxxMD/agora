package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericRepo
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
    if(request.header("Authorization") == None)
      authUserPass()
    else
      authToken()
    Ok()
  }
  post("/register") {
    //you can get any parameters in the queryString of a POST or GET using params("key")
    val email = params("email")
    val password = params("password")

    val newuser = new User(email, "user", None, None, None, None)

    /*Option is Scala's way of dealing with empty results or null. Rather than returning nothing or null you can
    * return an Option type. Option contains either Some(data) or None. You can also wrap a value in Option()*/
    val userIdent = UserIdentity(newuser, "userpass", email, None, None, None, Option(email), None, password, "fsdf")

    //inject the GenericRepo trait with a concrete implementation of a repository for UserIdentity(MapperDao in this instance)
    val userRepo = inject[GenericRepo[UserIdentity]]

    Ok(userRepo.create(userIdent))
  }
}
