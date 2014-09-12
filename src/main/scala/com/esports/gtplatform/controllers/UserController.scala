package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import org.scalatra.Ok

/**
 * Created by Matthew on 8/6/2014.
 */
class UserController(implicit val bindingModule: BindingModule) extends UserControllerT {
  get("/:id") {
    if (params("id") == "me") {
      auth()
      Ok(user)
    }
    else {
      Ok(requestUser.get)
    }
  }
  get("/") {
    Ok(userRepo.getAll) //TODO pagination!
  }
  patch("/:id") {
    auth()

    if (params("id").toInt != requestUser.get.id && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")
    val handle = parsedBody.\("globalHandle").extractOpt[String]
    val email = parsedBody.\("email").extractOpt[String]

    var editUser = requestUser.get.copy()
    if (handle.isDefined)
      editUser = user.copy(globalHandle = handle.get)
    if (email.isDefined)
      editUser = user.copy(email = email.get)
    userRepo.update(requestUser.get, editUser)
    Ok()
  }
}
