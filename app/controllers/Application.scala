package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }
/*  def templateDemo = Action {
    Ok(views.html.demo("Scala template in Angular")
      (Html("This is a play scala template in angular views folder which is compiled and used inplace!</div>"))
    )
  }

  def serveTemplate = Action {
    Ok(views.html.templ("Compiled from a scala template!"))
  }*/

}