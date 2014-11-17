package com.esports.gtplatform.business.services

import com.esports.gtplatform.business.UserRepo
import models.User

/**
 * Created by Matthew on 11/17/2014.
 */
class UserService(val userRepo: UserRepo) extends UserServiceT {

    override def hasAdminPermissions(user: User): Boolean = user.role == "admin"

    override def hasModeratorPermissions(user: User): Boolean = user.role =="admin" || user.role == "moderator"

    override def isUnique(obj: User): Boolean = userRepo.getByHandle(obj.globalHandle).isEmpty && userRepo.getByEmail(obj.email).isEmpty
}
