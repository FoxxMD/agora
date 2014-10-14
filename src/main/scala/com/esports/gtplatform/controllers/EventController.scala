package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.services.{PaymentService, StripePayment}
import com.esports.gtplatform.business._
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models.JoinType.JoinType
import models.PaymentType.PaymentType
import models._
import org.joda.time.DateTime
import org.json4s.Extraction
import org.scalatra.{BadRequest, NotImplemented, Ok}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(implicit val bindingModule: BindingModule) extends APIController with EventControllerT {

    get("/") {
        val events = eventRepo.getPaginated(params.getOrElse("page", "1").toInt)
        Ok(events)
    }
    post("/") {
        auth()
        val newEvent: Event = Event(parsedBody.\("name").extract[String], parsedBody.\("joinType").extract[JoinType])
        if (eventRepo.getByName(newEvent.name).isDefined)
            halt(400, "Event with this name already exists.")

        val tx = inject[Transaction]
        val eventUserRepo = inject[GenericMRepo[EventUser]]
        val inserted = tx { () =>

            val insertedEvent = eventRepo.create(newEvent)
            val eventDetails = EventDetails(insertedEvent, timeStart = parsedBody.\("details").\("timeStart").extractOpt[DateTime], timeEnd = parsedBody.\("details").\("timeEnd").extractOpt[DateTime])
            eventRepo.update(insertedEvent, insertedEvent.setDetails(eventDetails))
            val userEvent = EventUser(insertedEvent, user, isPresent = false, isAdmin = true, isModerator = true, hasPaid = false)

            eventUserRepo.create(userEvent)
        }
        Ok(inserted.event.id)
    }

    get("/:id") {
        Ok(requestEvent.get)
    }
    patch("/:id") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to edit this event.")
        //val activitiesraw =  compact(render(parsedBody.\("details").\("scheduledEvents")))
        val extractedDetails = parsedBody.\("details").extract[EventDetails].copy(
            event = requestEvent.get,
            credits = Some(compact(render(parsedBody.\("details").\("credits")))),
            scheduledEvents = Some(compact(render(parsedBody.\("details").\("scheduledEvents")))))

        val newEvent = requestEvent.get.copy(name = parsedBody.\("name").extract[String],
            joinType = parsedBody.\("joinType").extract[JoinType]).setDetails(extractedDetails)

        eventRepo.update(requestEvent.get, newEvent)
        Ok()
    }
    //Change the description of the event(front page)
    post("/:id/description") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to edit this event.")
        val newEvent = requestEvent.get.setDescription(parsedBody.\("description").extract[String])
        eventRepo.update(requestEvent.get, requestEvent.get.setDescription(parsedBody.\("description").extract[String]))
        Ok()
    }
    //Get a list of teams that are participating in tournaments at this event
    get("/:id/teamsAndGuilds") {
        val teams = requestEvent.get.tournaments.flatMap { x =>
            x.teams.map { u =>
                Extraction.decompose(u) merge render(
                    "tournament" -> Extraction.decompose(x).removeField {
                        case ("details", _) => true
                        case ("event", _) => true
                        case ("teams", _) => true
                        case ("users", _) => true
                        case _ => false
                    }.merge(render(
                        ("name" -> x.details.get.name) ~
                            ("teamMaxSize" -> x.details.get.teamMaxSize) ~
                            ("teamMinSize" -> x.details.get.teamMinSize))))
            }
        }
        params.get("page") match {
            case Some(p: String) =>

                Ok(teams.slice(pageSize * (p.toInt - 1), pageSize))
            case None =>
                Ok(teams)
        }

    }
    //Get a list of users for this event
    get("/:id/users") {
        params.get("page") match {
            case Some(p: String) =>
                Ok(requestEvent.get.users.slice(pageSize * (p.toInt - 1), pageSize))
            case None =>
                Ok(requestEvent.get.users.take(pageSize))
        }
    }
    get("/:id/users/:userId") {
        Ok(requestEventUser.get.user)
    }
    //Add a user to an event
    post("/:id/users") {
        val userId = parsedBody.\("userId").extractOpt[Int]
        auth()
        if (requestEvent.get.joinType == JoinType.Invite && user.role != "admin")
            halt(403, "This event is invite only. In order to join a moderator must invite you or accept your join request.")
        userId match {
            case Some(uid: Int) =>
                if (user.role != "admin")
                    halt(403, "You do not have permission to add users to this event.")
                val userRepo = inject[UserRepo]
                userRepo.get(uid) match {
                    case Some(u: User) =>
                        eventRepo.update(requestEvent.get, requestEvent.get.addUser(u))
                        Ok()
                    case None =>
                        BadRequest("No user exists with that Id")
                }
            case None =>
                eventRepo.update(requestEvent.get, requestEvent.get.addUser(user))
                Ok()
        }
    }
    //Delete a user from an event
    delete("/:id/users") {
        val userId = parsedBody.\("userId").extractOpt[Int]
        auth()
        userId match {
            case Some(uid: Int) =>
                if (user.role != "admin" && !requestEvent.get.isAdmin(user))
                    halt(403, "You do not have permission to remove users from this event.")
                val userRepo = inject[UserRepo]
                userRepo.get(uid) match {
                    case Some(u: User) =>
                        eventRepo.update(requestEvent.get, requestEvent.get.removeUser(u))
                        Ok()
                    case None =>
                        BadRequest("No user exists with that Id")
                }
            case None =>
                eventRepo.update(requestEvent.get, requestEvent.get.removeUser(user))
                Ok()
        }
    }
    patch("/:id/users/:userId") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user) && user.id != params("userId").toInt)
            halt(403, "You do not have permission to edit users at this event.")

        val present = parsedBody.\("isPresent").extractOpt[Boolean]
        val admin = parsedBody.\("isAdmin").extractOpt[Boolean]
        val mod = parsedBody.\("isModerator").extractOpt[Boolean]

        var eu = requestEvent.get.users.find(x => x.user.id == params("userId").toInt).getOrElse {
            logger.warn("Admin " + user.id + " tried to modify an EventUser for a non-existent user " + params("userId") + " on Event " + requestEvent.get.id)
            halt(400, "This user is not in this event.")
        } //TODO make this work with requestEventUser
        if (present.isDefined)
            eu = eu.copy(isPresent = present.get)
        if (admin.isDefined)
            eu = eu.copy(isAdmin = admin.get)
        if (mod.isDefined)
            eu = eu.copy(isModerator = mod.get)
        eventRepo.update(requestEvent.get, requestEvent.get.removeUser(eu.user).addUser(eu))
        Ok()
    }
    //get a list of tournaments for an event
    get("/:id/tournaments") {
        params.get("page") match {
            case Some(p: String) =>
                val i = toInt(p).getOrElse(halt(400, "Page parmeter was not a valid integer"))
                Ok(requestEvent.get.tournaments.drop(pageSize * (i + (-1))).take(pageSize))
            case None =>
                Ok(requestEvent.get.tournaments)
        }
    }
    delete("/:id/tournaments/:tourId") {
        if (user.role != "admin" && !requestEvent.get.isAdmin(user))
            halt(403, "You do not have permission to delete users from this event.")
        tournamentRepo.delete(requestTournament.get)
        Ok()
    }
    get("/:id/tournaments/:tourId") {
        val jsonTour = Extraction.decompose(requestTournament.get)
            .replace(List("users"), Extraction.decompose(requestTournament.get.users))
            .replace(List("teams"), Extraction.decompose(requestTournament.get.teams))
        Ok(jsonTour)
    }
    patch("/:id/tournaments/:tourId") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user))
            halt(403, "You do not have permission to edit users at this event.")

        var reqTour = requestTournament.get //TODO tournament security
        var tourDetails = requestTournament.get.details.getOrElse(TournamentDetails(reqTour))

        parsedBody.\("name").extractOpt[String].fold() { x =>
            tourDetails = tourDetails.copy(name = Option(x))
        }
        parsedBody.\("location").extractOpt[String].fold() { x =>
            tourDetails = tourDetails.copy(location = Option(x))
        }
        parsedBody.\("timeStart").extractOpt[DateTime].fold() { x =>
            tourDetails = tourDetails.copy(timeStart = Option(x))
        }
        parsedBody.\("timeEnd").extractOpt[DateTime].fold() { x =>
            tourDetails = tourDetails.copy(timeEnd = Option(x))
        }
        if(parsedBody.\("servers").toOption.isDefined)
        {
            tourDetails = tourDetails.copy(servers = Option(compact(render(parsedBody.\("servers")))))
        }
        if(parsedBody.\("rules").toOption.isDefined)
        {
            tourDetails = tourDetails.copy(rules = Option(compact(render(parsedBody.\("rules")))))
        }
        if(parsedBody.\("streams").toOption.isDefined)
        {
            tourDetails = tourDetails.copy(streams = Option(compact(render(parsedBody.\("streams")))))
        }
        if(parsedBody.\("prizes").toOption.isDefined)
        {
            tourDetails = tourDetails.copy(prizes = Option(compact(render(parsedBody.\("prizes")))))
        }
        if(parsedBody.\("description").toOption.isDefined)
        {
            tourDetails = tourDetails.copy(description = Option(compact(render(parsedBody.\("description")))))
        }
        reqTour = tournamentRepo.update(reqTour, reqTour.setDetails(tourDetails))
        Ok()
    }
    post("/:id/tournaments") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user))
            halt(403, "You do not have permission to edit users at this event.")

        val ttRepo = inject[GenericMRepo[TournamentType]]
        val gameRepo = inject[GameRepo]
        val tx = inject[Transaction]
        val tourRepo = inject[GenericMRepo[Tournament]]

        val regType = parsedBody.\("registrationType").extract[JoinType]
        val tourType = ttRepo.get(parsedBody.\("tournamentType").\("id").extract[Int]).get
        val game = gameRepo.get(parsedBody.\("game").\("id").extract[Int]).get

        val newTour = Tournament(tourType, regType, game, requestEvent.get)

        val inserted = tx { () =>
            val insertedTour = tourRepo.create(newTour)
            val extractedTD = parsedBody.\("details").extract[TournamentDetails]
            val completedTour = tourRepo.update(insertedTour, insertedTour.copy(details = Some(extractedTD.copy(tournament = insertedTour))))
            completedTour
        }
        Ok(inserted.id)
    }
    post("/:id/tournaments/:tourId/teams") {
        auth()
        val teamRepo = inject[GenericMRepo[Team]]
        val tx = inject[Transaction]

        val guildOnly = parsedBody.\("guildOnly").extractOrElse[Boolean](halt(400, "Team type not specified (Guild or Free-Agent)"))
        val teamPlayerIds = parsedBody.\("teamPlayers").extractOrElse[List[Int]](halt(400, "No members specified"))
        val captainId = parsedBody.\("captainId").extractOpt[Int]

        if (requestTournament.get.teams.exists(x => x.teamPlayers.exists(u => teamPlayerIds.contains(u.id))))
            halt(400, "One or more members already belongs to a Team in this Tournament.")

        if (guildOnly) {
            val guildId = parsedBody.\("guildId").extractOrElse[Int](halt(400, "No guild Id specified."))
            val guildRepo = inject[GuildRepo]
            val guild = guildRepo.get(guildId).getOrElse(halt(400, "No guild exists with that Id"))

            val members = guild.members.filter(x => teamPlayerIds.contains(x.user.id))

            val newTeam = Team(guild.name, JoinType.Invite, requestTournament.get, guildOnly = true)
            val inserted = tx { () =>
                val insertedTeam = teamRepo.create(newTeam)
                val newPlayers = members.map(x => TeamUser(insertedTeam, x.user, isCaptain = captainId.isDefined && x.user.id == captainId.get))
                teamRepo.update(insertedTeam, insertedTeam.copy(teamPlayers = newPlayers))
            }
            Ok(inserted)
        }
        else {
            val teamName = parsedBody.\("name").extractOrElse[String](halt(400, "No team name provided."))
            val userRepo = inject[UserRepo]
            val members = teamPlayerIds.map(x => userRepo.get(x).getOrElse(halt(400, "No users with the Id " + x + " exists.")))

            val newTeam = Team(teamName, JoinType.Public, requestTournament.get, guildOnly = false)

            val inserted = tx { () =>
                val insertedTeam = teamRepo.create(newTeam)
                val newPlayers = members.map(x => TeamUser(insertedTeam, x, isCaptain = captainId.isDefined && x.id == captainId.get))
                teamRepo.update(insertedTeam, insertedTeam.copy(teamPlayers = newPlayers))
            }
            Ok(inserted)
        }

    }
    patch("/:id/tournaments/:tourId/teams/:teamId") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isAdmin(user))
            halt(403, "You do not have permission to edit this team.")
        val teamRepo = inject[GenericMRepo[Team]]

        parsedBody.\("isPresent").extractOpt[Boolean].fold() { x =>
            teamRepo.update(requestTeam.get, requestTeam.get.copy(isPresent = x))
        }

        Ok()
    }
    post("/:id/tournaments/:tourId/teams/:teamId/members") {
        auth()
        val userId = parsedBody.\("userId").extractOrElse[Int](halt(400, "User Id is not specified."))

        if (requestTeam.get.teamPlayers.exists(x => x.id == userId))
            halt(400, "User with Id " + userId + " is already on this Team")

        val isCaptain = parsedBody.\("isCaptain").extractOrElse[Boolean](false)
        val userRepo = inject[UserRepo]
        val teamRepo = inject[GenericMRepo[Team]]

        userRepo.get(userId) match {
            case Some(u: User with Persisted) =>
                val updated = teamRepo.update(requestTeam.get, requestTeam.get.addUser(u))
                Ok(updated)
            case None =>
                halt(400, "No user with the specified Id " + userId + " exists.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/teams/:teamId/members") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isAdmin(user) && !requestTeam.get.teamPlayers.exists(x => x.user.id == user.id))
            halt(403, "You do not have permission to delete a user from this team.")

        val uExist = params.getOrElse("userId", halt(400, "No user Id specified."))
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val teamRepo = inject[GenericMRepo[Team]]
        requestTeam.get.teamPlayers.find(x => x.user.id == userId) match {
            case Some(x: TeamUser with Persisted) =>
                val updated = teamRepo.update(requestTeam.get, requestTeam.get.removeUser(x.user))
                Ok(updated)
            case None => halt(400, "User is not on team. Could not remove.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/teams/:teamId") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isAdmin(user))
            halt(403, "You do not have permission to delete this team.")

        val teamRepo = inject[GenericMRepo[Team]]
        teamRepo.delete(requestTeam.get)
        Ok(tournamentRepo.get(requestTournament.get.id))
    }
    post("/:id/tournaments/:tourId/players") {
        auth()
        val uExist = parsedBody.\("userId").extractOrElse[String](halt(400, "No user Id specified."))
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val tuRepo = inject[GenericMRepo[TournamentUser]]
        val userRepo = inject[UserRepo]

        userRepo.get(userId) match {
            case Some(u: User with Persisted) =>
                val newTu = TournamentUser(requestTournament.get, u)
                val inserted = tuRepo.create(newTu)
                Ok(inserted)
            case None =>
                halt(400, "No user with the specified Id " + userId + " exists.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    patch("/:id/tournaments/:tourId/players/:userId") {
        auth()
        val uExist = params("userId")
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val tuRepo = inject[GenericMRepo[TournamentUser]]
        requestTournament.get.users.find(x => x.user.id == userId) match {
            case Some(tu: TournamentUser with Persisted) =>
                var tourUser = tu
                parsedBody.\("isPresent").extractOpt[Boolean].fold() { x =>
                    tourUser = tuRepo.update(tourUser, tourUser.copy(isPresent = x))
                }
                parsedBody.\("isModerator").extractOpt[Boolean].fold() { x =>
                    tourUser = tuRepo.update(tourUser, tourUser.copy(isModerator = x))
                }
                parsedBody.\("isAdmin").extractOpt[Boolean].fold() { x =>
                    tourUser = tuRepo.update(tourUser, tourUser.copy(isAdmin = x))
                }
                Ok(tourUser)
            case None => halt(400, "No user with the Id " + userId + " is in this tournament.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/players/:userId") {
        auth()
        val uExist = params("userId")
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val tuRepo = inject[GenericMRepo[TournamentUser]]
        requestTournament.get.users.find(x => x.user.id == userId) match {
            case Some(tu: TournamentUser with Persisted) =>
                tuRepo.delete(tu)
                Ok()
            case None => halt(400, "No user with the Id " + userId + " is in this tournament.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    //A user paying for registration
    post("/:id/users/:userId/payRegistration") {
        import scala.collection.mutable
        auth()
        if (params("userId").toInt != user.id && (user.role == "admin" || requestEvent.get.isAdmin(user))) {
            val paid = parsedBody.\("paid").extractOrElse[Boolean](halt(400, "Missing payment status"))
            val receipt = parsedBody.\("receipt").extractOpt[String]
            val userRepo = inject[UserRepo]
            val theuser = requestEventUser.get.user //userRepo.get(params("userId").toInt).getOrElse(halt(400, "User with that Id does not exist."))
            eventRepo.update(requestEvent.get, requestEvent.get.setUserPayment(theuser, paid = paid, receipt))
            val atype = if (user.role == "admin") "Admin" else "Event Admin"
            logger.info("[" + atype + " " + user.id + "] Setting payment for User " + theuser.id + " on Event " + requestEvent.get.id + " to " + paid.toString.toUpperCase)
            Ok()
        }
        else {
            parsedBody.\("type").extractOpt[String] match {
                case None =>
                    halt(400, "No payment type was specified.")
                case Some("Stripe") =>
                    val card = parsedBody.\("card").extract[String]
                    val paymentService: PaymentService = new StripePayment(requestEvent.get)
                    paymentService.makePayment(user, mutable.Map("card" -> card)) match {
                        case (false, s: String) =>
                            BadRequest("Payment was not successful. Reason: " + s)
                        case (true, s: String) =>
                            Ok("Payment successful!")
                            eventRepo.update(requestEvent.get, requestEvent.get.setUserPayment(user, paid = true, Some(s)))
                    }
                case Some(s: String) =>
                    NotImplemented
                //TODO more payment types?
            }
        }
    }
    //add new payment option
    post("/:id/payments") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        if (requestEvent.get.payments.exists(x => x.payType == parsedBody.\("payType").extract[PaymentType]))
            halt(400, "You cannot add a payment type more than once.")
        val ep = EventPayment(requestEvent.get,
            parsedBody.\("payType").extract[PaymentType],
            parsedBody.\("secretKey").extractOpt[String],
            parsedBody.\("publicKey").extractOpt[String],
            parsedBody.\("address").extractOpt[String],
            parsedBody.\("amount").extract[Double])
        val updated = eventRepo.update(requestEvent.get, requestEvent.get.addPayment(ep))
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //Change details of a payment option
    post("/:id/payments/:payId") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        val ep = EventPayment(requestEvent.get,
            parsedBody.\("payType").extract[PaymentType],
            parsedBody.\("secretKey").extractOpt[String],
            parsedBody.\("publicKey").extractOpt[String],
            parsedBody.\("address").extractOpt[String],
            parsedBody.\("amount").extract[Double],
            parsedBody.\("isEnabled").extract[Boolean],
            parsedBody.\("id").extract[Int])
        val updated = eventRepo.update(requestEvent.get, requestEvent.get.changePayment(params("payId").toInt, ep))
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //delete a payment option
    delete("/:id/payments/:payId") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        val updated = eventRepo.update(requestEvent.get, requestEvent.get.removePayment(params("payId").toInt))
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //change privacy settings
    post("/:id/privacy") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        eventRepo.update(requestEvent.get, requestEvent.get.copy(joinType = parsedBody.\("privacy").extract[JoinType]))
        Ok()
    }
}
