package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business._
import com.esports.gtplatform.business.services.GuildServiceT
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models._
import org.scalatra.{NotImplemented, BadRequest, Forbidden, Ok}

/**
 * Created by Matthew on 7/29/2014.
 */
class GuildController(val guildRepo: GuildRepo,
                      val guildUserRepo: GuildUserRepo,
                      val userRepo: UserRepo,
                      val guildService: GuildServiceT,
                      val guildGameRepo: GuildGameLinkRepo) extends APIController with GuildControllerT {
    get("/?") {
        val guilds = guildRepo.getPaginated(params.getOrElse("page", "1").toInt)
        Ok(guilds)
    }
    post("/") {
        auth()
        val extractedGuild = parsedBody.extract[Guild]
        if (!guildService.isUnique(extractedGuild))
            halt(400, "Guild with that name already exists")

        //TODO transaction
        val insertedGuild = guildRepo.create(extractedGuild)
        for (x <- parsedBody.\("games").extract[List[GuildGame]]) yield {
            //TODO validate gameId
            guildGameRepo.create(x.copy(guildId = insertedGuild.id))
        }
        guildUserRepo.create(GuildUser(guildId = extractedGuild.id, userId = user.id.get, isCaptain = true))

        Ok()

    }

    get("/:id") {
        Ok(requestGuild)
    }
    patch("/:id") {
        auth()
        if (!guildService.canModify(user, requestGuild))
            halt(403, "You do not have permission to modify this Guild")
        guildRepo.update(requestGuild)
        Ok()
    }
    delete("/:id") {
        auth()
        if (!guildService.canDelete(user, requestGuild))
            halt(403, "You do not have permission to delete this Guild")
        guildRepo.delete(requestGuild)
        Ok()
    }
    /*
     * Guild Users Functionality
     *
     */
    get("/:id/users") {
        Ok(guildUserRepo.getByGuild(requestGuild.id.get))
    }
    post("/:id/users") {
        auth()
        if (!guildService.canModify(user, requestGuild))
            halt(403, "You don't have permission to edit this Guild.")

        val extractedMember = parsedBody.extract[GuildUser]
        if(!guildService.canJoin(extractedMember))
            halt(403, "Already a Guild member or Guild is invite-only.")

        guildUserRepo.create(extractedMember)
        Ok()

    }
    delete("/:id/users/:guildUserId") {
        auth()
        if (user.id.get != params("guildUserId").toInt || !guildService.canDelete(user, requestGuild))
            halt(403, "You don't have permission to edit this team.")
        /*if (user.role != "admin" && requestGuild.getCaptain != user)
          halt(403, "You don't have permission to edit this team")*/
        //TODO after getting invites working...


    }
    patch("/:id/users/:guildUserId") {
        NotImplemented()
        //TODO implement
    }
    /*
     * Game Functionality
     *
     */
    get("/:id/games") {
        Ok(requestGuild.games)
    }
    post("/:id/games") {
        auth()
        if (!guildService.canModify(user, requestGuild))
            halt(403, "You don't have permission to edit this Guild.")

        val newGames = parsedBody.extractOpt[GuildGame].fold(parsedBody.extract[List[GuildGame]])(x => List(x))
        //TODO Validate gameId
        for(x <- newGames) yield {
            guildGameRepo.create(x)
        }
        Ok()
    }
    delete("/:id/games/:guildGameId") {

        auth()
        if (!guildService.canModify(user, requestGuild))
            halt(403, "You don't have permission to edit this team.")
        guildGameRepo.delete(params("guildGameId").toInt)
        Ok()
    }
}
