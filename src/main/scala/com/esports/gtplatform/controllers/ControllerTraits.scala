package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.Injectable
import models.GameType
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{MethodOverride, CorsSupport, ScalatraServlet}
import org.scalatra.json._

/**
 * Created by Matthew on 7/24/2014.
 */

/*class MapperSerializer extends CustomSerializer[T with SurrogateIntId](format => (
  {
    PartialFunction.empty
  },
  {
    case x: T with SurrogateIntId =>
      ("id" -> x.id) ~
        ("thing" -> Extraction.decompose(x.))
  }
  ))*/

/*case object MapperSerializer extends CustomSerializer[SurrogateIntId](format =>
{
  case JString(s) => s.toInt
  case JNull => null
},
{
  case x: Int => JString(x.toString)
}
)*/


trait RESTController extends ScalatraServlet with JacksonJsonSupport with MethodOverride with Injectable {

  protected implicit val jsonFormats: Formats = DefaultFormats + new org.json4s.ext.EnumNameSerializer(GameType)
  before() {
    contentType = formats("json")
  }
}

trait StandardController extends RESTController with AuthenticationSupport with CorsSupport

trait APIController extends RESTController with AuthenticationSupport with CorsSupport

trait StandardWithOptAuth extends StandardController {
  before() {
    var rUser = authOptToken()
  }
}

trait StandardWithAuth extends StandardController {
  before() {
    var rUser = authToken()
  }
}
