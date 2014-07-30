package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.GenericRepo
import models.Game
import org.scalatra.Ok

/**
 * Created by Matthew on 7/24/2014.
 */

/* I would suggest reading the comments in all other files as these comments only make sense once
 * you've got a grasp on the other components
 * */
//
class GameController(implicit val bindingModule: BindingModule) extends StandardController { //Extends traits for Authentication, CORS, JSON support, and DI
//bindingModule for injection
  //inject repository implementation into GenericRepo trait
  val gameRepo = inject[GenericRepo[Game]]

get("/") {
//Full path is "/games/" because of relative mounting

  //Optional Authentication and return a User
  val anyuser = authOptToken()

  val games = gameRepo.getAll

  /* The BIGGEST and only caveat right now is that objects returned from the DB are returned wrapped in their DAOs.
  * I've talked to MapperDao's author and he's working on a solution right now but the crux of it is that Jackson can't
  * convert wrapped case classes into JSON, it just doesn't know that it's the case class.
  *
  * So to get around it you have to return a copy of the object using copy()
  * Below I am returning a list of returned copies using map()
  *
  * Sorry bout it! We'll figure it out eventually. And even if not it's not the biggest problem in the world(though it surely annoys me)
  * */

  Ok(games.map(x => x.copy()))
}

post("/") {

  //Extracting JSON to a domain object ONLY WORKS IF THE OBJECT IS A CASE CLASS. Otherwise we must provide a custom serializer.
  //Just another reason to make all domain objects case classes.
  val newgame = parsedBody.extract[Game]

  val newInserted = gameRepo.create(newgame)

  Ok(newInserted.copy())

}

}
