package com.esports.gtplatform.business

import ScalaBrackets.Bracket.ElimTour
import com.esports.gtplatform.dao.SquerylDao._
import com.esports.gtplatform.models._
import com.novus.salat.dao.SalatDAO
import models._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Table


/**
 * Created by Matthew on 7/29/2014.
 */

class SquerylTransaction extends TransactionSupport {
    def transaction[A](a: => A): A = inTransaction {
        a
    }
}

class GenericSquerylRepository[T <: DomainEntity[T]](theTable: Table[T]) extends GenericRepoIntId[T] {

    import com.esports.gtplatform.dao.Squreyl._

    def table = theTable

    def create(entity: T) = inTransaction(table.insert(entity))

    def update(entity: T) = {
        inTransaction(table.update(entity))
        entity
    }

    def getAll: List[T] = inTransaction(table.allRows.toList)

    def delete(entity: T) = inTransaction(table.delete(entity.id.get))

    def findById(id: Int): Option[T] = inTransaction(table.where(f => f.id.get === id).singleOption)

    override def get(id: Int): Option[T] = inTransaction {
        table.lookup(id)
    }

    override def delete(id: Int): Unit = inTransaction(table.delete(id))

    override def getPaginated(pageNo: Int, pageSize: Int): List[T] = inTransaction(from(table)(a => select(a) orderBy (a.id asc)).page(pageNo, pageSize).toList)
}

class GameRepository extends GenericSquerylRepository[Game](games) with GameRepo {

    override def getByName(name: String) = inTransaction(from(games)(g => where(g.name === name) select g).singleOption)
}

class GameTTLinkRepository extends GenericSquerylRepository[GameTournamentType](gamettLink) with GameTTLinkRepo

class WebTokenRepository extends GenericSquerylRepository[WebToken](webTokens) with WebTokenRepo {
    override def getByToken(token: String): Option[WebToken] = inTransaction(webTokens.where(w => w.token === token).singleOption)

    override def getByUser(id: Int): Option[WebToken] = inTransaction(webTokens.where(w => w.userId === id).singleOption)
}

class ApiKeyRepository extends GenericSquerylRepository[ApiKey](apiKeys) with ApiKeyRepo

class ConfirmationTokenRepository extends GenericSquerylRepository[ConfirmationToken](confirmationTokens) with ConfirmationTokenRepo {

    override def getByToken(token: String): Option[ConfirmationToken] = inTransaction(confirmationTokens.where(x => x.token === token).singleOption)

    //override def getByUser(user: User): Option[ConfirmationToken] = confirmationTokens.where(x => x.userIdentId === user.)
}

class PasswordTokenRepository extends GenericSquerylRepository[PasswordToken](passwordTokens) with PasswordTokenRepo {
    override def getByToken(token: String): Option[PasswordToken] = inTransaction(passwordTokens.where(x => x.token === token).singleOption)
}

class UserRepository extends GenericSquerylRepository[User](users) with UserRepo {

    override def getByEmail(email: String): Option[User] = inTransaction(users.where(u => u.email === email).singleOption)

    override def getByHandle(handle: String): Option[User] = inTransaction(users.where(u => u.globalHandle === handle).singleOption)

    override def getHydrated(id: Int): Option[User] = {
        val data = inTransaction {
            join(users, eventUsers.leftOuter, guildUsers.leftOuter, teamUsers.leftOuter, tournamentUsers.leftOuter, userPlatformProfiles.leftOuter)((u, eu, gu,teamU, tourU, profile) =>
                where(u.id === id)
                    select(u, eu, gu, teamU, tourU, profile)
                    on(u.id === eu.map(x => x.userId), u.id === gu.map(x => x.userId), u.id === teamU.map(x => x.userId), u.id === tourU.map(x => x.userId), u.id === profile.map(x => x.userId))).toList
        }
        if(data.isEmpty)
            return None

        val user =  data.head._1
        user.events(data.flatMap(x => x._2))
        user.guilds(data.flatMap(x => x._3))
        user.teams(data.flatMap(x => x._4))
        user.tournaments(data.flatMap(x => x._5))
        user.gameProfiles(data.flatMap(x => x._6))
        Option(user)
    }
}

class UserIdentityRepository extends GenericSquerylRepository[UserIdentity](userIdents) with UserIdentityRepo {

    override def getByUser(user: User): List[UserIdentity] = inTransaction(getByUser(user.id.get))

    override def getByUser(id: Int): List[UserIdentity] = inTransaction(userIdents.where(u => u.userId === id).toList)

    override def getByUserPass(email: String): Option[UserIdentity] = inTransaction(userIdents.where(u => u.email === email).singleOption)
}

class UserPlatformRepository extends GenericSquerylRepository[UserPlatformProfile](userPlatformProfiles) with UserPlatformRepo

class GuildRepository extends GenericSquerylRepository[Guild](guilds) with GuildRepo {

    override def getByName(name: String): Option[Guild] = inTransaction(guilds.where(x => x.name === name).singleOption)
}

class GuildUserRepository extends GenericSquerylRepository[GuildUser](guildUsers) with GuildUserRepo {

    override def getByGuild(id: Int): List[GuildUser] = inTransaction(guildUsers.where(x => x.guildId === id).toList)
}

class GuildGameRepository extends GenericSquerylRepository[GuildGame](guildGames) with GuildGameLinkRepo

class TournamentRepository extends GenericSquerylRepository[Tournament](tournaments) with TournamentRepo {

    override def getByEvent(id: Int): List[Tournament] = inTransaction(tournaments.where(x => x.eventId === id).toList)

    override def getByName(name: String): Option[Tournament] = ???
}

class TournamentDetailRepository extends GenericSquerylRepository[TournamentDetail](tournamentDetails) with TournamentDetailsRepo {
    override def getByTournament(id: Int): Option[TournamentDetail] = inTransaction(tournamentDetails.where(x => x.tournamentId === id).singleOption)
}

class TournamentUserRepository extends GenericSquerylRepository[TournamentUser](tournamentUsers) with TournamentUserRepo {

    override def getByTournament(id: Int): List[TournamentUser] = inTransaction(tournamentUsers.where(x => x.tournamentId === id).toList)

    override def getByTournament(tournament: Tournament): List[TournamentUser] = getByTournament(tournament.id.get)

    override def getByUser(u: User): List[TournamentUser] = inTransaction(tournamentUsers.where(x => x.userId === u.id).toList)
}

class TournamentTypesRepository extends GenericSquerylRepository[TournamentType](tournamentTypes) with TournamentTypeRepo

class EventRepository extends GenericSquerylRepository[Event](events) with EventRepo {
    private[this] val eventDetailRepo: EventDetailRepo = new EventDetailsRepository

    override def getByName(name: String): Option[Event] = inTransaction(events.where(x => x.name === name).singleOption)

    override def getHydrated(id: Int): Option[Event] = {
        val data = inTransaction{
         join(events, eventUsers.leftOuter, tournaments.leftOuter)((e, eu, t) =>
         where(e.id.get === id)
            select(e,eu,t)
         on(e.id.get === eu.map(x => x.eventId), e.id.get === t.map(x => x.eventId))).toList
        }
        if(data.isEmpty)
            return None
        val event = data.head._1
        val unzipped = data.unzip3
        event.users(unzipped._2.flatMap(x => x))
        event.tournaments(unzipped._3.flatMap(x => x))
        //event.details(eventDetailRepo.get(event.id.get).get)
        Option(event)
    }
}

class EventDetailsRepository extends GenericSquerylRepository[EventDetail](eventDetails) with EventDetailRepo

class EventUserRepository extends GenericSquerylRepository[EventUser](eventUsers) with EventUserRepo {

    override def getByEvent(id: Int): List[EventUser] = inTransaction(eventUsers.where(x => x.eventId === id).toList)

    def getByEventHydrated(id: Int): List[EventUser] = {
        val data = inTransaction {
            join(eventUsers, users.leftOuter, events.leftOuter)((eu, u, e) =>
                where(eu.eventId === id)
                    select(eu, u, e)
                    on(eu.userId === u.map(x => x.id.get), eu.eventId === e.map(x => x.id.get))).toList
        }
        for (d <- data) yield {

            d._1.copy(_event = d._3, _user = d._2)
        }
    }

    def getByUserHydrated(id: Int): List[EventUser] = {
        val data = inTransaction {
            join(eventUsers, users.leftOuter, events.leftOuter)((eu, u, e) =>
                where(eu.userId === id)
                select(eu, u, e)
                    on(eu.userId === u.map(x => x.id.get), eu.eventId === e.map(x => x.id.get))).toList
        }
        for (d <- data) yield {

            d._1.copy(_event = d._3, _user = d._2)
        }
    }

    override def getByEventAndUser(eventId: Int, userId: Int): Option[EventUser] = inTransaction(eventUsers.where(x => x.eventId === eventId and x.userId === userId).singleOption)

    override def getByUser(u: User): List[EventUser] = inTransaction(eventUsers.where(x => x.userId === u.id).toList)
}

class EventPaymentRepository extends GenericSquerylRepository[EventPayment](eventPayments) with EventPaymentRepo {
    override def getByEvent(id: Int): List[EventPayment] = inTransaction(eventPayments.where(x => x.eventId === id).toList)

    override def getBySecret(key: String): List[EventPayment] = inTransaction(eventPayments.where(x => x.secretKey === key).toList)
}

class TeamRepository extends GenericSquerylRepository[Team](teams) with TeamRepo {

    //def getByEvent: List[Team]
    override def getByGuild(id: Int): List[Team] = inTransaction(teams.where(x => x.guildOnly === true and x.guildId === id).toList)

    override def getByTournament(id: Int): List[Team] = inTransaction(teams.where(x => x.tournamentId === id).toList)

    override def getByName(name: String): Option[Team] = inTransaction(teams.where(x => x.name === name).singleOption)
}

class TeamUserRepository extends GenericSquerylRepository[TeamUser](teamUsers) with TeamUserRepo {

    private[this] val teamRepo: TeamRepo = new TeamRepository

    override def getByTeam(id: Int): List[TeamUser] = inTransaction(teamUsers.where(x => x.id === id).toList)

    override def getByEvent(id: Int): List[TeamUser] = ???

    override def getByUser(u: User): List[TeamUser] = inTransaction(teamUsers.where(x => x.userId === u.id).toList)

    override def getByTournament(id: Int): List[TeamUser] = teamRepo.getByTournament(id).flatMap(x => x.teamPlayers)
}

//non-active repository
class NonActiveUserRepository extends GenericSquerylRepository[User](nonActiveUsers) with NonActiveUserRepo {

    override def getByEmail(email: String): Option[User] = inTransaction(users.where(u => u.email === email).singleOption)

    override def getByHandle(handle: String): Option[User] = inTransaction(users.where(u => u.globalHandle === handle).singleOption)
}

class NonActiveUserIdentityRepository extends GenericSquerylRepository[UserIdentity](nonActiveUserIdents) with NonActiveUserIdentityRepo {

    override def getByUser(user: User): List[UserIdentity] = inTransaction(getByUser(user.id.get))

    override def getByUser(id: Int): List[UserIdentity] = inTransaction(userIdents.where(u => u.userId === id).toList)

    override def getByUserPass(email: String): Option[UserIdentity] = inTransaction(userIdents.where(u => u.email === email).singleOption)
}

class BracketRepository extends GenericRepo[ElimTour, String] {
    import com.mongodb.casbah.Imports._
    import com.novus.salat._
    import com.novus.salat.global._

    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("bracketDB")
    val coll = db("brackets")
    object BracketDAO extends SalatDAO[ElimTour, String](collection = MongoConnection()("bracket_db")("bracket_coll"))

    override def get(id: String): Option[ElimTour] = {

        //coll.findOneByID(MongoDBObject("_id" -> id)).headOption.fold[Option[ElimTour]](None){x => Option(grater[ElimTour].asObject(x))}
        BracketDAO.findOneById(id = id)
    }

    override def update(obj: ElimTour): ElimTour = {

        coll.findAndModify(MongoDBObject("_id" -> obj.id), grater[ElimTour].asDBObject(obj))
         obj//findOneByID(MongoDBObject("_id" -> obj._id)).head.up //.fold[Option[ElimTour]](None){x => Option(grater[ElimTour].asObject(x))}
    }

    override def delete(id: String): Unit = {
        BracketDAO.removeById(id)
        //coll.findAndRemove(MongoDBObject("_id" -> id))
    }

    override def delete(obj: ElimTour): Unit = {
        BracketDAO.removeById(obj.id)
        //coll.findAndRemove(MongoDBObject("_id" -> obj._id))
    }

    override def getPaginated(pageNo: Int, pageSize: Int): List[ElimTour] = ???

    override def create(obj: ElimTour): ElimTour = {
        val insertedId = BracketDAO.insert(obj)
        obj.copy(id = insertedId.get)
       //coll.insert(grater[ElimTour].asDBObject(obj.copy(_id = BSONObjectId.generate)))
    }

    override def getAll: List[ElimTour] = ???
}
