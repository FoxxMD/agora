package controllers

import models._
import dao._
import play.api._
import com.googlecode.mapperdao.Query._
import play.api.mvc._
import dao.Daos._
import com.googlecode.mapperdao.jdbc.Transaction

object Team extends Controller {

  val tx = Transaction.default(txManager)
  def GetAll = Action { implicit request =>
    val teams = tx {() =>
      val q=(select from TeamEntity)
      queryDao.count(q);
    }
    Ok("There are " + teams + "teams")
  }
}
