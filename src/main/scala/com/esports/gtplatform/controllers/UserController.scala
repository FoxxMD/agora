package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import org.scalatra.Ok

/**
 * Created by Matthew on 8/6/2014.
 */
class UserController(implicit val bindingModule: BindingModule) extends UserControllerT {
  get("/:id") {
    if(params("id") == "me")
    {
      auth()
      Ok(user)
    }
    else{
      Ok(requestUser.get)
    }
  }
  get("/") {
    Ok(userRepo.getAll) //TODO pagination!
  }
}
