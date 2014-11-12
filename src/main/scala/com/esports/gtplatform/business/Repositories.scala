package com.esports.gtplatform.business

import com.escalatesoft.subcut.inject.{AutoInjectable, BindingModule, Injectable}
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Query._
import io.strongtyped.active.slick.models.Identifiable
import io.strongtyped.active.slick.{ActiveSlick, TableQueries, Tables}
import models.Tournament
import org.slf4j.LoggerFactory
import scala.reflect.ClassTag
import scala.slick.driver.JdbcDriver
import scala.slick.jdbc.JdbcBackend
import scala.slick.jdbc.JdbcBackend.{SessionDef, Database}
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import models._

/**
 * Created by Matthew on 7/29/2014.
 */


trait SqlAccessRepository extends SqlAccess{

  def lowLevelQuery[T](query: String, args: List[Any]): List[T] = Q.queryNA[T](query).list
  def lowLevelUpdate(query: String, args: List[Any]): Unit = Q.updateNA(query).execute
}
/*trait GenericComponent extends CrudComponent{outer: Profile =>
    import simple._

}*/
