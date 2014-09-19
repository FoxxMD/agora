package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.googlecode.mapperdao.exceptions.QueryException
import models.GamePlatform
import models.GamePlatform.{GamePlatform}
import models.{GamePlatform, UserPlatformProfile}
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
    Ok(userRepo.getPaginated(params.getOrElse("page", "1").toInt))
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
  post("/:id/platforms") {
    auth()
    if (params("id").toInt != requestUser.get.id && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

    val platformType = parsedBody.\("platform").extract[GamePlatform]
    val identity = parsedBody.\("identifier").extract[String]

    if(requestUser.get.gameProfiles.exists(x => x.platform == platformType))
      halt(400, "User already has this platform added.")

    userRepo.update(requestUser.get, requestUser.get.addGameProfile(UserPlatformProfile(requestUser.get, platformType, identity)))
    Ok()
  }
  delete("/:id/platforms") {
    auth()
    if (params("id").toInt != requestUser.get.id && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")
    val pbody = parsedBody

    val platformType = GamePlatform.fromString(params("platform"))

    if(!requestUser.get.gameProfiles.exists(x => x.platform == platformType))
      halt(400, "User does not have a platform with this type to delete.")

    userRepo.update(requestUser.get, requestUser.get.removeGameProfile(platformType))
    Ok()
  }
  patch("/:id/platforms") {
    auth()
    if (params("id").toInt != requestUser.get.id && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

    val platformType = parsedBody.\("platform").extract[GamePlatform]
    val identity = parsedBody.\("identifier").extract[String]

    if(!requestUser.get.gameProfiles.exists(x => x.platform == platformType))
      halt(400, "User does not have a platform with this type to edit.")

    val editedPlatform = requestUser.get.gameProfiles.find(x => x.platform == platformType).get.copy(identifier = identity)

    userRepo.update(requestUser.get, requestUser.get.removeGameProfile(platformType).addGameProfile(editedPlatform))
    Ok()
  }
}
