package com.esports.gtplatform.business.services

/**
 * Created by Matthew on 8/20/2014.
 */

import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business._
import com.esports.gtplatform.models.ConfirmationToken
import models.{EventUser, User, UserIdentity, Event}
import org.slf4j.LoggerFactory
class ConfirmationException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
class RegistrationService(val eventUserRepo: EventUserRepo, val inactiveUserRepo: NonActiveUserRepo, val inactiveIdentRepo: NonActiveUserIdentityRepo, val userRepo: UserRepo, val userIdentRepo: UserIdentityRepo, val confirmTokenRepo: ConfirmationTokenRepo) extends RegistrationServiceT {

    val logger = LoggerFactory.getLogger(getClass)

    def isUniqueEmail(user: User): Boolean = {
        userRepo.getByEmail(user.email).isEmpty && inactiveUserRepo.getByEmail(user.email).isEmpty
    }

    def isUniqueHandle(user: User): Boolean = {
        userRepo.getByHandle(user.globalHandle).isEmpty && inactiveUserRepo.getByHandle(user.email).isEmpty
    }

    def createInactiveUser(user: User, password: String, eventId: Option[Int] = None): String = {
        val token = java.util.UUID.randomUUID.toString
        val salted = PasswordSecurity.createHash(password)

        //TODO transaction
        val insertedUser = inactiveUserRepo.create(user)
        val insertedIdent = inactiveIdentRepo.create(UserIdentity(
            userId = insertedUser.id.get,
            userIdentifier = "userpass",
            providerId = insertedUser.email,
            email = Some(insertedUser.email),
            password = Some(salted)))

        logger.info("[Registration] NON-Active User " + insertedUser.email + " created.")

        val confirmToken = confirmTokenRepo.create(ConfirmationToken(insertedIdent.id.get,token, eventId))

        if(eventId.isDefined)
            logger.info("[Registration] Confirmation token for " + insertedUser.email + " inserted, associated with Event " + eventId)
        else
            logger.info("[Registration] Confirmation token for " + insertedUser.email + " inserted")

        confirmToken.token

    }
    def createActiveUser(user: User, password: String, eventId: Option[Int] = None): Unit = {
        val token = java.util.UUID.randomUUID.toString
        val salted = PasswordSecurity.createHash(password)

        //TODO transaction
        val insertedUser = userRepo.create(user)
        val insertedIdent = userIdentRepo.create(UserIdentity(
            userId = insertedUser.id.get,
            userIdentifier = "userpass",
            providerId = insertedUser.email,
            email = Some(insertedUser.email),
            password = Some(salted)))

        logger.info("[Registration] Active User " + insertedUser.email + " created.")

        if(eventId.isDefined)
        {
            logger.info("[Registration] Event Id defined, attempting to create EventUser for Event " + eventId)
            eventUserRepo.create(EventUser(userId = insertedUser.id.get, eventId = eventId.get))
            logger.info("[Registration] EventUser created.")
        }

    }


    def confirmInactiveUser(token: String): Option[Int] = {

        logger.info("[Registration] Looking for confirmation token " + token)
        val confirmToken = confirmTokenRepo.getByToken(token).getOrElse{
            logger.warn("[Registration] Confirmation token not found!")
            throw new ConfirmationException
        }
        val eventId = confirmToken.eventId
        logger.info("[Registration] Confirmation token found.")

        val ident = inactiveIdentRepo.get(confirmToken.userIdentId).get
        inactiveIdentRepo.delete(ident)
        val user = inactiveUserRepo.get(ident.userId).get
        inactiveUserRepo.delete(user)

        logger.info("[Registration] Found inactive user, transferring to active")

        val insertedUser = userRepo.create(user.copy(id = None))
        userIdentRepo.create(ident.copy(userId = insertedUser.id.get))

        logger.info("[Registration] User transferred!")

        if(eventId.isDefined){
            logger.info("[Registration] Event " +eventId.get + " found with confirmation token, attempting to create EventUser for new User " + insertedUser.id.get)
           val eventUser = eventUserRepo.create(EventUser(eventId = eventId.get, userId = insertedUser.id.get))
            logger.info("[Registration] EventUser " + eventUser.id.get + " created" )
        }

        confirmTokenRepo.delete(confirmToken)
        eventId
    }
}
