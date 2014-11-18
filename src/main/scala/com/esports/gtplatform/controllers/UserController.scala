package com.esports.gtplatform.controllers

import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business._
import models.{User, UserIdentity}
import org.json4s.Extraction
import org.json4s.JsonDSL._
import org.scalatra.{InternalServerError, Ok}
import scaldi.{Injectable, Injector}

/**
 * Created by Matthew on 8/6/2014.
 */

abstract class BaseController(implicit inj: Injector) extends BasicServletWithLogging with Injectable {
    val webTokenRepo = inject [WebTokenRepo]
    val apiKeyRepo = inject [ApiKeyRepo]
    val userIdentRepo = inject [UserIdentityRepo]
    val userRepo = inject [UserRepo]
}

class UserController(override val userRepo: UserRepo, override val userIdentRepo: UserIdentityRepo, val userPlatformRepo: UserPlatformRepo)(implicit val inj: Injector) extends BaseController with UserControllerT {

  get("/:id") {
      var userToGet: User = null
    if (params("id") == "me") {
      auth()
        userToGet = userRepo.getHydrated(user.id.get).get
    }
    else {
        userToGet = userRepo.getHydrated(requestUser.id.get).get
    }
      val jsonUser = Extraction.decompose(userToGet) merge render(
          //("email" -> userToGet.email) ~
              ("events" -> Extraction.decompose(userToGet.events)) ~
              ("tournaments" -> Extraction.decompose(userToGet.tournaments(event = None))) ~
              ("gameProfiles" -> Extraction.decompose(userToGet.gameProfiles)) ~
              ("guilds" -> Extraction.decompose(userToGet.guilds))) removeField {

          case("user", _) => true
          case _ => false
      }
      if(isAuthenticated && (paramId.isDefined && paramId.get == user.id.get) || (params("id") == "me"))
          jsonUser merge render("email" -> userToGet.email)
      Ok(jsonUser)
  }
  get("/") {
    Ok(userRepo.getPaginated(params.getOrElse("page", "1").toInt))
  }
  patch("/:id") {
    auth()
    if (params("id").toInt != requestUser.id.get && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

      val extractedUser = parsedBody.extract[User]

      //TODO transaction
      if(requestUser.email != extractedUser.email)
      {
         val ident = userIdentRepo.getByUser(extractedUser).find(x => x.email.isDefined)
          if(ident.isDefined)
          {
              logger.info("User " + requestUser.id + " is changing email, found an associated useridentity with userpass. Changing userident email.")
              userIdentRepo.update(ident.get.copy(email = Some(extractedUser.email)))
          }
      }
      userRepo.update(extractedUser)

      Ok()
  }
    delete("/:id"){
        auth()
        if(user.role != "admin" && user.id != requestUser.id)
            halt(403, "You don't have permission to delete this user")
        userRepo.delete(requestUser)
        Ok()
    }
  post("/:id/platforms") {
    auth()
    if (params("id").toInt != requestUser.id.get && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

    userPlatformRepo.create(parsedBody.extract)

/*    if(requestUser.gameProfiles.exists(x => x.platform == platformType))
      halt(400, "User already has this platform added.")*/

    Ok()
  }
  delete("/:id/platforms/:platformId") {
    auth()
    if (params("id").toInt != requestUser.id.get && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

      //TODO validate it's the user's platform object
    userIdentRepo.delete(params("platformId").toInt)
    Ok()
  }
  patch("/:id/platforms") {
    auth()
    if (params("id").toInt != requestUser.id.get && user.role != "admin")
      halt(403, "You don't have permission to edit this user.")

      userIdentRepo.update(parsedBody.extract)

    Ok()
  }
    post("/:id/password") {
        auth()
        val currentPass = parsedBody.\("current").extract[String]
        val newPass = parsedBody.\("new").extract[String]

        userIdentRepo.getByUser(requestUser).find(x => x.userIdentifier == "userpass") match {
            case Some(ident: UserIdentity) =>
                if(PasswordSecurity.validatePassword(currentPass, ident.password.get))
                {
                    val newIdent = ident.copy(password = Option(PasswordSecurity.createHash(newPass)))
                    userIdentRepo.update(ident)
                    Ok()
                }
                else{
                    logger.warn("User " + user.id + " tried to change their password but used an incorrect current password.")
                    halt(400, "Current password was not correct.")
                }
            case None =>
            logger.warn("No identity found for User " + requestUser.id)
            InternalServerError("Could not find identity for requested user. Something's wrong!")
        }
    }
}
