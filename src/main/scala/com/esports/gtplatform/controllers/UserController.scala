package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import org.scalatra.Ok

/**
 * Created by Matthew on 8/6/2014.
 */
class UserController(implicit val bindingModule: BindingModule) extends StandardController {

  get("/me")
  {
    Ok(authToken().get)
  }
}
