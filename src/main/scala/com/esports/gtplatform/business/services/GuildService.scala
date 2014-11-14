package com.esports.gtplatform.business.services

import models.{GuildUser, Guild, User}

/**
 * Created by Matthew on 11/14/2014.
 */
class GuildService extends GuildServiceT {
    override def canJoin(gu: GuildUser): Boolean = ???

    override def isUnique(obj: Guild): Boolean = ???

    //def canCreate(user: User, id: Int): Boolean
    override def canDelete(user: User, obj: Guild): Boolean = ???

    override def canRead(user: User, obj: Guild): Boolean = ???

    //def canModify(user: User, id: Int): Boolean
    override def canCreate(user: User, obj: Guild): Boolean = ???

    //def canRead(user: User, id: Int): Boolean
    override def canModify(user: User, obj: Guild): Boolean = ???
}
