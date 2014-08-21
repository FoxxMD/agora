import javax.servlet.ServletContext

import com.escalatesoft.subcut.inject.NewBindingModule
import com.esports.gtplatform.business._
import com.esports.gtplatform.controllers._
import com.esports.gtplatform.data.DatabaseInit
import com.googlecode.mapperdao.jdbc.Transaction
import dao.Daos._
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
  module.bind[SqlAccess] toSingle new SqlAccessRepository
  module.bind[GenericMRepo[Game]] toSingle new GenericMRepository[Game](GameEntity)
  module.bind[GenericMRepo[UserIdentity]] toSingle new GenericMRepository[UserIdentity](UserIdentityEntity)
  module.bind[GenericMRepo[Team]] toSingle new GenericMRepository[Team](TeamEntity)
  module.bind[GenericMRepo[TeamUser]] toSingle new GenericMRepository[TeamUser](TeamUserEntity)
  module.bind[GenericMRepo[User]] toSingle new GenericMRepository[User](UserEntity)
  module.bind[UserRepo] toSingle new UserRepository(UserEntity)
  //Created a separate set of tables/repositories for non-confirmed users.
  module.bind[NonActiveUserIdentityRepo] toSingle new NonActiveUserIdentityRepository
  module.bind[NonActiveUserRepo] toSingle new NonActiveUserRepository

  module.bind[Transaction] toSingle { Transaction.default(Transaction.transactionManager(jdbc)) }
    }
  )

  //Eventually there will be different configurations depending on environment, such as for testing with mock repos.
  //object TestingConfiguration extends NewBindingModule(module =>)


  override def init(context: ServletContext) {
    //configureDb()

    //Here we assign our Standard Configuration for DI with subcut to the variable that will inject into our controllers
    implicit val bindingModule = StandardConfiguration

    //This is how we mount individual controllers to a route. Each controller's url argument is relative to this path.
    context.mount(new UserManagementController,"/api/")
    context.mount(new GameController,"/api/games")
    context.mount(new TeamController,"/api/teams")
    context.mount(new UserController,"/api/users")

  }
  
  override def destroy(context:ServletContext) {
    //closeDbConnection()
  }
}
