package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.{GuildUserRepo, GuildRepo}
import models.{GuildUser, Guild, User}

/**
 * Created by Matthew on 11/14/2014.
 */
class GuildService(val guildRepo: GuildRepo, val guildUserRepo: GuildUserRepo, val userService: UserServiceT) extends GuildServiceT {

    override def canJoin(gu: GuildUser): Boolean = {
        val guild = guildRepo.get(gu.guildId.get).get
            guild.joinType != "invite" && (guild.maxPlayers.isEmpty || (guild.maxPlayers.isDefined && guild.members.size < guild.maxPlayers.get))
    }

    override def isUnique(obj: Guild): Boolean = guildRepo.getByName(obj.name).isEmpty

    override def canDelete(user: User, obj: Guild): Boolean = obj.getCaptain == user || userService.hasAdminPermissions(user)

    override def canRead(user: User, obj: Guild): Boolean = true

    override def canCreate(user: User, obj: Guild): Boolean = true

    override def canModify(user: User, obj: Guild): Boolean = obj.getCaptain == user || userService.hasAdminPermissions(user)
}
