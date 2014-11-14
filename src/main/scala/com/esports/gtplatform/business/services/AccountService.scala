package com.esports.gtplatform.business.services

import com.esports.gtplatform.Utilities.PasswordSecurity
import com.esports.gtplatform.business.{PasswordTokenRepo, UserIdentityRepo, WebTokenRepo}
import com.esports.gtplatform.models.{PasswordToken}
import models.{User, UserIdentity}
import org.slf4j.LoggerFactory

/**
 * Created by Matthew on 11/12/2014.
 */
class AccountService(val passwordTokenRepo: PasswordTokenRepo,val userIdentRepo: UserIdentityRepo, val webTokenRepo: WebTokenRepo) extends AccountServiceT {
    val logger = LoggerFactory.getLogger(getClass)

    override def resetPassword(password: String, token: Option[PasswordToken], user: Option[User]): Unit ={
        var ident: Option[UserIdentity] = None
        val salted = PasswordSecurity.createHash(password)

        if(token.isDefined){
            ident = userIdentRepo.getByUser(token.get.userId).headOption
        }
        else if(user.isDefined){
            userIdentRepo.getByUser(user.get)
        }
        else{
            logger.error("No token or user was specified! Cannot reset password when there is no way to get the user's identity")
            throw new Exception("Token or User was not defined, cannot reset password")
        }
        userIdentRepo.update(ident.get.copy(password = Option(salted)))

/*        catch {
            case m:BadSqlGrammarException =>
                logger.error("Problem during password recovery process",m)
                trans.setRollbackOnly()
        }*/
    }

    override def generatePasswordToken(user: User): String = ???

    override def generateWebToken(ident: UserIdentity): String = ???
}
