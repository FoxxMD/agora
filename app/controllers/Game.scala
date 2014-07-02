package controllers

import models._
import dao._
import play.api._
import play.api.mvc._
import dao.Daos._
import com.googlecode.mapperdao.jdbc.Transaction

object Game extends Controller {

  val tx = Transaction.default(txManager)

    def CreateGame = Action(parse.text) { implicit request =>
    val body: String = request.body
      val inserted = tx { () =>
        val inserted = mapperDao.insert(GameEntity, new Game(body, "Test Publisher","somewebsite.com", GameType.FPS))
        inserted;
      }
      Ok("Game created " + inserted.name)
    }
}
