import javax.servlet.ServletContext

import com.escalatesoft.subcut.inject.NewBindingModule
import com.esports.gtplatform.business.{GenericMDaoTypedRepository, GenericRepo}
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.data.DatabaseInit
import dao.GameEntity
import models.Game
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle with DatabaseInit {

  object StandardConfiguration extends NewBindingModule(module =>

  module.bind [GenericRepo[Game]] toSingle new GenericMDaoTypedRepository[Game](GameEntity)

  )


  override def init(context: ServletContext) {
    //configureDb()
    implicit val bindingModule = StandardConfiguration
    context.mount(new UserManagementController,"/")
    context.mount(new GameController,"/game")

  }
  
  override def destroy(context:ServletContext) {
    //closeDbConnection()
  }
}
