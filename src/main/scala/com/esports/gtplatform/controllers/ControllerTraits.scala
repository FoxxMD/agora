package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.Injectable
import models.GameType
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{MethodOverride, CorsSupport, ScalatraServlet}
import org.scalatra.json._

/**
 * Created by Matthew on 7/24/2014.
 */

/*Traits are the shit. They are similar to interfaces except they can have properties and defined methods.
* Use traits to build characteristics for a class or object. They can be mixed in using "extend" and then "with" for multiple mixins.
*
* In this file we are building the characteristics that we will make up a controller. */

trait RESTController extends ScalatraServlet with JacksonJsonSupport with MethodOverride with Injectable {
  /*ScalatraServlet = base for HTTP interaction in Scalatra
  * JacksonJsonSupport = Provide support for converting responses into JSON and conversion between JSON
  * MethodOverride = Add support for non-standard PUT/PATCH verbs
  * Injectable = Support for injecting dependencies using subcut */

  //Providing conversion between primitives and JSON, with added support for serializing the GameType enumeration.
  //Eventually will have to add support for all Enumeration types used.
   protected implicit val jsonFormats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(GameType)
  before() {
    //Lets the controller know to format the response in json so we don't have to specify on each action.
    contentType = formats("json")
  }
}

/*Add support for Authentication and CORS.

 * Authentication can be accessed by using one of the authentication methods in AuthStrategy -- authToken, authOptToken, or authUserPass
 *
 * The reason these two are the same is that we will eventually remove CorsSupport
 * so only APIControllers can be accessed outside of the domain. For dev it's easier to have both totally open.
 *
*/
trait StandardController extends RESTController with AuthenticationSupport with CorsSupport
trait APIController extends RESTController with AuthenticationSupport with CorsSupport

//Provide support for user-aware auth on every action
trait StandardWithOptAuth extends StandardController {
  before() {
    var rUser = authOptToken()
  }
}
//Add mandatory authentication on every action
trait StandardWithAuth extends StandardController {
  before() {
    var rUser = authToken()
  }
}
