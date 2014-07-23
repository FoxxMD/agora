/**
 * Copyright 2014 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.lang.reflect.Constructor

import securesocial.controllers.{MailTemplates, ViewTemplates}
import securesocial.core._
import models.User
import securesocial.core.authenticator.{AuthenticatorStore, CookieAuthenticatorBuilder, HttpHeaderAuthenticatorBuilder, IdGenerator}
import securesocial.core.providers._
import securesocial.core.providers.utils.{Mailer, PasswordHasher, PasswordValidator}
import securesocial.core.services._
import service.gtUserService

import scala.collection.immutable.ListMap

object Global extends play.api.GlobalSettings {

  /**
   * The runtime environment for this sample app.
   */
  object MyRuntimeEnvironment extends RuntimeEnvironment.GT[User] {
    /*override lazy val routes = new CustomRoutesService()*/
    override lazy val userService: gtUserService = new gtUserService()
    /*override lazy val eventListeners = List(new MyEventListener())*/
  }

/*  object SSConfig extends NewBindingModule({ module =>
  import module._

     bind [RuntimeEnvironment] toSingle new GT[User] {
       override lazy val userService: gtUserService = new gtUserService()
     }
    bind [controllers.MyRegisterApi] toModuleSingle { implicit module => new controllers.MyRegisterApi }
  })

  object Context extends Injectable {
    implicit val bindingModule = SSConfig  // use the standard config by default

    val application = inject[controllers.MyRegisterApi]
    val applicationClass = classOf[controllers.MyRegisterApi]
  }*/

  /**
   * An implementation that checks if the controller expects a RuntimeEnvironment and
   * passes the instance to it if required.
   *
   * This can be replaced by any DI framework to inject it differently.
   *
   * @param controllerClass
   * @tparam A
   * @return
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance  = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[User]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(MyRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }
/*  override def getControllerInstance[A](controllerClass: Class[A]): A = {

    controllerClass match {
      case Context.applicationClass => Context.application.asInstanceOf[A]
      case _ => throw new IllegalArgumentException

    }
  }*/
}

object RuntimeEnvironment {

  abstract class GT[U] extends RuntimeEnvironment[U] {
    override lazy val routes: RoutesService = new RoutesService.Default()

    override lazy val viewTemplates: ViewTemplates = new ViewTemplates.Default(this)
    override lazy val mailTemplates: MailTemplates = new MailTemplates.Default(this)
    override lazy val mailer: Mailer = new Mailer.Default(mailTemplates)

    override lazy val currentHasher: PasswordHasher = new PasswordHasher.Default()
    override lazy val passwordHashers: Map[String, PasswordHasher] = Map(currentHasher.id -> currentHasher)
    override lazy val passwordValidator: PasswordValidator = new PasswordValidator.Default()

    override lazy val httpService: HttpService = new HttpService.Default()
    override lazy val cacheService: CacheService = new CacheService.Default()
    override lazy val avatarService: Option[AvatarService] = Some(new AvatarService.Default(httpService))
    override lazy val idGenerator: IdGenerator = new IdGenerator.Default()

    override lazy val authenticatorService = new AuthenticatorService(
      new CookieAuthenticatorBuilder[U](new AuthenticatorStore.Default(cacheService), idGenerator),
      new HttpHeaderAuthenticatorBuilder[U](new AuthenticatorStore.Default(cacheService), idGenerator)
    )

    override lazy val eventListeners: List[EventListener[U]] = List()

    protected def include(p: IdentityProvider) = p.id -> p

    protected def oauth1ClientFor(provider: String) = new OAuth1Client.Default(ServiceInfoHelper.forProvider(provider), httpService)

    protected def oauth2ClientFor(provider: String) = new OAuth2Client.Default(httpService, OAuth2Settings.forProvider(provider))

    override lazy val providers = ListMap(
      // oauth 2 client providers
      //include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
      //include(new GoogleProvider(routes, cacheService,oauth2ClientFor(GoogleProvider.Google))),
      // username password
      include(new UsernamePasswordProvider[U](userService, avatarService, viewTemplates, passwordHashers))
    )
  }

}