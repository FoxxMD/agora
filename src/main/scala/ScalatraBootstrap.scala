import javax.servlet.ServletContext

import com.escalatesoft.subcut.inject.NewBindingModule
import com.esports.gtplatform.business.{GenericMDaoTypedRepository, GenericRepo}
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.data.DatabaseInit
import dao._
import models._
import org.scalatra.LifeCycle

/* This is where we bootstrap the back-end service -- it's basically a "Global" file in any other context. Not much
*  needs to be changed here on a daily basis as it mostly for wiring up controllers and dependency injection.
*
*  */

class ScalatraBootstrap extends LifeCycle with DatabaseInit {

//  This object acts as the standard configuration for DI with Subcut.
  object StandardConfiguration extends NewBindingModule(module => {

  /* Each repository Trait that will be used gets wired up here to a concrete implementation(class) that actually does the work.
 *  GenericRepo is the Trait we're injecting, with a Generic parameter for the type of Domain Object being injected.
 *  GenericMDaoTypedRepository is the concrete implementation with Game as the generic parameter -- we also pass the corresponding mapperDao entity so it knows what to do later
 */
  //TODO move database configuration and init into this module
  module.bind[GenericRepo[Game]] toSingle new GenericMDaoTypedRepository[Game](GameEntity)
  module.bind[GenericRepo[UserIdentity]] toSingle new GenericMDaoTypedRepository[UserIdentity](UserIdentityEntity)

    }
  )

  //Eventually there will be different configurations depending on environment, such as for testing with mock repos.
  //object TestingConfiguration extends NewBindingModule(module =>)


  override def init(context: ServletContext) {
    //configureDb()

    //Here we assign our Standard Configuration for DI with subcut to the variable that will inject into our controllers
    implicit val bindingModule = StandardConfiguration

    //This is how we mount individual controllers to a route. Each controller's url argument is relative to this path.
    context.mount(new UserManagementController,"/")
    context.mount(new GameController,"/game")

  }
  
  override def destroy(context:ServletContext) {
    //closeDbConnection()
  }
}
