package com.esports.gtplatform.controllers

import com.escalatesoft.subcut.inject.BindingModule
import com.esports.gtplatform.business.services.{PaymentException, CustomerException, PaymentService, StripePayment}
import com.esports.gtplatform.business._
import com.esports.gtplatform.models.Team
import com.googlecode.mapperdao.Persisted
import com.googlecode.mapperdao.jdbc.Transaction
import models.JoinType.JoinType
import models.PaymentType.PaymentType
import models._
import org.joda.time.DateTime
import org.json4s.Extraction
import org.scalatra.scalate.ScalateUrlGeneratorSupport
import org.scalatra.{BadRequest, NotImplemented, Ok}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scaldi.Injector

/**
 * Created by Matthew on 8/22/2014.
 */
class EventController(val eventRepo: EventRepo,
                      val eventUserRepo: EventUserRepo,
                      val tournamentRepo: TournamentRepo,
                      val userRepo: UserRepo,
                      val ttRepo: TournamentTypeRepo) extends APIController with EventControllerT with ScalateUrlGeneratorSupport {

    get("/") {
        val events = eventRepo.getPaginated(params.getOrElse("page", "1").toInt)
        Ok(events)
    }
    post("/") {
        auth()
        val newEvent: Event = Event(parsedBody.\("name").extract[String], parsedBody.\("joinType").extract[String])
        if (eventRepo.getByName(newEvent.name).isDefined)
            halt(400, "Event with this name already exists.")

        val tx = inject[Transaction]
        val inserted = tx { () =>

            val insertedEvent = eventRepo.create(newEvent)
            val eventDetails = EventDetail(insertedEvent, timeStart = parsedBody.\("details").\("timeStart").extractOpt[DateTime], timeEnd = parsedBody.\("details").\("timeEnd").extractOpt[DateTime])
            eventRepo.update(insertedEvent)
            val userEvent = EventUser(insertedEvent, user, isPresent = false, isAdmin = true, isModerator = true, hasPaid = false)

            eventUserRepo.create(userEvent)
        }
        Ok(inserted.eventId.id)
    }

    get("/:id") {
        Ok(requestEvent.get)
    }
    patch("/:id") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to edit this event.")
        //val activitiesraw =  compact(render(parsedBody.\("details").\("scheduledEvents")))
        val extractedDetails = parsedBody.\("details").extract[EventDetail].copy(
            event = requestEvent.get,
            credits = Some(compact(render(parsedBody.\("details").\("credits")))),
            scheduledEvents = Some(compact(render(parsedBody.\("details").\("scheduledEvents")))),
            faq = Some(compact(render(parsedBody.\("details").\("faq")))))

        val newEvent = requestEvent.get.copy(name = parsedBody.\("name").extract[String],
            joinType = parsedBody.\("joinType").extract[JoinType]).setDetails(extractedDetails)

        eventRepo.update(requestEvent.get)
        Ok()
    }
    //Change the description of the event(front page)
    post("/:id/description") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to edit this event.")
        val newEvent = requestEvent.get.setDescription(parsedBody.\("description").extract[String])
        eventRepo.update(requestEvent.get)
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
                Ok(requestEvent.get.users.drop(pageSize * (p.toInt - 1)).take(pageSize))
            case None =>
                Ok(requestEvent.get.users.take(pageSize))
        }
    }
    get("/:id/users/:userId") {
        Ok(requestEventUser.get.userId)
    }
    //Add a user to an event
    post("/:id/users") {
        val userId = parsedBody.\("userId").extractOpt[Int]
        auth()
        if (requestEvent.get.joinType == "Invite" && user.role != "admin")
            halt(403, "This event is invite only. In order to join a moderator must invite you or accept your join request.")
        userId match {
            case Some(uid: Int) =>
                if (user.role != "admin")
                    halt(403, "You do not have permission to add users to this event.")
                userRepo.get(uid) match {
                    case Some(u: User) =>
                        eventRepo.update(requestEvent.get)
                        logger.info("[Admin] (" + user.id + ") Added User " + u.id + " to Event " + requestEvent.get.id)
                        Ok()
                    case None =>
                        BadRequest("No user exists with that Id")
                }
            case None =>
                eventRepo.update(requestEvent.get)
                logger.info("[Event] ("+ requestEvent.get.id +")User " + user.id + " joined Event ")
                Ok()
        }
    }
    //Delete a user from an event
    delete("/:id/users/:userId") {
        val userId = params("userId").toInt
        auth()
/*        userId match {
            case Some(uid: Int) =>*/
                if (user.role != "admin" && !requestEvent.get.isAdmin(user))
                    halt(403, "You do not have permission to remove users from this event.")
                userRepo.get(userId) match {
                    case Some(u: User) =>
                        eventRepo.update(requestEvent.get)
                        logger.info("[Admin] with " + user.id + " removed User " + u.id + " from Event " + requestEvent.get.id)
                        Ok()
                    case None =>
                        BadRequest("No user exists with that Id")
/*                }
            case None =>
                eventRepo.update(requestEvent.get, requestEvent.get.removeUser(user))
                logger.info("User " + user.id + " left Event " + requestEvent.get.id)
                Ok()*/
        }
    }
    patch("/:id/users/:userId") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user) && user.id != params("userId").toInt)
            halt(403, "You do not have permission to edit users at this event.")

        val present = parsedBody.\("isPresent").extractOpt[Boolean]
        val admin = parsedBody.\("isAdmin").extractOpt[Boolean]
        val mod = parsedBody.\("isModerator").extractOpt[Boolean]

        var eu = requestEvent.get.users.find(x => x.userId == params("userId").toInt).getOrElse {
            logger.warn("[Admin] " + user.id + " tried to modify an EventUser for a non-existent user " + params("userId") + " on Event " + requestEvent.get.id)
            halt(400, "This user is not in this event.")
        } //TODO make this work with requestEventUser
        if (present.isDefined)
        {
            eu = eu.copy(isPresent = present.get)
        }
        if (admin.isDefined)
        {
            eu = eu.copy(isAdmin = admin.get)
            logger.info("[Admin] " + user.id + "set Event Admin = " + admin.get + " on User " + eu.userId.id + " for Event " + requestEvent.get.id)
        }
        if (mod.isDefined)
        {
            eu = eu.copy(isModerator = mod.get)
            logger.info("[Admin] " + user.id + "set Event Moderator = " + mod.get + " on User " + eu.userId.id + " for Event " + requestEvent.get.id)
        }
        eventRepo.update(requestEvent.get)
        Ok()
    }
    //get a list of tournaments for an event
    get("/:id/tournaments") {
     Ok(tournamentRepo.getByEvent(paramId.get))
    }

    post("/:id/tournaments/:tourId/teams") {
        auth()
        val teamRepo = inject[GenericMRepo[Team]]
        val tx = inject[Transaction]

        val guildOnly = parsedBody.\("guildOnly").extractOrElse[Boolean](halt(400, "Team type not specified (Guild or Free-Agent)"))
        val teamPlayerIds = parsedBody.\("teamPlayers").extractOrElse[List[Int]](halt(400, "No members specified"))
        val captainId = parsedBody.\("captainId").extractOpt[Int]

        if (requestTournament.get.teams.exists(x => x.teamPlayers.exists(u => teamPlayerIds.contains(u.userId.id))))
            halt(400, "One or more members already belongs to a Team in this Tournament.")

        if (guildOnly) {
            val guildId = parsedBody.\("guildId").extractOrElse[Int](halt(400, "No guild Id specified."))
            val guildRepo = inject[GuildRepo]
            val guild = guildRepo.get(guildId).getOrElse(halt(400, "No guild exists with that Id"))

            val members = guild.members.filter(x => teamPlayerIds.contains(x.userId.id))

            val newTeam = Team(guild.name, JoinType.Invite, requestTournament.get, guildOnly = true, guildId = Option(guildId))
            val inserted = tx { () =>
                val insertedTeam = teamRepo.create(newTeam)
                val newPlayers = members.map(x => TeamUser(insertedTeam, x.userId, isCaptain = captainId.isDefined && x.userId.id == captainId.get))
                teamRepo.update(insertedTeam)
            }
            logger.info("[Tournament] ("+ requestTournament.get.id +") New Guild Team \"" + inserted.name + "\" created.")
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
                teamRepo.update(insertedTeam)
            }
            logger.info("[Tournament] ("+ requestTournament.get.id +") New Team \"" + inserted.name + "\" created.")
            Ok(inserted)
        }

    }
    patch("/:id/tournaments/:tourId/teams/:teamId") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isAdmin(user) && !requestTournament.get.isModerator(user))
            halt(403, "You do not have permission to edit this team.")
        val teamRepo = inject[GenericMRepo[Team]]

        parsedBody.\("isPresent").extractOpt[Boolean].fold() { x =>
            teamRepo.update(requestTeam.get)
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
                val updated = teamRepo.update(requestTeam.get)
                Ok(updated)
            case None =>
                halt(400, "No user with the specified Id " + userId + " exists.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/teams/:teamId/members") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isModerator(user) && !requestTeam.get.teamPlayers.exists(x => x.userId.id == user.id))
            halt(403, "You do not have permission to delete a user from this team.")

        val uExist = params.getOrElse("userId", halt(400, "No user Id specified."))
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val teamRepo = inject[GenericMRepo[Team]]
        requestTeam.get.teamPlayers.find(x => x.userId.id == userId) match {
            case Some(x: TeamUser with Persisted) =>
                val updated = teamRepo.update(requestTeam.get)
                Ok(updated)
            case None => halt(400, "User is not on team. Could not remove.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/teams/:teamId") {
        auth()
        if (user.role != "admin" && requestTeam.get.getCaptain.id != user.id && !requestEvent.get.isAdmin(user) && !requestTournament.get.isAdmin(user))
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
                logger.info("[Tournament] ("+ requestTournament.get.id +") User " + u.id + " joined as player.")
                Ok(inserted)
            case None =>
                halt(400, "No user with the specified Id " + userId + " exists.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    patch("/:id/tournaments/:tourId/players/:userId") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user) && !requestTournament.get.isModerator(user))
            halt(403, "You do not have permission to delete this team.")
        val uExist = params("userId")
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val tuRepo = inject[GenericMRepo[TournamentUser]]
        requestTournament.get.users.find(x => x.userId.id == userId) match {
            case Some(tu: TournamentUser with Persisted) =>
                var tourUser = tu
                parsedBody.\("isPresent").extractOpt[Boolean].fold() { x =>
                    tourUser = tuRepo.update(tourUser)
                }
                parsedBody.\("isModerator").extractOpt[Boolean].fold() { x =>
                    if(!requestTournament.get.isAdmin(user) && !requestEvent.get.isAdmin(user))
                        halt(403, "You do not have permission to assign moderator status.")
                    tourUser = tuRepo.update(tourUser)
                }
                parsedBody.\("isAdmin").extractOpt[Boolean].fold() { x =>
                    if(!requestTournament.get.isAdmin(user) && !requestEvent.get.isAdmin(user))
                        halt(403, "You do not have permission to assign admin status.")
                    tourUser = tuRepo.update(tourUser)
                }
                Ok(tourUser)
            case None => halt(400, "No user with the Id " + userId + " is in this tournament.")
            case _ => logger.warn("Not hitting a match here!")
        }
    }
    delete("/:id/tournaments/:tourId/players/:userId") {
        auth()
        if (user.role != "admin" && !requestEvent.get.isAdmin(user) && !requestTournament.get.isModerator(user))
            halt(403, "You do not have permission to remove this user.")
        val uExist = params("userId")
        val userId = toInt(uExist).getOrElse(halt(400, "User id was not a valid integer"))
        val tuRepo = inject[GenericMRepo[TournamentUser]]
        requestTournament.get.users.find(x => x.userId.id == userId) match {
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
            val theuser = requestEventUser.get.userId //userRepo.get(params("userId").toInt).getOrElse(halt(400, "User with that Id does not exist."))
            eventRepo.update(requestEvent.get)
            val atype = if (user.role == "admin") "Admin" else "Event Admin"
            logger.info("[" + atype + "] (" + user.id + ") Setting payment for User " + theuser.id + " on Event " + requestEvent.get.id + " to " + paid.toString.toUpperCase)
            Ok()
        }
        else {
            parsedBody.\("type").extractOpt[String] match {
                case None =>
                    halt(400, "No payment type was specified.")
                case Some("Stripe") =>
                    val card = parsedBody.\("card").extract[String]
                    val paymentService: PaymentService = new StripePayment(requestEvent.get)
                    paymentService.checkForExistingUser(user) match {
                        case None =>
                            logger.info("No user with customer or payment Id found.")
                            val eventUserRepo = inject[EventUserRepo]
                            val payingUser = eventUserRepo.getByUser(user).headOption.getOrElse(halt(500,"Could not find you in our system! Contact an admin to sort this out."))

                           val evWithCustomer = try {
                                paymentService.createCustomer(payingUser,mutable.Map("card" -> card))
                            }
                            catch {
                                case cust: CustomerException =>
                                    halt(500,
                                      s"""There was a problem with stripe. Your card has NOT been charged. Please check your payment details and try again.
                                         | The problem: ${cust.getMessage} .
                                         | The error: ${cust.getCause.getMessage}""".stripMargin)
                                case e: Exception =>
                                   val json = render(("message" -> "We had a problem storing your payment Id. Your card has NOT been charged. DO NOT attempt to pay again, please contact an admin or send us a ticket.") ~
                                                    ("customerId" -> e.getMessage))
                                    halt(500, json)
                            }

                            try {
                                paymentService.makePayment(evWithCustomer)
                            }
                            catch {
                                case p: PaymentException =>
                                    val msg =
                                      s"""There was a problem with stripe. Your card has NOT been charged. DO NOT attempt to pay again, please contact an admin or send us a ticket.
                                         |The problem: ${p.getMessage} .
                                         |The error: ${p.getCause.getMessage} """.stripMargin
                                    val json = render(("message" -> msg) ~
                                        ("customerId" -> evWithCustomer.customerId))
                                    halt(500, json)
                                case e: Exception =>
                                    val msg = "We had a problem storing your payment receipt. Your card HAS been charged. Please contact an admin or send us a ticket."
                                    val json = render(("message" -> msg) ~
                                        ("customerId" -> evWithCustomer.customerId))
                                    halt(500, json)
                            }
                            Ok("Payment successful!")

                        case _ =>
                            logger.warn("User with customer or payment Id found!")
                            halt(500, "You already have already payment information in our system! Please contact an admin or send us a ticket letting us know the problem.")
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
        val updated = eventRepo.update(requestEvent.get)
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //Change details of a payment option
    post("/:id/payments/:payId") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        val ep = EventPayment(requestEvent.get,
            parsedBody.\("payType").extract[String],
            parsedBody.\("secretKey").extractOpt[String],
            parsedBody.\("publicKey").extractOpt[String],
            parsedBody.\("address").extractOpt[String],
            parsedBody.\("amount").extract[Double],
            parsedBody.\("isEnabled").extract[Boolean],
            parsedBody.\("id").extract[Int])
        val updated = eventRepo.update(requestEvent.get)
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //delete a payment option
    delete("/:id/payments/:payId") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        val updated = eventRepo.update(requestEvent.get)
        Ok(updated.payments.filter(x => x.isEnabled))
    }
    //change privacy settings
    post("/:id/privacy") {
        auth()
        if (!requestEvent.get.isAdmin(user) && user.role != "admin")
            halt(403, "You do not have permission to do that")
        eventRepo.update(requestEvent.get)
        Ok()
    }

    /*
    * Tournament URL Support
    * */

/*    post("/tournaments"){
        redirect(url("tournaments"))
    }
    get("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    patch("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    delete("/tournaments/:id"){
        redirect(url("tournaments/"+params("tid")))
    }
    get("/tournaments/:id/details"){
        redirect(url("tournaments/"+params("tid")))
    }
    patch("/tournaments/:tid/details"){
        redirect(url("tournaments/"+params("tid")+"/details"))
    }*/
}
