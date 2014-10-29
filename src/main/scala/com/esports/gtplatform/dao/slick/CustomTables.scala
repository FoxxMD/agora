
package com.esports.gtplatform.dao.slick

//auto-generated
import com.esports.gtplatform.models._
import models.{EventDetail, _}

object Tables extends {
    val profile = scala.slick.driver.MySQLDriver
} with Tables

trait Tables extends {
    val profile: scala.slick.driver.JdbcProfile
    import profile.simple._
    import scala.slick.model.ForeignKeyAction
    // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
    import scala.slick.jdbc.{GetResult => GR}

    /** DDL for all tables. Call .create to execute. */
    lazy val ddl = EventDetails.ddl ++ EventPayments.ddl ++ Events.ddl ++ EventsUsers.ddl ++ Games.ddl ++ GamesTournamentsTypes.ddl ++ Guilds.ddl ++ GuildsGames.ddl ++ GuildsUsers.ddl ++ Nonactiveuseridentity.ddl ++ Nonactiveusers.ddl ++ Teams.ddl ++ TeamsUsers.ddl ++ Tournaments.ddl ++ TournamentsDetails.ddl ++ TournamentsTypes.ddl ++ TournamentsUsers.ddl ++ Users.ddl ++ UsersIdentity.ddl ++ UsersPlatformProfile.ddl

    /** GetResult implicit for fetching EventDetail objects using plain SQL queries */
    implicit def GetResultEventDetail(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[org.joda.time.DateTime]]): GR[EventDetail] = GR{
        prs => import prs._
            EventDetail.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[org.joda.time.DateTime], <<?[org.joda.time.DateTime], <<?[String], <<?[String], <<?[String]))
    }
    /** Table description of table event_details. Objects of this class serve as prototypes for rows in queries. */
    class EventDetails(_tableTag: Tag) extends Table[EventDetail](_tableTag, "event_details") {
        def * = (eventsId, location, address, city, state, description, rules, prizes, streams, servers, timestart, timeend, scheduledevents, credits, faq) <> (EventDetail.tupled, EventDetail.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (eventsId.?, location, address, city, state, description, rules, prizes, streams, servers, timestart, timeend, scheduledevents, credits, faq).shaped.<>({r=>import r._; _1.map(_=> EventDetail.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column events_id DBType(INT), AutoInc, PrimaryKey */
        val eventsId: Column[Int] = column[Int]("events_id", O.AutoInc, O.PrimaryKey)
        /** Database column location DBType(VARCHAR), Length(45,true), Default(None) */
        val location: Column[Option[String]] = column[Option[String]]("location", O.Length(45,varying=true), O.Default(None))
        /** Database column address DBType(VARCHAR), Length(45,true), Default(None) */
        val address: Column[Option[String]] = column[Option[String]]("address", O.Length(45,varying=true), O.Default(None))
        /** Database column city DBType(VARCHAR), Length(45,true), Default(None) */
        val city: Column[Option[String]] = column[Option[String]]("city", O.Length(45,varying=true), O.Default(None))
        /** Database column state DBType(VARCHAR), Length(45,true), Default(None) */
        val state: Column[Option[String]] = column[Option[String]]("state", O.Length(45,varying=true), O.Default(None))
        /** Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val description: Column[Option[String]] = column[Option[String]]("description", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column rules DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val rules: Column[Option[String]] = column[Option[String]]("rules", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column prizes DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val prizes: Column[Option[String]] = column[Option[String]]("prizes", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column streams DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val streams: Column[Option[String]] = column[Option[String]]("streams", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column servers DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val servers: Column[Option[String]] = column[Option[String]]("servers", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column timeStart DBType(INT), Default(None) */
        val timestart: Column[Option[org.joda.time.DateTime]] = column[Option[org.joda.time.DateTime]]("timeStart", O.Default(None))
        /** Database column timeEnd DBType(INT), Default(None) */
        val timeend: Column[Option[org.joda.time.DateTime]] = column[Option[org.joda.time.DateTime]]("timeEnd", O.Default(None))
        /** Database column scheduledevents DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val scheduledevents: Column[Option[String]] = column[Option[String]]("scheduledevents", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column credits DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val credits: Column[Option[String]] = column[Option[String]]("credits", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column faq DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val faq: Column[Option[String]] = column[Option[String]]("faq", O.Length(2147483647,varying=true), O.Default(None))

        /** Foreign key referencing Events (database name event_details_ibfk_1) */
        lazy val eventsFk = foreignKey("event_details_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table EventDetails */
    lazy val EventDetails = new TableQuery(tag => new EventDetails(tag))

    /** GetResult implicit for fetching EventPayment objects using plain SQL queries */
    implicit def GetResultEventPayment(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Double], e4: GR[Boolean]): GR[EventPayment] = GR{
        prs => import prs._
            EventPayment.tupled((<<[Int], <<[Int], <<[String], <<?[String], <<?[String], <<?[String], <<[Double], <<[Boolean]))
    }
    /** Table description of table event_payments. Objects of this class serve as prototypes for rows in queries. */
    class EventPayments(_tableTag: Tag) extends Table[EventPayment](_tableTag, "event_payments") {
        def * = (id, eventsId, paytype, secret, public, address, amount, isenabled) <> (EventPayment.tupled, EventPayment.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, eventsId.?, paytype.?, secret, public, address, amount.?, isenabled.?).shaped.<>({r=>import r._; _1.map(_=> EventPayment.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column events_id DBType(INT) */
        val eventsId: Column[Int] = column[Int]("events_id")
        /** Database column paytype DBType(VARCHAR), Length(45,true) */
        val paytype: Column[String] = column[String]("paytype", O.Length(45,varying=true))
        /** Database column secret DBType(VARCHAR), Length(45,true), Default(None) */
        val secret: Column[Option[String]] = column[Option[String]]("secret", O.Length(45,varying=true), O.Default(None))
        /** Database column public DBType(VARCHAR), Length(45,true), Default(None) */
        val public: Column[Option[String]] = column[Option[String]]("public", O.Length(45,varying=true), O.Default(None))
        /** Database column address DBType(VARCHAR), Length(45,true), Default(None) */
        val address: Column[Option[String]] = column[Option[String]]("address", O.Length(45,varying=true), O.Default(None))
        /** Database column amount DBType(DOUBLE) */
        val amount: Column[Double] = column[Double]("amount")
        /** Database column isenabled DBType(BIT) */
        val isenabled: Column[Boolean] = column[Boolean]("isenabled")

        /** Foreign key referencing Events (database name event_payments_ibfk_1) */
        lazy val eventsFk = foreignKey("event_payments_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table EventPayments */
    lazy val EventPayments = new TableQuery(tag => new EventPayments(tag))

    /** GetResult implicit for fetching Event objects using plain SQL queries */
    implicit def GetResultEvent(implicit e0: GR[Int], e1: GR[String]): GR[Event] = GR{
        prs => import prs._
            Event.tupled((<<[Int], <<[String], <<[String]))
    }
    /** Table description of table events. Objects of this class serve as prototypes for rows in queries. */
    class Events(_tableTag: Tag) extends Table[Event](_tableTag, "events") {
        def * = (id, name, eventtype) <> (Event.tupled, Event.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, name.?, eventtype.?).shaped.<>({r=>import r._; _1.map(_=> Event.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(45,true) */
        val name: Column[String] = column[String]("name", O.Length(45,varying=true))
        /** Database column eventType DBType(VARCHAR), Length(15,true) */
        val eventtype: Column[String] = column[String]("eventType", O.Length(15,varying=true))
    }
    /** Collection-like TableQuery object for table Events */
    lazy val Events = new TableQuery(tag => new Events(tag))

    /** GetResult implicit for fetching EventUser objects using plain SQL queries */
    implicit def GetResultEventUser(implicit e0: GR[Int], e1: GR[Boolean], e2: GR[Option[String]]): GR[EventUser] = GR{
        prs => import prs._
            EventUser.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean], <<[Boolean], <<[Boolean], <<[Boolean], <<?[String], <<?[String]))
    }
    /** Table description of table events_users. Objects of this class serve as prototypes for rows in queries. */
    class EventsUsers(_tableTag: Tag) extends Table[EventUser](_tableTag, "events_users") {
        def * = (id, usersId, eventsId, ispresent, isadmin, ismoderator, haspaid, receiptid, customerid) <> (EventUser.tupled, EventUser.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, usersId.?, eventsId.?, ispresent.?, isadmin.?, ismoderator.?, haspaid.?, receiptid, customerid).shaped.<>({r=>import r._; _1.map(_=> EventUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column events_id DBType(INT) */
        val eventsId: Column[Int] = column[Int]("events_id")
        /** Database column isPresent DBType(BIT) */
        val ispresent: Column[Boolean] = column[Boolean]("isPresent")
        /** Database column isAdmin DBType(BIT) */
        val isadmin: Column[Boolean] = column[Boolean]("isAdmin")
        /** Database column isModerator DBType(BIT) */
        val ismoderator: Column[Boolean] = column[Boolean]("isModerator")
        /** Database column hasPaid DBType(BIT) */
        val haspaid: Column[Boolean] = column[Boolean]("hasPaid")
        /** Database column receiptId DBType(VARCHAR), Length(45,true), Default(None) */
        val receiptid: Column[Option[String]] = column[Option[String]]("receiptId", O.Length(45,varying=true), O.Default(None))
        /** Database column customerId DBType(VARCHAR), Length(45,true), Default(None) */
        val customerid: Column[Option[String]] = column[Option[String]]("customerId", O.Length(45,varying=true), O.Default(None))

        /** Foreign key referencing Events (database name events_users_ibfk_1) */
        lazy val eventsFk = foreignKey("events_users_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing Users (database name events_users_ibfk_2) */
        lazy val usersFk = foreignKey("events_users_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table EventsUsers */
    lazy val EventsUsers = new TableQuery(tag => new EventsUsers(tag))

    /** GetResult implicit for fetching Game objects using plain SQL queries */
    implicit def GetResultGame(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Boolean]): GR[Game] = GR{
        prs => import prs._
            Game.tupled((<<[Int], <<[String], <<?[String], <<?[String], <<[String], <<[Boolean], <<[Boolean], <<?[String]))
    }
    /** Table description of table games. Objects of this class serve as prototypes for rows in queries. */
    class Games(_tableTag: Tag) extends Table[Game](_tableTag, "games") {
        def * = (id, name, publisher, website, gametype, userplay, teamplay, logofilename) <> (Game.tupled, Game.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, name.?, publisher, website, gametype.?, userplay.?, teamplay.?, logofilename).shaped.<>({r=>import r._; _1.map(_=> Game.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(45,true) */
        val name: Column[String] = column[String]("name", O.Length(45,varying=true))
        /** Database column publisher DBType(VARCHAR), Length(45,true), Default(None) */
        val publisher: Column[Option[String]] = column[Option[String]]("publisher", O.Length(45,varying=true), O.Default(None))
        /** Database column website DBType(VARCHAR), Length(100,true), Default(None) */
        val website: Column[Option[String]] = column[Option[String]]("website", O.Length(100,varying=true), O.Default(None))
        /** Database column gameType DBType(VARCHAR), Length(20,true) */
        val gametype: Column[String] = column[String]("gameType", O.Length(20,varying=true))
        /** Database column userPlay DBType(BIT) */
        val userplay: Column[Boolean] = column[Boolean]("userPlay")
        /** Database column teamPlay DBType(BIT) */
        val teamplay: Column[Boolean] = column[Boolean]("teamPlay")
        /** Database column logoFilename DBType(VARCHAR), Length(30,true), Default(None) */
        val logofilename: Column[Option[String]] = column[Option[String]]("logoFilename", O.Length(30,varying=true), O.Default(None))
    }
    /** Collection-like TableQuery object for table Games */
    lazy val Games = new TableQuery(tag => new Games(tag))

    /** GetResult implicit for fetching GameTournamentType objects using plain SQL queries */
    implicit def GetResultGameTournamentType(implicit e0: GR[Int]): GR[GameTournamentType] = GR{
        prs => import prs._
            GameTournamentType.tupled((<<[Int], <<[Int], <<[Int]))
    }
    /** Table description of table games_tournaments_types. Objects of this class serve as prototypes for rows in queries. */
    class GamesTournamentsTypes(_tableTag: Tag) extends Table[GameTournamentType](_tableTag, "games_tournaments_types") {
        def * = (gamesId, tournamenttypesId, id) <> (GameTournamentType.tupled, GameTournamentType.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (gamesId.?, tournamenttypesId.?, id.?).shaped.<>({r=>import r._; _1.map(_=> GameTournamentType.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column games_id DBType(INT) */
        val gamesId: Column[Int] = column[Int]("games_id")
        /** Database column tournamenttypes_id DBType(INT) */
        val tournamenttypesId: Column[Int] = column[Int]("tournamenttypes_id")
        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

        /** Foreign key referencing Games (database name games_tournaments_types_ibfk_1) */
        lazy val gamesFk = foreignKey("games_tournaments_types_ibfk_1", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing TournamentsTypes (database name games_tournaments_types_ibfk_2) */
        lazy val tournamentsTypesFk = foreignKey("games_tournaments_types_ibfk_2", tournamenttypesId, TournamentsTypes)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table GamesTournamentsTypes */
    lazy val GamesTournamentsTypes = new TableQuery(tag => new GamesTournamentsTypes(tag))

    /** GetResult implicit for fetching Guild objects using plain SQL queries */
    implicit def GetResultGuild(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Int]], e4: GR[org.joda.time.DateTime]): GR[Guild] = GR{
        prs => import prs._
            Guild.tupled((<<[Int], <<[String], <<?[String], <<?[Int], <<[String], <<[org.joda.time.DateTime]))
    }
    /** Table description of table guilds. Objects of this class serve as prototypes for rows in queries. */
    class Guilds(_tableTag: Tag) extends Table[Guild](_tableTag, "guilds") {
        def * = (id, name, description, maxplayers, jointype, createddate) <> (Guild.tupled, Guild.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, name.?, description, maxplayers, jointype.?, createddate.?).shaped.<>({r=>import r._; _1.map(_=> Guild.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(45,true) */
        val name: Column[String] = column[String]("name", O.Length(45,varying=true))
        /** Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val description: Column[Option[String]] = column[Option[String]]("description", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column maxPlayers DBType(INT), Default(None) */
        val maxplayers: Column[Option[Int]] = column[Option[Int]]("maxPlayers", O.Default(None))
        /** Database column joinType DBType(VARCHAR), Length(45,true) */
        val jointype: Column[String] = column[String]("joinType", O.Length(45,varying=true))
        /** Database column createdDate DBType(INT) */
        val createddate: Column[org.joda.time.DateTime] = column[org.joda.time.DateTime]("createdDate")
    }
    /** Collection-like TableQuery object for table Guilds */
    lazy val Guilds = new TableQuery(tag => new Guilds(tag))

    /** GetResult implicit for fetching GuildGame objects using plain SQL queries */
    implicit def GetResultGuildGame(implicit e0: GR[Int]): GR[GuildGame] = GR{
        prs => import prs._
            GuildGame.tupled((<<[Int], <<[Int], <<[Int]))
    }
    /** Table description of table guilds_games. Objects of this class serve as prototypes for rows in queries. */
    class GuildsGames(_tableTag: Tag) extends Table[GuildGame](_tableTag, "guilds_games") {
        def * = (id, guildsId, gamesId) <> (GuildGame.tupled, GuildGame.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, guildsId.?, gamesId.?).shaped.<>({r=>import r._; _1.map(_=> GuildGame.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column guilds_id DBType(INT) */
        val guildsId: Column[Int] = column[Int]("guilds_id")
        /** Database column games_id DBType(INT) */
        val gamesId: Column[Int] = column[Int]("games_id")

        /** Foreign key referencing Games (database name guilds_games_ibfk_1) */
        lazy val gamesFk = foreignKey("guilds_games_ibfk_1", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing Guilds (database name guilds_games_ibfk_2) */
        lazy val guildsFk = foreignKey("guilds_games_ibfk_2", guildsId, Guilds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table GuildsGames */
    lazy val GuildsGames = new TableQuery(tag => new GuildsGames(tag))

    /** GetResult implicit for fetching GuildUser objects using plain SQL queries */
    implicit def GetResultGuildUser(implicit e0: GR[Int], e1: GR[Boolean]): GR[GuildUser] = GR{
        prs => import prs._
            GuildUser.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean]))
    }
    /** Table description of table guilds_users. Objects of this class serve as prototypes for rows in queries. */
    class GuildsUsers(_tableTag: Tag) extends Table[GuildUser](_tableTag, "guilds_users") {
        def * = (id, guildsId, usersId, iscaptain) <> (GuildUser.tupled, GuildUser.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, guildsId.?, usersId.?, iscaptain.?).shaped.<>({r=>import r._; _1.map(_=> GuildUser.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column guilds_id DBType(INT) */
        val guildsId: Column[Int] = column[Int]("guilds_id")
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column isCaptain DBType(BIT) */
        val iscaptain: Column[Boolean] = column[Boolean]("isCaptain")

        /** Foreign key referencing Guilds (database name guilds_users_ibfk_1) */
        lazy val guildsFk = foreignKey("guilds_users_ibfk_1", guildsId, Guilds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing Users (database name guilds_users_ibfk_2) */
        lazy val usersFk = foreignKey("guilds_users_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table GuildsUsers */
    lazy val GuildsUsers = new TableQuery(tag => new GuildsUsers(tag))

    /** GetResult implicit for fetching UserIdentity objects using plain SQL queries */
    implicit def GetResultUserIdentity(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[UserIdentity] = GR{
        prs => import prs._
            UserIdentity.tupled((<<[Int], <<[Int], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String]))
    }
    /** Table description of table nonactiveuseridentity. Objects of this class serve as prototypes for rows in queries. */
    class Nonactiveuseridentity(_tableTag: Tag) extends Table[UserIdentity](_tableTag, "nonactiveuseridentity") {
        def * = (id, usersId, useridentifier, providerid, email, password, firstname, lastname) <> (UserIdentity.tupled, UserIdentity.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, usersId.?, useridentifier.?, providerid.?, email, password, firstname, lastname).shaped.<>({r=>import r._; _1.map(_=> UserIdentity.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column userIdentifier DBType(VARCHAR), Length(50,true) */
        val useridentifier: Column[String] = column[String]("userIdentifier", O.Length(50,varying=true))
        /** Database column providerId DBType(VARCHAR), Length(45,true) */
        val providerid: Column[String] = column[String]("providerId", O.Length(45,varying=true))
        /** Database column email DBType(VARCHAR), Length(45,true), Default(None) */
        val email: Column[Option[String]] = column[Option[String]]("email", O.Length(45,varying=true), O.Default(None))
        /** Database column password DBType(VARCHAR), Length(100,true), Default(None) */
        val password: Column[Option[String]] = column[Option[String]]("password", O.Length(100,varying=true), O.Default(None))
        /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
        val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
        /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
        val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))

        /** Foreign key referencing Nonactiveusers (database name nonactiveuseridentity_ibfk_1) */
        lazy val nonactiveusersFk = foreignKey("nonactiveuseridentity_ibfk_1", usersId, Nonactiveusers)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table Nonactiveuseridentity */
    lazy val Nonactiveuseridentity = new TableQuery(tag => new Nonactiveuseridentity(tag))

    /** GetResult implicit for fetching User objects using plain SQL queries */
    implicit def GetResultUser(implicit e0: GR[Int], e1: GR[String], e2: GR[org.joda.time.DateTime], e3: GR[Option[String]]): GR[User] = GR{
        prs => import prs._
            User.tupled((<<[Int], <<[String], <<[org.joda.time.DateTime], <<?[String], <<?[String], <<[String], <<[String]))
    }
    /** Table description of table nonactiveusers. Objects of this class serve as prototypes for rows in queries. */
    class Nonactiveusers(_tableTag: Tag) extends Table[User](_tableTag, "nonactiveusers") {
        def * = (id, email, createddate, firstname, lastname, globalhandle, role) <> (User.tupled, User.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, email.?, createddate.?, firstname, lastname, globalhandle.?, role.?).shaped.<>({r=>import r._; _1.map(_=> User.tupled((_1.get, _2.get, _3.get, _4, _5, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column email DBType(VARCHAR), Length(50,true) */
        val email: Column[String] = column[String]("email", O.Length(50,varying=true))
        /** Database column createdDate DBType(INT) */
        val createddate: Column[org.joda.time.DateTime] = column[org.joda.time.DateTime]("createdDate")
        /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
        val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
        /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
        val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))
        /** Database column globalHandle DBType(VARCHAR), Length(45,true) */
        val globalhandle: Column[String] = column[String]("globalHandle", O.Length(45,varying=true))
        /** Database column role DBType(VARCHAR), Length(11,true) */
        val role: Column[String] = column[String]("role", O.Length(11,varying=true))
    }
    /** Collection-like TableQuery object for table Nonactiveusers */
    lazy val Nonactiveusers = new TableQuery(tag => new Nonactiveusers(tag))

    /** GetResult implicit for fetching Team objects using plain SQL queries */
    implicit def GetResultTeam(implicit e0: GR[Int], e1: GR[String], e2: GR[org.joda.time.DateTime], e3: GR[Boolean], e4: GR[Option[Int]]): GR[Team] = GR{
        prs => import prs._
            Team.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[org.joda.time.DateTime], <<[Boolean], <<[Boolean], <<?[Int]))
    }
    /** Table description of table teams. Objects of this class serve as prototypes for rows in queries. */
    class Teams(_tableTag: Tag) extends Table[Team](_tableTag, "teams") {
        def * = (id, name, jointype, tournamentId, createddate, ispresent, guildonly, guildid) <> (Team.tupled, Team.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, name.?, jointype.?, tournamentId.?, createddate.?, ispresent.?, guildonly.?, guildid).shaped.<>({r=>import r._; _1.map(_=> Team.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(45,true) */
        val name: Column[String] = column[String]("name", O.Length(45,varying=true))
        /** Database column joinType DBType(VARCHAR), Length(45,true) */
        val jointype: Column[String] = column[String]("joinType", O.Length(45,varying=true))
        /** Database column tournament_id DBType(INT) */
        val tournamentId: Column[Int] = column[Int]("tournament_id")
        /** Database column createdDate DBType(INT) */
        val createddate: Column[org.joda.time.DateTime] = column[org.joda.time.DateTime]("createdDate")
        /** Database column isPresent DBType(BIT) */
        val ispresent: Column[Boolean] = column[Boolean]("isPresent")
        /** Database column guildOnly DBType(BIT) */
        val guildonly: Column[Boolean] = column[Boolean]("guildOnly")
        /** Database column guildId DBType(INT), Default(None) */
        val guildid: Column[Option[Int]] = column[Option[Int]]("guildId", O.Default(None))

        /** Foreign key referencing Tournaments (database name teams_ibfk_1) */
        lazy val tournamentsFk = foreignKey("teams_ibfk_1", tournamentId, Tournaments)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table Teams */
    lazy val Teams = new TableQuery(tag => new Teams(tag))

    /** GetResult implicit for fetching TeamUser objects using plain SQL queries */
    implicit def GetResultTeamUser(implicit e0: GR[Int], e1: GR[Boolean]): GR[TeamUser] = GR{
        prs => import prs._
            TeamUser.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean]))
    }
    /** Table description of table teams_users. Objects of this class serve as prototypes for rows in queries. */
    class TeamsUsers(_tableTag: Tag) extends Table[TeamUser](_tableTag, "teams_users") {
        def * = (id, teamsId, usersId, iscaptain) <> (TeamUser.tupled, TeamUser.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, teamsId.?, usersId.?, iscaptain.?).shaped.<>({r=>import r._; _1.map(_=> TeamUser.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column teams_id DBType(INT) */
        val teamsId: Column[Int] = column[Int]("teams_id")
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column isCaptain DBType(BIT) */
        val iscaptain: Column[Boolean] = column[Boolean]("isCaptain")

        /** Foreign key referencing Teams (database name teams_users_ibfk_1) */
        lazy val teamsFk = foreignKey("teams_users_ibfk_1", teamsId, Teams)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing Users (database name teams_users_ibfk_2) */
        lazy val usersFk = foreignKey("teams_users_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table TeamsUsers */
    lazy val TeamsUsers = new TableQuery(tag => new TeamsUsers(tag))

    /** GetResult implicit for fetching Tournament objects using plain SQL queries */
    implicit def GetResultTournament(implicit e0: GR[Int], e1: GR[String]): GR[Tournament] = GR{
        prs => import prs._
            Tournament.tupled((<<[Int], <<[Int], <<[String], <<[Int], <<[Int]))
    }
    /** Table description of table tournaments. Objects of this class serve as prototypes for rows in queries. */
    class Tournaments(_tableTag: Tag) extends Table[Tournament](_tableTag, "tournaments") {
        def * = (id, tournamenttypesId, registrationtype, gamesId, eventsId) <> (Tournament.tupled, Tournament.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, tournamenttypesId.?, registrationtype.?, gamesId.?, eventsId.?).shaped.<>({r=>import r._; _1.map(_=> Tournament.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column tournamenttypes_id DBType(INT) */
        val tournamenttypesId: Column[Int] = column[Int]("tournamenttypes_id")
        /** Database column registrationType DBType(VARCHAR), Length(15,true) */
        val registrationtype: Column[String] = column[String]("registrationType", O.Length(15,varying=true))
        /** Database column games_id DBType(INT) */
        val gamesId: Column[Int] = column[Int]("games_id")
        /** Database column events_id DBType(INT) */
        val eventsId: Column[Int] = column[Int]("events_id")

        /** Foreign key referencing Events (database name tournaments_ibfk_3) */
        lazy val eventsFk = foreignKey("tournaments_ibfk_3", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
        /** Foreign key referencing Games (database name tournaments_ibfk_2) */
        lazy val gamesFk = foreignKey("tournaments_ibfk_2", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
        /** Foreign key referencing TournamentsTypes (database name tournaments_ibfk_1) */
        lazy val tournamentsTypesFk = foreignKey("tournaments_ibfk_1", tournamenttypesId, TournamentsTypes)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    }
    /** Collection-like TableQuery object for table Tournaments */
    lazy val Tournaments = new TableQuery(tag => new Tournaments(tag))

    /** GetResult implicit for fetching TournamentDetail objects using plain SQL queries */
    implicit def GetResultTournamentDetail(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[org.joda.time.DateTime]], e3: GR[Option[Int]]): GR[TournamentDetail] = GR{
        prs => import prs._
            TournamentDetail.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[org.joda.time.DateTime], <<?[org.joda.time.DateTime], <<?[Int], <<?[Int], <<?[Int], <<?[Int]))
    }
    /** Table description of table tournaments_details. Objects of this class serve as prototypes for rows in queries. */
    class TournamentsDetails(_tableTag: Tag) extends Table[TournamentDetail](_tableTag, "tournaments_details") {
        def * = (tournamentId, name, gameplayed, description, location, locationsub, rules, prizes, streams, servers, timestart, timeend, teamminsize, teammaxsize, playerminsize, playermaxsize) <> (TournamentDetail.tupled, TournamentDetail.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (tournamentId.?, name, gameplayed, description, location, locationsub, rules, prizes, streams, servers, timestart, timeend, teamminsize, teammaxsize, playerminsize, playermaxsize).shaped.<>({r=>import r._; _1.map(_=> TournamentDetail.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column tournament_id DBType(INT), PrimaryKey */
        val tournamentId: Column[Int] = column[Int]("tournament_id", O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(45,true), Default(None) */
        val name: Column[Option[String]] = column[Option[String]]("name", O.Length(45,varying=true), O.Default(None))
        /** Database column gamePlayed DBType(VARCHAR), Length(45,true), Default(None) */
        val gameplayed: Column[Option[String]] = column[Option[String]]("gamePlayed", O.Length(45,varying=true), O.Default(None))
        /** Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val description: Column[Option[String]] = column[Option[String]]("description", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column location DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val location: Column[Option[String]] = column[Option[String]]("location", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column locationsub DBType(VARCHAR), Length(255,true), Default(None) */
        val locationsub: Column[Option[String]] = column[Option[String]]("locationsub", O.Length(255,varying=true), O.Default(None))
        /** Database column rules DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val rules: Column[Option[String]] = column[Option[String]]("rules", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column prizes DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val prizes: Column[Option[String]] = column[Option[String]]("prizes", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column streams DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val streams: Column[Option[String]] = column[Option[String]]("streams", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column servers DBType(LONGTEXT), Length(2147483647,true), Default(None) */
        val servers: Column[Option[String]] = column[Option[String]]("servers", O.Length(2147483647,varying=true), O.Default(None))
        /** Database column timeStart DBType(INT), Default(None) */
        val timestart: Column[Option[org.joda.time.DateTime]] = column[Option[org.joda.time.DateTime]]("timeStart", O.Default(None))
        /** Database column timeEnd DBType(INT), Default(None) */
        val timeend: Column[Option[org.joda.time.DateTime]] = column[Option[org.joda.time.DateTime]]("timeEnd", O.Default(None))
        /** Database column teamMinSize DBType(INT), Default(None) */
        val teamminsize: Column[Option[Int]] = column[Option[Int]]("teamMinSize", O.Default(None))
        /** Database column teamMaxSize DBType(INT), Default(None) */
        val teammaxsize: Column[Option[Int]] = column[Option[Int]]("teamMaxSize", O.Default(None))
        /** Database column playerMinSize DBType(INT), Default(None) */
        val playerminsize: Column[Option[Int]] = column[Option[Int]]("playerMinSize", O.Default(None))
        /** Database column playerMaxSize DBType(INT), Default(None) */
        val playermaxsize: Column[Option[Int]] = column[Option[Int]]("playerMaxSize", O.Default(None))

        /** Foreign key referencing Tournaments (database name tournaments_details_ibfk_1) */
        lazy val tournamentsFk = foreignKey("tournaments_details_ibfk_1", tournamentId, Tournaments)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table TournamentsDetails */
    lazy val TournamentsDetails = new TableQuery(tag => new TournamentsDetails(tag))

    /** GetResult implicit for fetching TournamentType objects using plain SQL queries */
    implicit def GetResultTournamentType(implicit e0: GR[Int], e1: GR[String], e2: GR[Boolean]): GR[TournamentType] = GR{
        prs => import prs._
            TournamentType.tupled((<<[Int], <<[String], <<[Boolean]))
    }
    /** Table description of table tournaments_types. Objects of this class serve as prototypes for rows in queries. */
    class TournamentsTypes(_tableTag: Tag) extends Table[TournamentType](_tableTag, "tournaments_types") {
        def * = (id, name, teamplay) <> (TournamentType.tupled, TournamentType.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, name.?, teamplay.?).shaped.<>({r=>import r._; _1.map(_=> TournamentType.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column Id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("Id", O.AutoInc, O.PrimaryKey)
        /** Database column name DBType(VARCHAR), Length(50,true) */
        val name: Column[String] = column[String]("name", O.Length(50,varying=true))
        /** Database column teamPlay DBType(BIT) */
        val teamplay: Column[Boolean] = column[Boolean]("teamPlay")
    }
    /** Collection-like TableQuery object for table TournamentsTypes */
    lazy val TournamentsTypes = new TableQuery(tag => new TournamentsTypes(tag))

    /** GetResult implicit for fetching TournamentUser objects using plain SQL queries */
    implicit def GetResultTournamentUser(implicit e0: GR[Int], e1: GR[Boolean]): GR[TournamentUser] = GR{
        prs => import prs._
            TournamentUser.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean], <<[Boolean], <<[Boolean]))
    }
    /** Table description of table tournaments_users. Objects of this class serve as prototypes for rows in queries. */
    class TournamentsUsers(_tableTag: Tag) extends Table[TournamentUser](_tableTag, "tournaments_users") {
        def * = (id, usersId, tournamentId, ispresent, isadmin, ismoderator) <> (TournamentUser.tupled, TournamentUser.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, usersId.?, tournamentId.?, ispresent.?, isadmin.?, ismoderator.?).shaped.<>({r=>import r._; _1.map(_=> TournamentUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column tournament_id DBType(INT) */
        val tournamentId: Column[Int] = column[Int]("tournament_id")
        /** Database column isPresent DBType(BIT) */
        val ispresent: Column[Boolean] = column[Boolean]("isPresent")
        /** Database column isAdmin DBType(BIT) */
        val isadmin: Column[Boolean] = column[Boolean]("isAdmin")
        /** Database column isModerator DBType(BIT) */
        val ismoderator: Column[Boolean] = column[Boolean]("isModerator")

        /** Foreign key referencing Tournaments (database name tournaments_users_ibfk_1) */
        lazy val tournamentsFk = foreignKey("tournaments_users_ibfk_1", tournamentId, Tournaments)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
        /** Foreign key referencing Users (database name tournaments_users_ibfk_2) */
        lazy val usersFk = foreignKey("tournaments_users_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table TournamentsUsers */
    lazy val TournamentsUsers = new TableQuery(tag => new TournamentsUsers(tag))

    /** GetResult implicit for fetching User objects using plain SQL queries */
    implicit def GetResultUser(implicit e0: GR[Int], e1: GR[String], e2: GR[org.joda.time.DateTime], e3: GR[Option[String]]): GR[User] = GR{
        prs => import prs._
            User.tupled((<<[Int], <<[String], <<[org.joda.time.DateTime], <<?[String], <<?[String], <<[String], <<[String]))
    }
    /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
    class Users(_tableTag: Tag) extends Table[User](_tableTag, "users") {
        def * = (id, email, createddate, firstname, lastname, globalhandle, role) <> (User.tupled, User.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, email.?, createddate.?, firstname, lastname, globalhandle.?, role.?).shaped.<>({r=>import r._; _1.map(_=> User.tupled((_1.get, _2.get, _3.get, _4, _5, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column email DBType(VARCHAR), Length(50,true) */
        val email: Column[String] = column[String]("email", O.Length(50,varying=true))
        /** Database column createdDate DBType(INT) */
        val createddate: Column[org.joda.time.DateTime] = column[org.joda.time.DateTime]("createdDate")
        /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
        val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
        /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
        val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))
        /** Database column globalHandle DBType(VARCHAR), Length(45,true) */
        val globalhandle: Column[String] = column[String]("globalHandle", O.Length(45,varying=true))
        /** Database column role DBType(VARCHAR), Length(11,true) */
        val role: Column[String] = column[String]("role", O.Length(11,varying=true))
    }
    /** Collection-like TableQuery object for table Users */
    lazy val Users = new TableQuery(tag => new Users(tag))

    /** GetResult implicit for fetching UserIdentity objects using plain SQL queries */
    implicit def GetResultUserIdentity(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[UserIdentity] = GR{
        prs => import prs._
            UserIdentity.tupled((<<[Int], <<[Int], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String]))
    }
    /** Table description of table users_identity. Objects of this class serve as prototypes for rows in queries. */
    class UsersIdentity(_tableTag: Tag) extends Table[UserIdentity](_tableTag, "users_identity") {
        def * = (id, usersId, useridentifier, providerid, email, password, firstname, lastname) <> (UserIdentity.tupled, UserIdentity.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, usersId.?, useridentifier.?, providerid.?, email, password, firstname, lastname).shaped.<>({r=>import r._; _1.map(_=> UserIdentity.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column userIdentifier DBType(VARCHAR), Length(50,true) */
        val useridentifier: Column[String] = column[String]("userIdentifier", O.Length(50,varying=true))
        /** Database column providerId DBType(VARCHAR), Length(45,true) */
        val providerid: Column[String] = column[String]("providerId", O.Length(45,varying=true))
        /** Database column email DBType(VARCHAR), Length(45,true), Default(None) */
        val email: Column[Option[String]] = column[Option[String]]("email", O.Length(45,varying=true), O.Default(None))
        /** Database column password DBType(VARCHAR), Length(100,true), Default(None) */
        val password: Column[Option[String]] = column[Option[String]]("password", O.Length(100,varying=true), O.Default(None))
        /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
        val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
        /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
        val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))

        /** Foreign key referencing Users (database name users_identity_ibfk_1) */
        lazy val usersFk = foreignKey("users_identity_ibfk_1", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table UsersIdentity */
    lazy val UsersIdentity = new TableQuery(tag => new UsersIdentity(tag))

    /** GetResult implicit for fetching UserPlatformProfile objects using plain SQL queries */
    implicit def GetResultUserPlatformProfile(implicit e0: GR[Int], e1: GR[String]): GR[UserPlatformProfile] = GR{
        prs => import prs._
            UserPlatformProfile.tupled((<<[Int], <<[Int], <<[String], <<[String]))
    }
    /** Table description of table users_platform_profile. Objects of this class serve as prototypes for rows in queries. */
    class UsersPlatformProfile(_tableTag: Tag) extends Table[UserPlatformProfile](_tableTag, "users_platform_profile") {
        def * = (id, usersId, platform, identifier) <> (UserPlatformProfile.tupled, UserPlatformProfile.unapply)
        /** Maps whole row to an option. Useful for outer joins. */
        def ? = (id.?, usersId.?, platform.?, identifier.?).shaped.<>({r=>import r._; _1.map(_=> UserPlatformProfile.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

        /** Database column id DBType(INT), AutoInc, PrimaryKey */
        val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
        /** Database column users_id DBType(INT) */
        val usersId: Column[Int] = column[Int]("users_id")
        /** Database column platform DBType(VARCHAR), Length(45,true) */
        val platform: Column[String] = column[String]("platform", O.Length(45,varying=true))
        /** Database column identifier DBType(VARCHAR), Length(45,true) */
        val identifier: Column[String] = column[String]("identifier", O.Length(45,varying=true))

        /** Foreign key referencing Users (database name users_platform_profile_ibfk_1) */
        lazy val usersFk = foreignKey("users_platform_profile_ibfk_1", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    }
    /** Collection-like TableQuery object for table UsersPlatformProfile */
    lazy val UsersPlatformProfile = new TableQuery(tag => new UsersPlatformProfile(tag))
}
