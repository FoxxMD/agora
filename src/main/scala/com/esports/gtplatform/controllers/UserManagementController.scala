package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.googlecode.mapperdao.jdbc.Transaction
import dao.Daos._
import dao.UserIdentityEntity
import models.{User, UserIdentity}
import org.scalatra.Ok

/**
 * Created by Matthew on 7/29/2014.
 */
class UserManagementController(implicit val bindingModule : BindingModule) extends StandardController {

  post("/login") {
    Ok(authUserPass().get)
  }
  post("/register"){
    val email = params("email")
    val password = params("password")

    val newuser = new User(email,"user",None,None,None,None)
    val userIdent = UserIdentity(newuser,"userpass",email,None,None,None,Option(email),None,password,"fsdf")

    val tx = Transaction.default(txManager)
    val inserted = tx {() =>
      mapperDao.insert(UserIdentityEntity, userIdent)
    }
    Ok(inserted.copy())
  }
}
