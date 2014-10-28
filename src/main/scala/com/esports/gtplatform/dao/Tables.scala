package dao

import com.escalatesoft.subcut.inject.{BoundToModule, AutoInjectable, BindingModule, Injectable}
import models.Event

import scala.slick.jdbc.JdbcBackend

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Apikeys.ddl ++ Confirmationtokens.ddl ++ Eventdetails.ddl ++ Eventpayments.ddl ++ Events.ddl ++ Games.ddl ++ GamesEvents.ddl ++ GamesTournamenttypes.ddl ++ Guilds.ddl ++ GuildsGames.ddl ++ GuildsUsers.ddl ++ Invites.ddl ++ Nonactiveuseridentity.ddl ++ Nonactiveusers.ddl ++ Passwordtokens.ddl ++ Teams.ddl ++ TeamsUsers.ddl ++ Tokens.ddl ++ Tournament.ddl ++ Tournamentdetails.ddl ++ Tournamenttypes.ddl ++ UserEvents.ddl ++ Useridentity.ddl ++ Userplatformprofile.ddl ++ Users.ddl ++ UserTournaments.ddl

  /** Entity class storing rows of table Apikeys
   *  @param id Database column id DBType(INT), PrimaryKey
   *  @param apitoken Database column apiToken DBType(VARCHAR), Length(100,true) */
  case class ApikeysRow(id: Int, apitoken: String)
  /** GetResult implicit for fetching ApikeysRow objects using plain SQL queries */
  implicit def GetResultApikeysRow(implicit e0: GR[Int], e1: GR[String]): GR[ApikeysRow] = GR{
    prs => import prs._
    ApikeysRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table apikeys. Objects of this class serve as prototypes for rows in queries. */
  class Apikeys(_tableTag: Tag) extends Table[ApikeysRow](_tableTag, "apikeys") {
    def * = (id, apitoken) <> (ApikeysRow.tupled, ApikeysRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, apitoken.?).shaped.<>({r=>import r._; _1.map(_=> ApikeysRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column apiToken DBType(VARCHAR), Length(100,true) */
    val apitoken: Column[String] = column[String]("apiToken", O.Length(100,varying=true))

    /** Foreign key referencing Users (database name apikeys_ibfk_1) */
    lazy val usersFk = foreignKey("apikeys_ibfk_1", id, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (apitoken) (database name apiToken_UNIQUE) */
    val index1 = index("apiToken_UNIQUE", apitoken, unique=true)
  }
  /** Collection-like TableQuery object for table Apikeys */
  lazy val Apikeys = new TableQuery(tag => new Apikeys(tag))

  /** Entity class storing rows of table Confirmationtokens
   *  @param useridentid Database column userIdentId DBType(INT), PrimaryKey
   *  @param token Database column token DBType(VARCHAR), Length(50,true)
   *  @param eventid Database column eventId DBType(INT), Default(None) */
  case class ConfirmationtokensRow(useridentid: Int, token: String, eventid: Option[Int] = None)
  /** GetResult implicit for fetching ConfirmationtokensRow objects using plain SQL queries */
  implicit def GetResultConfirmationtokensRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]]): GR[ConfirmationtokensRow] = GR{
    prs => import prs._
    ConfirmationtokensRow.tupled((<<[Int], <<[String], <<?[Int]))
  }
  /** Table description of table confirmationtokens. Objects of this class serve as prototypes for rows in queries. */
  class Confirmationtokens(_tableTag: Tag) extends Table[ConfirmationtokensRow](_tableTag, "confirmationtokens") {
    def * = (useridentid, token, eventid) <> (ConfirmationtokensRow.tupled, ConfirmationtokensRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (useridentid.?, token.?, eventid).shaped.<>({r=>import r._; _1.map(_=> ConfirmationtokensRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column userIdentId DBType(INT), PrimaryKey */
    val useridentid: Column[Int] = column[Int]("userIdentId", O.PrimaryKey)
    /** Database column token DBType(VARCHAR), Length(50,true) */
    val token: Column[String] = column[String]("token", O.Length(50,varying=true))
    /** Database column eventId DBType(INT), Default(None) */
    val eventid: Column[Option[Int]] = column[Option[Int]]("eventId", O.Default(None))
  }
  /** Collection-like TableQuery object for table Confirmationtokens */
  lazy val Confirmationtokens = new TableQuery(tag => new Confirmationtokens(tag))

  /** Entity class storing rows of table Eventdetails
   *  @param eventsId Database column events_id DBType(INT), AutoInc, PrimaryKey
   *  @param location Database column location DBType(VARCHAR), Length(45,true), Default(None)
   *  @param address Database column address DBType(VARCHAR), Length(45,true), Default(None)
   *  @param city Database column city DBType(VARCHAR), Length(45,true), Default(None)
   *  @param state Database column state DBType(VARCHAR), Length(45,true), Default(None)
   *  @param timestart Database column timeStart DBType(INT), Default(None)
   *  @param timeend Database column timeEnd DBType(INT), Default(None)
   *  @param description Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param rules Database column rules DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param prizes Database column prizes DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param streams Database column streams DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param servers Database column servers DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param scheduledevents Database column scheduledevents DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param credits Database column credits DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param faq Database column faq DBType(LONGTEXT), Length(2147483647,true), Default(None) */
  case class EventdetailsRow(eventsId: Int, location: Option[String] = None, address: Option[String] = None, city: Option[String] = None, state: Option[String] = None, timestart: Option[Int] = None, timeend: Option[Int] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, scheduledevents: Option[String] = None, credits: Option[String] = None, faq: Option[String] = None)
  /** GetResult implicit for fetching EventdetailsRow objects using plain SQL queries */
  implicit def GetResultEventdetailsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[EventdetailsRow] = GR{
    prs => import prs._
    EventdetailsRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Int], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table eventdetails. Objects of this class serve as prototypes for rows in queries. */
  class Eventdetails(_tableTag: Tag) extends Table[EventdetailsRow](_tableTag, "eventdetails") {
    def * = (eventsId, location, address, city, state, timestart, timeend, description, rules, prizes, streams, servers, scheduledevents, credits, faq) <> (EventdetailsRow.tupled, EventdetailsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (eventsId.?, location, address, city, state, timestart, timeend, description, rules, prizes, streams, servers, scheduledevents, credits, faq).shaped.<>({r=>import r._; _1.map(_=> EventdetailsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    /** Database column timeStart DBType(INT), Default(None) */
    val timestart: Column[Option[Int]] = column[Option[Int]]("timeStart", O.Default(None))
    /** Database column timeEnd DBType(INT), Default(None) */
    val timeend: Column[Option[Int]] = column[Option[Int]]("timeEnd", O.Default(None))
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
    /** Database column scheduledevents DBType(LONGTEXT), Length(2147483647,true), Default(None) */
    val scheduledevents: Column[Option[String]] = column[Option[String]]("scheduledevents", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column credits DBType(LONGTEXT), Length(2147483647,true), Default(None) */
    val credits: Column[Option[String]] = column[Option[String]]("credits", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column faq DBType(LONGTEXT), Length(2147483647,true), Default(None) */
    val faq: Column[Option[String]] = column[Option[String]]("faq", O.Length(2147483647,varying=true), O.Default(None))

    /** Foreign key referencing Events (database name eventdetails_ibfk_1) */
    lazy val eventsFk = foreignKey("eventdetails_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Eventdetails */
  lazy val Eventdetails = new TableQuery(tag => new Eventdetails(tag))

  /** Entity class storing rows of table Eventpayments
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param eventsId Database column events_id DBType(INT)
   *  @param paytype Database column paytype DBType(VARCHAR), Length(45,true)
   *  @param secret Database column secret DBType(VARCHAR), Length(45,true), Default(None)
   *  @param public Database column public DBType(VARCHAR), Length(45,true), Default(None)
   *  @param address Database column address DBType(VARCHAR), Length(45,true), Default(None)
   *  @param amount Database column amount DBType(DOUBLE)
   *  @param isenabled Database column isenabled DBType(SMALLINT), Default(1) */
  case class EventpaymentsRow(id: Int, eventsId: Int, paytype: String, secret: Option[String] = None, public: Option[String] = None, address: Option[String] = None, amount: Double, isenabled: Short = 1)
  /** GetResult implicit for fetching EventpaymentsRow objects using plain SQL queries */
  implicit def GetResultEventpaymentsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Double], e4: GR[Short]): GR[EventpaymentsRow] = GR{
    prs => import prs._
    EventpaymentsRow.tupled((<<[Int], <<[Int], <<[String], <<?[String], <<?[String], <<?[String], <<[Double], <<[Short]))
  }
  /** Table description of table eventpayments. Objects of this class serve as prototypes for rows in queries. */
  class Eventpayments(_tableTag: Tag) extends Table[EventpaymentsRow](_tableTag, "eventpayments") {
    def * = (id, eventsId, paytype, secret, public, address, amount, isenabled) <> (EventpaymentsRow.tupled, EventpaymentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, eventsId.?, paytype.?, secret, public, address, amount.?, isenabled.?).shaped.<>({r=>import r._; _1.map(_=> EventpaymentsRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    /** Database column isenabled DBType(SMALLINT), Default(1) */
    val isenabled: Column[Short] = column[Short]("isenabled", O.Default(1))

    /** Foreign key referencing Events (database name eventpayments_ibfk_1) */
    lazy val eventsFk = foreignKey("eventpayments_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Eventpayments */
  lazy val Eventpayments = new TableQuery(tag => new Eventpayments(tag))

  /** Entity class storing rows of table Events
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param eventtype Database column eventType DBType(VARCHAR), Length(15,true)
   *  @param name Database column name DBType(VARCHAR), Length(45,true) */
  case class EventsRow(id: Int, eventtype: String, name: String)
  /** GetResult implicit for fetching EventsRow objects using plain SQL queries */
  implicit def GetResultEventsRow(implicit e0: GR[Int], e1: GR[String]): GR[EventsRow] = GR{
    prs => import prs._
    EventsRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table events. Objects of this class serve as prototypes for rows in queries. */
  class Events(_tableTag: Tag) extends Table[EventsRow](_tableTag, "events") {
    def * = (id, eventtype, name) <> (EventsRow.tupled, EventsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, eventtype.?, name.?).shaped.<>({r=>import r._; _1.map(_=> EventsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column eventType DBType(VARCHAR), Length(15,true) */
    val eventtype: Column[String] = column[String]("eventType", O.Length(15,varying=true))
    /** Database column name DBType(VARCHAR), Length(45,true) */
    val name: Column[String] = column[String]("name", O.Length(45,varying=true))
  }
  /** Collection-like TableQuery object for table Events */
  lazy val Events = new TableQuery(tag => new Events(tag))

  /** Entity class storing rows of table Games
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param name Database column name DBType(VARCHAR), Length(45,true)
   *  @param publisher Database column publisher DBType(VARCHAR), Length(45,true), Default(None)
   *  @param website Database column website DBType(VARCHAR), Length(100,true), Default(None)
   *  @param gametype Database column gameType DBType(VARCHAR), Length(20,true)
   *  @param userplay Database column userPlay DBType(TINYINT), Default(1)
   *  @param teamplay Database column teamPlay DBType(TINYINT), Default(1)
   *  @param logofilename Database column logoFilename DBType(VARCHAR), Length(30,true), Default(None) */
  case class GamesRow(id: Int, name: String, publisher: Option[String] = None, website: Option[String] = None, gametype: String, userplay: Byte = 1, teamplay: Byte = 1, logofilename: Option[String] = None)
  {

      var tt: List[TournamenttypesRow] = Nil
      var link: List[GamesTournamenttypesRow] = Nil
  }

  /** GetResult implicit for fetching GamesRow objects using plain SQL queries */
  implicit def GetResultGamesRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Byte]): GR[GamesRow] = GR{
    prs => import prs._
    GamesRow.tupled((<<[Int], <<[String], <<?[String], <<?[String], <<[String], <<[Byte], <<[Byte], <<?[String]))
  }
    implicit class GamesExtensions[C[_]](q: Query[Games, GamesRow, C]) {

/*        def tournamentTypes: List[TournamenttypesRow] ={
                val blah = for {
                    link <- GamesTournamenttypes if link.gamesId == this
                    tt <- Tournamenttypes if tt.id == link.tournamenttypesId
                } yield tt
                blah.list
            }*/
def tournamentTypes(db: JdbcBackend.DatabaseDef): List[TournamenttypesRow] = {
    val blah = db.withSession { implicit session =>
        q.leftJoin(GamesTournamenttypes).on { case (g, gt) => g.id === gt.gamesId}
            .leftJoin(Tournamenttypes).on { case ((g, gt), tt) => gt.tournamenttypesId === tt.id}
            .map { case ((g, gt), tt) =>
            tt
        }.list
    }
    blah
}

        def hydrated(implicit session: JdbcBackend.Session): List[GamesRow] = {
            q.leftJoin(GamesTournamenttypes).on { case (g, gt) => g.id === gt.gamesId}
                .leftJoin(Tournamenttypes).on { case ((g, gt), tt) => gt.tournamenttypesId === tt.id}
                /*                   .map { case ((g, gt), tt) =>
                                    (gt,tt)
                                }.list*/
                .mapResult { case ((g, gt), tt) =>
                g.tt = g.tt.:+(tt)
                g.link = g.link.:+(gt)
                g
            }
                .list.groupBy(_.id).mapValues { groupsWithSameId =>
                groupsWithSameId.reduce { (previousGroup, group) =>
                    previousGroup.tt = previousGroup.tt.++(group.tt)
                    previousGroup.link = previousGroup.link.++(group.link)
                    previousGroup
                }
            }.values.toList
        }

        //q.join(GamesTournamenttypes).on(_.id === _.gamesId).map(_.)
    }

  /** Table description of table games. Objects of this class serve as prototypes for rows in queries. */
  class Games(_tableTag: Tag) extends Table[GamesRow](_tableTag, "games") {
    def * = (id, name, publisher, website, gametype, userplay, teamplay, logofilename) <> (GamesRow.tupled, GamesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, publisher, website, gametype.?, userplay.?, teamplay.?, logofilename).shaped.<>({r=>import r._; _1.map(_=> GamesRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    /** Database column userPlay DBType(TINYINT), Default(1) */
    val userplay: Column[Byte] = column[Byte]("userPlay", O.Default(1))
    /** Database column teamPlay DBType(TINYINT), Default(1) */
    val teamplay: Column[Byte] = column[Byte]("teamPlay", O.Default(1))
    /** Database column logoFilename DBType(VARCHAR), Length(30,true), Default(None) */
    val logofilename: Column[Option[String]] = column[Option[String]]("logoFilename", O.Length(30,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Games */
  lazy val Games = new TableQuery(tag => new Games(tag))

  /** Entity class storing rows of table GamesEvents
   *  @param gamesId Database column games_id DBType(INT)
   *  @param eventsId Database column events_id DBType(INT) */
  case class GamesEventsRow(gamesId: Int, eventsId: Int)
  /** GetResult implicit for fetching GamesEventsRow objects using plain SQL queries */
  implicit def GetResultGamesEventsRow(implicit e0: GR[Int]): GR[GamesEventsRow] = GR{
    prs => import prs._
    GamesEventsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table games_events. Objects of this class serve as prototypes for rows in queries. */
  class GamesEvents(_tableTag: Tag) extends Table[GamesEventsRow](_tableTag, "games_events") {
    def * = (gamesId, eventsId) <> (GamesEventsRow.tupled, GamesEventsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (gamesId.?, eventsId.?).shaped.<>({r=>import r._; _1.map(_=> GamesEventsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column games_id DBType(INT) */
    val gamesId: Column[Int] = column[Int]("games_id")
    /** Database column events_id DBType(INT) */
    val eventsId: Column[Int] = column[Int]("events_id")

    /** Foreign key referencing Events (database name games_events_ibfk_1) */
    lazy val eventsFk = foreignKey("games_events_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Games (database name games_events_ibfk_2) */
    lazy val gamesFk = foreignKey("games_events_ibfk_2", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table GamesEvents */
  lazy val GamesEvents = new TableQuery(tag => new GamesEvents(tag))

  /** Entity class storing rows of table GamesTournamenttypes
   *  @param gamesId Database column games_id DBType(INT)
   *  @param tournamenttypesId Database column tournamenttypes_id DBType(INT)
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey */
  case class GamesTournamenttypesRow(gamesId: Int, tournamenttypesId: Int, id: Int)
  /** GetResult implicit for fetching GamesTournamenttypesRow objects using plain SQL queries */
  implicit def GetResultGamesTournamenttypesRow(implicit e0: GR[Int]): GR[GamesTournamenttypesRow] = GR{
    prs => import prs._
    GamesTournamenttypesRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table games_tournamenttypes. Objects of this class serve as prototypes for rows in queries. */
  class GamesTournamenttypes(_tableTag: Tag) extends Table[GamesTournamenttypesRow](_tableTag, "games_tournamenttypes") {
    def * = (gamesId, tournamenttypesId, id) <> (GamesTournamenttypesRow.tupled, GamesTournamenttypesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (gamesId.?, tournamenttypesId.?, id.?).shaped.<>({r=>import r._; _1.map(_=> GamesTournamenttypesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column games_id DBType(INT) */
    val gamesId: Column[Int] = column[Int]("games_id")
    /** Database column tournamenttypes_id DBType(INT) */
    val tournamenttypesId: Column[Int] = column[Int]("tournamenttypes_id")
    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    /** Foreign key referencing Games (database name games_tournamenttypes_ibfk_1) */
    lazy val gamesFk = foreignKey("games_tournamenttypes_ibfk_1", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Tournamenttypes (database name games_tournamenttypes_ibfk_2) */
    lazy val tournamenttypesFk = foreignKey("games_tournamenttypes_ibfk_2", tournamenttypesId, Tournamenttypes)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table GamesTournamenttypes */
  lazy val GamesTournamenttypes = new TableQuery(tag => new GamesTournamenttypes(tag))

  /** Entity class storing rows of table Guilds
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param name Database column name DBType(VARCHAR), Length(45,true)
   *  @param description Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param maxplayers Database column maxPlayers DBType(INT), Default(None)
   *  @param jointype Database column joinType DBType(VARCHAR), Length(45,true)
   *  @param createddate Database column createdDate DBType(INT) */
  case class GuildsRow(id: Int, name: String, description: Option[String] = None, maxplayers: Option[Int] = None, jointype: String, createddate: Int)
  /** GetResult implicit for fetching GuildsRow objects using plain SQL queries */
  implicit def GetResultGuildsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[Int]]): GR[GuildsRow] = GR{
    prs => import prs._
    GuildsRow.tupled((<<[Int], <<[String], <<?[String], <<?[Int], <<[String], <<[Int]))
  }
  /** Table description of table guilds. Objects of this class serve as prototypes for rows in queries. */
  class Guilds(_tableTag: Tag) extends Table[GuildsRow](_tableTag, "guilds") {
    def * = (id, name, description, maxplayers, jointype, createddate) <> (GuildsRow.tupled, GuildsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, description, maxplayers, jointype.?, createddate.?).shaped.<>({r=>import r._; _1.map(_=> GuildsRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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
    val createddate: Column[Int] = column[Int]("createdDate")
  }
  /** Collection-like TableQuery object for table Guilds */
  lazy val Guilds = new TableQuery(tag => new Guilds(tag))

  /** Entity class storing rows of table GuildsGames
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param guildsId Database column guilds_id DBType(INT)
   *  @param gamesId Database column games_id DBType(INT) */
  case class GuildsGamesRow(id: Int, guildsId: Int, gamesId: Int)
  /** GetResult implicit for fetching GuildsGamesRow objects using plain SQL queries */
  implicit def GetResultGuildsGamesRow(implicit e0: GR[Int]): GR[GuildsGamesRow] = GR{
    prs => import prs._
    GuildsGamesRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table guilds_games. Objects of this class serve as prototypes for rows in queries. */
  class GuildsGames(_tableTag: Tag) extends Table[GuildsGamesRow](_tableTag, "guilds_games") {
    def * = (id, guildsId, gamesId) <> (GuildsGamesRow.tupled, GuildsGamesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, guildsId.?, gamesId.?).shaped.<>({r=>import r._; _1.map(_=> GuildsGamesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

  /** Entity class storing rows of table GuildsUsers
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param guildsId Database column guilds_id DBType(INT)
   *  @param usersId Database column users_id DBType(INT)
   *  @param iscaptain Database column isCaptain DBType(BIT) */
  case class GuildsUsersRow(id: Int, guildsId: Int, usersId: Int, iscaptain: Boolean)
  /** GetResult implicit for fetching GuildsUsersRow objects using plain SQL queries */
  implicit def GetResultGuildsUsersRow(implicit e0: GR[Int], e1: GR[Boolean]): GR[GuildsUsersRow] = GR{
    prs => import prs._
    GuildsUsersRow.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean]))
  }
  /** Table description of table guilds_users. Objects of this class serve as prototypes for rows in queries. */
  class GuildsUsers(_tableTag: Tag) extends Table[GuildsUsersRow](_tableTag, "guilds_users") {
    def * = (id, guildsId, usersId, iscaptain) <> (GuildsUsersRow.tupled, GuildsUsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, guildsId.?, usersId.?, iscaptain.?).shaped.<>({r=>import r._; _1.map(_=> GuildsUsersRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

  /** Entity class storing rows of table Invites
   *  @param id Database column id DBType(INT), PrimaryKey
   *  @param author Database column author DBType(INT)
   *  @param receiver Database column receiver DBType(INT), Default(None)
   *  @param message Database column message DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param createdon Database column createdOn DBType(INT)
   *  @param tournamentId Database column tournament_id DBType(INT), Default(None)
   *  @param eventsId Database column events_id DBType(INT), Default(None)
   *  @param guildsId Database column guilds_id DBType(INT), Default(None)
   *  @param teamsId Database column teams_id DBType(INT), Default(None)
   *  @param usersId Database column users_id DBType(INT), Default(None) */
  case class InvitesRow(id: Int, author: Int, receiver: Option[Int] = None, message: Option[String] = None, createdon: Int, tournamentId: Option[Int] = None, eventsId: Option[Int] = None, guildsId: Option[Int] = None, teamsId: Option[Int] = None, usersId: Option[Int] = None)
  /** GetResult implicit for fetching InvitesRow objects using plain SQL queries */
  implicit def GetResultInvitesRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]]): GR[InvitesRow] = GR{
    prs => import prs._
    InvitesRow.tupled((<<[Int], <<[Int], <<?[Int], <<?[String], <<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table invites. Objects of this class serve as prototypes for rows in queries. */
  class Invites(_tableTag: Tag) extends Table[InvitesRow](_tableTag, "invites") {
    def * = (id, author, receiver, message, createdon, tournamentId, eventsId, guildsId, teamsId, usersId) <> (InvitesRow.tupled, InvitesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, author.?, receiver, message, createdon.?, tournamentId, eventsId, guildsId, teamsId, usersId).shaped.<>({r=>import r._; _1.map(_=> InvitesRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6, _7, _8, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column author DBType(INT) */
    val author: Column[Int] = column[Int]("author")
    /** Database column receiver DBType(INT), Default(None) */
    val receiver: Column[Option[Int]] = column[Option[Int]]("receiver", O.Default(None))
    /** Database column message DBType(LONGTEXT), Length(2147483647,true), Default(None) */
    val message: Column[Option[String]] = column[Option[String]]("message", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column createdOn DBType(INT) */
    val createdon: Column[Int] = column[Int]("createdOn")
    /** Database column tournament_id DBType(INT), Default(None) */
    val tournamentId: Column[Option[Int]] = column[Option[Int]]("tournament_id", O.Default(None))
    /** Database column events_id DBType(INT), Default(None) */
    val eventsId: Column[Option[Int]] = column[Option[Int]]("events_id", O.Default(None))
    /** Database column guilds_id DBType(INT), Default(None) */
    val guildsId: Column[Option[Int]] = column[Option[Int]]("guilds_id", O.Default(None))
    /** Database column teams_id DBType(INT), Default(None) */
    val teamsId: Column[Option[Int]] = column[Option[Int]]("teams_id", O.Default(None))
    /** Database column users_id DBType(INT), Default(None) */
    val usersId: Column[Option[Int]] = column[Option[Int]]("users_id", O.Default(None))

    /** Foreign key referencing Events (database name invites_ibfk_1) */
    lazy val eventsFk = foreignKey("invites_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Guilds (database name invites_ibfk_2) */
    lazy val guildsFk = foreignKey("invites_ibfk_2", guildsId, Guilds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Teams (database name invites_ibfk_3) */
    lazy val teamsFk = foreignKey("invites_ibfk_3", teamsId, Teams)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tournament (database name invites_ibfk_4) */
    lazy val tournamentFk = foreignKey("invites_ibfk_4", tournamentId, Tournament)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name invites_ibfk_5) */
    lazy val usersFk = foreignKey("invites_ibfk_5", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Invites */
  lazy val Invites = new TableQuery(tag => new Invites(tag))

  /** Entity class storing rows of table Nonactiveuseridentity
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param userid Database column userId DBType(INT)
   *  @param useridentifier Database column userIdentifier DBType(VARCHAR), Length(50,true)
   *  @param providerid Database column providerId DBType(VARCHAR), Length(45,true)
   *  @param email Database column email DBType(VARCHAR), Length(45,true), Default(None)
   *  @param password Database column password DBType(VARCHAR), Length(100,true), Default(None)
   *  @param firstname Database column firstName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param lastname Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
  case class NonactiveuseridentityRow(id: Int, userid: Int, useridentifier: String, providerid: String, email: Option[String] = None, password: Option[String] = None, firstname: Option[String] = None, lastname: Option[String] = None)
  /** GetResult implicit for fetching NonactiveuseridentityRow objects using plain SQL queries */
  implicit def GetResultNonactiveuseridentityRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[NonactiveuseridentityRow] = GR{
    prs => import prs._
    NonactiveuseridentityRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table nonactiveuseridentity. Objects of this class serve as prototypes for rows in queries. */
  class Nonactiveuseridentity(_tableTag: Tag) extends Table[NonactiveuseridentityRow](_tableTag, "nonactiveuseridentity") {
    def * = (id, userid, useridentifier, providerid, email, password, firstname, lastname) <> (NonactiveuseridentityRow.tupled, NonactiveuseridentityRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userid.?, useridentifier.?, providerid.?, email, password, firstname, lastname).shaped.<>({r=>import r._; _1.map(_=> NonactiveuseridentityRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column userId DBType(INT) */
    val userid: Column[Int] = column[Int]("userId")
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
    lazy val nonactiveusersFk = foreignKey("nonactiveuseridentity_ibfk_1", userid, Nonactiveusers)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Nonactiveuseridentity */
  lazy val Nonactiveuseridentity = new TableQuery(tag => new Nonactiveuseridentity(tag))

  /** Entity class storing rows of table Nonactiveusers
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param email Database column email DBType(VARCHAR), Length(50,true)
   *  @param createddate Database column createdDate DBType(DATETIME)
   *  @param lastlogin Database column lastLogin DBType(DATETIME), Default(None)
   *  @param firstname Database column firstName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param lastname Database column lastName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param globalhandle Database column globalHandle DBType(VARCHAR), Length(45,true), Default(None)
   *  @param role Database column role DBType(VARCHAR), Length(11,true) */
  case class NonactiveusersRow(id: Int, email: String, createddate: java.sql.Timestamp, lastlogin: Option[java.sql.Timestamp] = None, firstname: Option[String] = None, lastname: Option[String] = None, globalhandle: Option[String] = None, role: String)
  /** GetResult implicit for fetching NonactiveusersRow objects using plain SQL queries */
  implicit def GetResultNonactiveusersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Option[java.sql.Timestamp]], e4: GR[Option[String]]): GR[NonactiveusersRow] = GR{
    prs => import prs._
    NonactiveusersRow.tupled((<<[Int], <<[String], <<[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<[String]))
  }
  /** Table description of table nonactiveusers. Objects of this class serve as prototypes for rows in queries. */
  class Nonactiveusers(_tableTag: Tag) extends Table[NonactiveusersRow](_tableTag, "nonactiveusers") {
    def * = (id, email, createddate, lastlogin, firstname, lastname, globalhandle, role) <> (NonactiveusersRow.tupled, NonactiveusersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, email.?, createddate.?, lastlogin, firstname, lastname, globalhandle, role.?).shaped.<>({r=>import r._; _1.map(_=> NonactiveusersRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column email DBType(VARCHAR), Length(50,true) */
    val email: Column[String] = column[String]("email", O.Length(50,varying=true))
    /** Database column createdDate DBType(DATETIME) */
    val createddate: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("createdDate")
    /** Database column lastLogin DBType(DATETIME), Default(None) */
    val lastlogin: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastLogin", O.Default(None))
    /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
    val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
    /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
    val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))
    /** Database column globalHandle DBType(VARCHAR), Length(45,true), Default(None) */
    val globalhandle: Column[Option[String]] = column[Option[String]]("globalHandle", O.Length(45,varying=true), O.Default(None))
    /** Database column role DBType(VARCHAR), Length(11,true) */
    val role: Column[String] = column[String]("role", O.Length(11,varying=true))
  }
  /** Collection-like TableQuery object for table Nonactiveusers */
  lazy val Nonactiveusers = new TableQuery(tag => new Nonactiveusers(tag))

  /** Entity class storing rows of table Passwordtokens
   *  @param id Database column id DBType(INT), PrimaryKey
   *  @param token Database column token DBType(VARCHAR), Length(70,true) */
  case class PasswordtokensRow(id: Int, token: String)
  /** GetResult implicit for fetching PasswordtokensRow objects using plain SQL queries */
  implicit def GetResultPasswordtokensRow(implicit e0: GR[Int], e1: GR[String]): GR[PasswordtokensRow] = GR{
    prs => import prs._
    PasswordtokensRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table passwordtokens. Objects of this class serve as prototypes for rows in queries. */
  class Passwordtokens(_tableTag: Tag) extends Table[PasswordtokensRow](_tableTag, "passwordtokens") {
    def * = (id, token) <> (PasswordtokensRow.tupled, PasswordtokensRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, token.?).shaped.<>({r=>import r._; _1.map(_=> PasswordtokensRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column token DBType(VARCHAR), Length(70,true) */
    val token: Column[String] = column[String]("token", O.Length(70,varying=true))

    /** Uniqueness Index over (token) (database name unique_token) */
    val index1 = index("unique_token", token, unique=true)
  }
  /** Collection-like TableQuery object for table Passwordtokens */
  lazy val Passwordtokens = new TableQuery(tag => new Passwordtokens(tag))

  /** Entity class storing rows of table Teams
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param name Database column name DBType(VARCHAR), Length(45,true)
   *  @param jointype Database column joinType DBType(VARCHAR), Length(45,true)
   *  @param tournamentId Database column tournament_id DBType(INT)
   *  @param createddate Database column createdDate DBType(INT), Default(None)
   *  @param ispresent Database column isPresent DBType(SMALLINT), Default(0)
   *  @param guildonly Database column guildOnly DBType(SMALLINT), Default(0)
   *  @param guildid Database column guildId DBType(INT), Default(None) */
  case class TeamsRow(id: Int, name: String, jointype: String, tournamentId: Int, createddate: Option[Int] = None, ispresent: Short = 0, guildonly: Short = 0, guildid: Option[Int] = None)
  /** GetResult implicit for fetching TeamsRow objects using plain SQL queries */
  implicit def GetResultTeamsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]], e3: GR[Short]): GR[TeamsRow] = GR{
    prs => import prs._
    TeamsRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<?[Int], <<[Short], <<[Short], <<?[Int]))
  }
  /** Table description of table teams. Objects of this class serve as prototypes for rows in queries. */
  class Teams(_tableTag: Tag) extends Table[TeamsRow](_tableTag, "teams") {
    def * = (id, name, jointype, tournamentId, createddate, ispresent, guildonly, guildid) <> (TeamsRow.tupled, TeamsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, jointype.?, tournamentId.?, createddate, ispresent.?, guildonly.?, guildid).shaped.<>({r=>import r._; _1.map(_=> TeamsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name DBType(VARCHAR), Length(45,true) */
    val name: Column[String] = column[String]("name", O.Length(45,varying=true))
    /** Database column joinType DBType(VARCHAR), Length(45,true) */
    val jointype: Column[String] = column[String]("joinType", O.Length(45,varying=true))
    /** Database column tournament_id DBType(INT) */
    val tournamentId: Column[Int] = column[Int]("tournament_id")
    /** Database column createdDate DBType(INT), Default(None) */
    val createddate: Column[Option[Int]] = column[Option[Int]]("createdDate", O.Default(None))
    /** Database column isPresent DBType(SMALLINT), Default(0) */
    val ispresent: Column[Short] = column[Short]("isPresent", O.Default(0))
    /** Database column guildOnly DBType(SMALLINT), Default(0) */
    val guildonly: Column[Short] = column[Short]("guildOnly", O.Default(0))
    /** Database column guildId DBType(INT), Default(None) */
    val guildid: Column[Option[Int]] = column[Option[Int]]("guildId", O.Default(None))

    /** Foreign key referencing Tournament (database name teams_ibfk_1) */
    lazy val tournamentFk = foreignKey("teams_ibfk_1", tournamentId, Tournament)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Teams */
  lazy val Teams = new TableQuery(tag => new Teams(tag))

  /** Entity class storing rows of table TeamsUsers
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param teamsId Database column teams_id DBType(INT)
   *  @param usersId Database column users_id DBType(INT)
   *  @param iscaptain Database column isCaptain DBType(BIT) */
  case class TeamsUsersRow(id: Int, teamsId: Int, usersId: Int, iscaptain: Boolean)
  /** GetResult implicit for fetching TeamsUsersRow objects using plain SQL queries */
  implicit def GetResultTeamsUsersRow(implicit e0: GR[Int], e1: GR[Boolean]): GR[TeamsUsersRow] = GR{
    prs => import prs._
    TeamsUsersRow.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean]))
  }
  /** Table description of table teams_users. Objects of this class serve as prototypes for rows in queries. */
  class TeamsUsers(_tableTag: Tag) extends Table[TeamsUsersRow](_tableTag, "teams_users") {
    def * = (id, teamsId, usersId, iscaptain) <> (TeamsUsersRow.tupled, TeamsUsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, teamsId.?, usersId.?, iscaptain.?).shaped.<>({r=>import r._; _1.map(_=> TeamsUsersRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

  /** Entity class storing rows of table Tokens
   *  @param id Database column id DBType(INT), PrimaryKey
   *  @param token Database column token DBType(VARCHAR), Length(100,true)
   *  @param issuedon Database column issuedOn DBType(INT), Default(None) */
  case class TokensRow(id: Int, token: String, issuedon: Option[Int] = None)
  /** GetResult implicit for fetching TokensRow objects using plain SQL queries */
  implicit def GetResultTokensRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]]): GR[TokensRow] = GR{
    prs => import prs._
    TokensRow.tupled((<<[Int], <<[String], <<?[Int]))
  }
  /** Table description of table tokens. Objects of this class serve as prototypes for rows in queries. */
  class Tokens(_tableTag: Tag) extends Table[TokensRow](_tableTag, "tokens") {
    def * = (id, token, issuedon) <> (TokensRow.tupled, TokensRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, token.?, issuedon).shaped.<>({r=>import r._; _1.map(_=> TokensRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column token DBType(VARCHAR), Length(100,true) */
    val token: Column[String] = column[String]("token", O.Length(100,varying=true))
    /** Database column issuedOn DBType(INT), Default(None) */
    val issuedon: Column[Option[Int]] = column[Option[Int]]("issuedOn", O.Default(None))

    /** Foreign key referencing Users (database name user_token_id) */
    lazy val usersFk = foreignKey("user_token_id", id, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)

    /** Uniqueness Index over (token) (database name token_UNIQUE) */
    val index1 = index("token_UNIQUE", token, unique=true)
  }
  /** Collection-like TableQuery object for table Tokens */
  lazy val Tokens = new TableQuery(tag => new Tokens(tag))

  /** Entity class storing rows of table Tournament
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param tournamenttypesId Database column tournamenttypes_id DBType(INT)
   *  @param registrationtype Database column registrationType DBType(VARCHAR), Length(15,true)
   *  @param gamesId Database column games_id DBType(INT)
   *  @param eventsId Database column events_id DBType(INT) */
  case class TournamentRow(id: Int, tournamenttypesId: Int, registrationtype: String, gamesId: Int, eventsId: Int)
  /** GetResult implicit for fetching TournamentRow objects using plain SQL queries */
  implicit def GetResultTournamentRow(implicit e0: GR[Int], e1: GR[String]): GR[TournamentRow] = GR{
    prs => import prs._
    TournamentRow.tupled((<<[Int], <<[Int], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table tournament. Objects of this class serve as prototypes for rows in queries. */
  class Tournament(_tableTag: Tag) extends Table[TournamentRow](_tableTag, "tournament") {
    def * = (id, tournamenttypesId, registrationtype, gamesId, eventsId) <> (TournamentRow.tupled, TournamentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, tournamenttypesId.?, registrationtype.?, gamesId.?, eventsId.?).shaped.<>({r=>import r._; _1.map(_=> TournamentRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

    /** Foreign key referencing Events (database name tournament_ibfk_3) */
    lazy val eventsFk = foreignKey("tournament_ibfk_3", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Games (database name tournament_ibfk_2) */
    lazy val gamesFk = foreignKey("tournament_ibfk_2", gamesId, Games)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tournamenttypes (database name tournament_ibfk_1) */
    lazy val tournamenttypesFk = foreignKey("tournament_ibfk_1", tournamenttypesId, Tournamenttypes)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Tournament */
  lazy val Tournament = new TableQuery(tag => new Tournament(tag))

  /** Entity class storing rows of table Tournamentdetails
   *  @param tournamentId Database column tournament_id DBType(INT), PrimaryKey
   *  @param name Database column name DBType(VARCHAR), Length(45,true), Default(None)
   *  @param gameplayed Database column gamePlayed DBType(VARCHAR), Length(45,true), Default(None)
   *  @param description Database column description DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param rules Database column rules DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param prizes Database column prizes DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param streams Database column streams DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param servers Database column servers DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param timestart Database column timeStart DBType(INT), Default(None)
   *  @param timeend Database column timeEnd DBType(INT), Default(None)
   *  @param tournamentdetailscol Database column tournamentdetailscol DBType(VARCHAR), Length(45,true), Default(None)
   *  @param location Database column location DBType(LONGTEXT), Length(2147483647,true), Default(None)
   *  @param locationsub Database column locationsub DBType(VARCHAR), Length(255,true), Default(None)
   *  @param teammaxsize Database column teamMaxSize DBType(INT), Default(0)
   *  @param teamminsize Database column teamMinSize DBType(INT), Default(None)
   *  @param playerminsize Database column playerMinSize DBType(INT), Default(0)
   *  @param playermaxsize Database column playerMaxSize DBType(INT), Default(0) */
  case class TournamentdetailsRow(tournamentId: Int, name: Option[String] = None, gameplayed: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, timestart: Option[Int] = None, timeend: Option[Int] = None, tournamentdetailscol: Option[String] = None, location: Option[String] = None, locationsub: Option[String] = None, teammaxsize: Int = 0, teamminsize: Option[Int] = None, playerminsize: Int = 0, playermaxsize: Int = 0)
  /** GetResult implicit for fetching TournamentdetailsRow objects using plain SQL queries */
  implicit def GetResultTournamentdetailsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[TournamentdetailsRow] = GR{
    prs => import prs._
    TournamentdetailsRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Int], <<?[Int], <<?[String], <<?[String], <<?[String], <<[Int], <<?[Int], <<[Int], <<[Int]))
  }
  /** Table description of table tournamentdetails. Objects of this class serve as prototypes for rows in queries. */
  class Tournamentdetails(_tableTag: Tag) extends Table[TournamentdetailsRow](_tableTag, "tournamentdetails") {
    def * = (tournamentId, name, gameplayed, description, rules, prizes, streams, servers, timestart, timeend, tournamentdetailscol, location, locationsub, teammaxsize, teamminsize, playerminsize, playermaxsize) <> (TournamentdetailsRow.tupled, TournamentdetailsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (tournamentId.?, name, gameplayed, description, rules, prizes, streams, servers, timestart, timeend, tournamentdetailscol, location, locationsub, teammaxsize.?, teamminsize, playerminsize.?, playermaxsize.?).shaped.<>({r=>import r._; _1.map(_=> TournamentdetailsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14.get, _15, _16.get, _17.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column tournament_id DBType(INT), PrimaryKey */
    val tournamentId: Column[Int] = column[Int]("tournament_id", O.PrimaryKey)
    /** Database column name DBType(VARCHAR), Length(45,true), Default(None) */
    val name: Column[Option[String]] = column[Option[String]]("name", O.Length(45,varying=true), O.Default(None))
    /** Database column gamePlayed DBType(VARCHAR), Length(45,true), Default(None) */
    val gameplayed: Column[Option[String]] = column[Option[String]]("gamePlayed", O.Length(45,varying=true), O.Default(None))
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
    val timestart: Column[Option[Int]] = column[Option[Int]]("timeStart", O.Default(None))
    /** Database column timeEnd DBType(INT), Default(None) */
    val timeend: Column[Option[Int]] = column[Option[Int]]("timeEnd", O.Default(None))
    /** Database column tournamentdetailscol DBType(VARCHAR), Length(45,true), Default(None) */
    val tournamentdetailscol: Column[Option[String]] = column[Option[String]]("tournamentdetailscol", O.Length(45,varying=true), O.Default(None))
    /** Database column location DBType(LONGTEXT), Length(2147483647,true), Default(None) */
    val location: Column[Option[String]] = column[Option[String]]("location", O.Length(2147483647,varying=true), O.Default(None))
    /** Database column locationsub DBType(VARCHAR), Length(255,true), Default(None) */
    val locationsub: Column[Option[String]] = column[Option[String]]("locationsub", O.Length(255,varying=true), O.Default(None))
    /** Database column teamMaxSize DBType(INT), Default(0) */
    val teammaxsize: Column[Int] = column[Int]("teamMaxSize", O.Default(0))
    /** Database column teamMinSize DBType(INT), Default(None) */
    val teamminsize: Column[Option[Int]] = column[Option[Int]]("teamMinSize", O.Default(None))
    /** Database column playerMinSize DBType(INT), Default(0) */
    val playerminsize: Column[Int] = column[Int]("playerMinSize", O.Default(0))
    /** Database column playerMaxSize DBType(INT), Default(0) */
    val playermaxsize: Column[Int] = column[Int]("playerMaxSize", O.Default(0))

    /** Foreign key referencing Tournament (database name tournamentdetails_ibfk_1) */
    lazy val tournamentFk = foreignKey("tournamentdetails_ibfk_1", tournamentId, Tournament)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Tournamentdetails */
  lazy val Tournamentdetails = new TableQuery(tag => new Tournamentdetails(tag))

  /** Entity class storing rows of table Tournamenttypes
   *  @param id Database column Id DBType(INT), AutoInc, PrimaryKey
   *  @param name Database column name DBType(VARCHAR), Length(50,true)
   *  @param teamplay Database column teamPlay DBType(TINYINT) */
  case class TournamenttypesRow(id: Int, name: String, teamplay: Byte)
  /** GetResult implicit for fetching TournamenttypesRow objects using plain SQL queries */
  implicit def GetResultTournamenttypesRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Byte]): GR[TournamenttypesRow] = GR{
    prs => import prs._
    TournamenttypesRow.tupled((<<[Int], <<[String], <<[Byte]))
  }
  /** Table description of table tournamenttypes. Objects of this class serve as prototypes for rows in queries. */
  class Tournamenttypes(_tableTag: Tag) extends Table[TournamenttypesRow](_tableTag, "tournamenttypes") {
    def * = (id, name, teamplay) <> (TournamenttypesRow.tupled, TournamenttypesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, teamplay.?).shaped.<>({r=>import r._; _1.map(_=> TournamenttypesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column Id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("Id", O.AutoInc, O.PrimaryKey)
    /** Database column name DBType(VARCHAR), Length(50,true) */
    val name: Column[String] = column[String]("name", O.Length(50,varying=true))
    /** Database column teamPlay DBType(TINYINT) */
    val teamplay: Column[Byte] = column[Byte]("teamPlay")
  }
  /** Collection-like TableQuery object for table Tournamenttypes */
  lazy val Tournamenttypes = new TableQuery(tag => new Tournamenttypes(tag))

  /** Entity class storing rows of table UserEvents
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param usersId Database column users_id DBType(INT)
   *  @param eventsId Database column events_id DBType(INT)
   *  @param ispresent Database column isPresent DBType(TINYINT), Default(0)
   *  @param isadmin Database column isAdmin DBType(TINYINT), Default(0)
   *  @param ismoderator Database column isModerator DBType(TINYINT), Default(0)
   *  @param haspaid Database column hasPaid DBType(TINYINT), Default(0)
   *  @param receiptid Database column receiptId DBType(VARCHAR), Length(45,true), Default(None)
   *  @param customerid Database column customerId DBType(VARCHAR), Length(45,true), Default(None) */
  case class UserEventsRow(id: Int, usersId: Int, eventsId: Int, ispresent: Byte = 0, isadmin: Byte = 0, ismoderator: Byte = 0, haspaid: Byte = 0, receiptid: Option[String] = None, customerid: Option[String] = None)
  /** GetResult implicit for fetching UserEventsRow objects using plain SQL queries */
  implicit def GetResultUserEventsRow(implicit e0: GR[Int], e1: GR[Byte], e2: GR[Option[String]]): GR[UserEventsRow] = GR{
    prs => import prs._
    UserEventsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Byte], <<[Byte], <<[Byte], <<[Byte], <<?[String], <<?[String]))
  }
  /** Table description of table user_events. Objects of this class serve as prototypes for rows in queries. */
  class UserEvents(_tableTag: Tag) extends Table[UserEventsRow](_tableTag, "user_events") {
    def * = (id, usersId, eventsId, ispresent, isadmin, ismoderator, haspaid, receiptid, customerid) <> (UserEventsRow.tupled, UserEventsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, usersId.?, eventsId.?, ispresent.?, isadmin.?, ismoderator.?, haspaid.?, receiptid, customerid).shaped.<>({r=>import r._; _1.map(_=> UserEventsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column users_id DBType(INT) */
    val usersId: Column[Int] = column[Int]("users_id")
    /** Database column events_id DBType(INT) */
    val eventsId: Column[Int] = column[Int]("events_id")
    /** Database column isPresent DBType(TINYINT), Default(0) */
    val ispresent: Column[Byte] = column[Byte]("isPresent", O.Default(0))
    /** Database column isAdmin DBType(TINYINT), Default(0) */
    val isadmin: Column[Byte] = column[Byte]("isAdmin", O.Default(0))
    /** Database column isModerator DBType(TINYINT), Default(0) */
    val ismoderator: Column[Byte] = column[Byte]("isModerator", O.Default(0))
    /** Database column hasPaid DBType(TINYINT), Default(0) */
    val haspaid: Column[Byte] = column[Byte]("hasPaid", O.Default(0))
    /** Database column receiptId DBType(VARCHAR), Length(45,true), Default(None) */
    val receiptid: Column[Option[String]] = column[Option[String]]("receiptId", O.Length(45,varying=true), O.Default(None))
    /** Database column customerId DBType(VARCHAR), Length(45,true), Default(None) */
    val customerid: Column[Option[String]] = column[Option[String]]("customerId", O.Length(45,varying=true), O.Default(None))

    /** Foreign key referencing Events (database name user_events_ibfk_1) */
    lazy val eventsFk = foreignKey("user_events_ibfk_1", eventsId, Events)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Users (database name user_events_ibfk_2) */
    lazy val usersFk = foreignKey("user_events_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserEvents */
  lazy val UserEvents = new TableQuery(tag => new UserEvents(tag))

  /** Entity class storing rows of table Useridentity
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param usersId Database column users_id DBType(INT)
   *  @param useridentifier Database column userIdentifier DBType(VARCHAR), Length(50,true)
   *  @param providerid Database column providerId DBType(VARCHAR), Length(45,true)
   *  @param email Database column email DBType(VARCHAR), Length(45,true), Default(None)
   *  @param password Database column password DBType(VARCHAR), Length(100,true), Default(None)
   *  @param firstname Database column firstName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param lastname Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
  case class UseridentityRow(id: Int, usersId: Int, useridentifier: String, providerid: String, email: Option[String] = None, password: Option[String] = None, firstname: Option[String] = None, lastname: Option[String] = None)
  /** GetResult implicit for fetching UseridentityRow objects using plain SQL queries */
  implicit def GetResultUseridentityRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[UseridentityRow] = GR{
    prs => import prs._
    UseridentityRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table useridentity. Objects of this class serve as prototypes for rows in queries. */
  class Useridentity(_tableTag: Tag) extends Table[UseridentityRow](_tableTag, "useridentity") {
    def * = (id, usersId, useridentifier, providerid, email, password, firstname, lastname) <> (UseridentityRow.tupled, UseridentityRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, usersId.?, useridentifier.?, providerid.?, email, password, firstname, lastname).shaped.<>({r=>import r._; _1.map(_=> UseridentityRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

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

    /** Foreign key referencing Users (database name useridentity_ibfk_1) */
    lazy val usersFk = foreignKey("useridentity_ibfk_1", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Useridentity */
  lazy val Useridentity = new TableQuery(tag => new Useridentity(tag))

  /** Entity class storing rows of table Userplatformprofile
   *  @param usersId Database column users_id DBType(INT)
   *  @param platform Database column platform DBType(VARCHAR), Length(45,true)
   *  @param identifier Database column identifier DBType(VARCHAR), Length(45,true)
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey */
  case class UserplatformprofileRow(usersId: Int, platform: String, identifier: String, id: Int)
  /** GetResult implicit for fetching UserplatformprofileRow objects using plain SQL queries */
  implicit def GetResultUserplatformprofileRow(implicit e0: GR[Int], e1: GR[String]): GR[UserplatformprofileRow] = GR{
    prs => import prs._
    UserplatformprofileRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table userplatformprofile. Objects of this class serve as prototypes for rows in queries. */
  class Userplatformprofile(_tableTag: Tag) extends Table[UserplatformprofileRow](_tableTag, "userplatformprofile") {
    def * = (usersId, platform, identifier, id) <> (UserplatformprofileRow.tupled, UserplatformprofileRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (usersId.?, platform.?, identifier.?, id.?).shaped.<>({r=>import r._; _1.map(_=> UserplatformprofileRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column users_id DBType(INT) */
    val usersId: Column[Int] = column[Int]("users_id")
    /** Database column platform DBType(VARCHAR), Length(45,true) */
    val platform: Column[String] = column[String]("platform", O.Length(45,varying=true))
    /** Database column identifier DBType(VARCHAR), Length(45,true) */
    val identifier: Column[String] = column[String]("identifier", O.Length(45,varying=true))
    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    /** Foreign key referencing Users (database name userplatformprofile_ibfk_1) */
    lazy val usersFk = foreignKey("userplatformprofile_ibfk_1", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Userplatformprofile */
  lazy val Userplatformprofile = new TableQuery(tag => new Userplatformprofile(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param email Database column email DBType(VARCHAR), Length(50,true)
   *  @param createddate Database column createdDate DBType(INT)
   *  @param lastlogin Database column lastLogin DBType(DATETIME), Default(None)
   *  @param firstname Database column firstName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param lastname Database column lastName DBType(VARCHAR), Length(45,true), Default(None)
   *  @param globalhandle Database column globalHandle DBType(VARCHAR), Length(45,true), Default(None)
   *  @param role Database column role DBType(VARCHAR), Length(11,true) */
  case class UsersRow(id: Int, email: String, createddate: Int, lastlogin: Option[java.sql.Timestamp] = None, firstname: Option[String] = None, lastname: Option[String] = None, globalhandle: Option[String] = None, role: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[String]]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[Int], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<[String]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "users") {
    def * = (id, email, createddate, lastlogin, firstname, lastname, globalhandle, role) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, email.?, createddate.?, lastlogin, firstname, lastname, globalhandle, role.?).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column email DBType(VARCHAR), Length(50,true) */
    val email: Column[String] = column[String]("email", O.Length(50,varying=true))
    /** Database column createdDate DBType(INT) */
    val createddate: Column[Int] = column[Int]("createdDate")
    /** Database column lastLogin DBType(DATETIME), Default(None) */
    val lastlogin: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("lastLogin", O.Default(None))
    /** Database column firstName DBType(VARCHAR), Length(45,true), Default(None) */
    val firstname: Column[Option[String]] = column[Option[String]]("firstName", O.Length(45,varying=true), O.Default(None))
    /** Database column lastName DBType(VARCHAR), Length(45,true), Default(None) */
    val lastname: Column[Option[String]] = column[Option[String]]("lastName", O.Length(45,varying=true), O.Default(None))
    /** Database column globalHandle DBType(VARCHAR), Length(45,true), Default(None) */
    val globalhandle: Column[Option[String]] = column[Option[String]]("globalHandle", O.Length(45,varying=true), O.Default(None))
    /** Database column role DBType(VARCHAR), Length(11,true) */
    val role: Column[String] = column[String]("role", O.Length(11,varying=true))
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table UserTournaments
   *  @param id Database column id DBType(INT), AutoInc, PrimaryKey
   *  @param usersId Database column users_id DBType(INT)
   *  @param tournamentId Database column tournament_id DBType(INT)
   *  @param ispresent Database column isPresent DBType(TINYINT), Default(0)
   *  @param isadmin Database column isAdmin DBType(TINYINT), Default(0)
   *  @param ismoderator Database column isModerator DBType(TINYINT), Default(0) */
  case class UserTournamentsRow(id: Int, usersId: Int, tournamentId: Int, ispresent: Byte = 0, isadmin: Byte = 0, ismoderator: Byte = 0)
  /** GetResult implicit for fetching UserTournamentsRow objects using plain SQL queries */
  implicit def GetResultUserTournamentsRow(implicit e0: GR[Int], e1: GR[Byte]): GR[UserTournamentsRow] = GR{
    prs => import prs._
    UserTournamentsRow.tupled((<<[Int], <<[Int], <<[Int], <<[Byte], <<[Byte], <<[Byte]))
  }
  /** Table description of table user_tournaments. Objects of this class serve as prototypes for rows in queries. */
  class UserTournaments(_tableTag: Tag) extends Table[UserTournamentsRow](_tableTag, "user_tournaments") {
    def * = (id, usersId, tournamentId, ispresent, isadmin, ismoderator) <> (UserTournamentsRow.tupled, UserTournamentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, usersId.?, tournamentId.?, ispresent.?, isadmin.?, ismoderator.?).shaped.<>({r=>import r._; _1.map(_=> UserTournamentsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id DBType(INT), AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column users_id DBType(INT) */
    val usersId: Column[Int] = column[Int]("users_id")
    /** Database column tournament_id DBType(INT) */
    val tournamentId: Column[Int] = column[Int]("tournament_id")
    /** Database column isPresent DBType(TINYINT), Default(0) */
    val ispresent: Column[Byte] = column[Byte]("isPresent", O.Default(0))
    /** Database column isAdmin DBType(TINYINT), Default(0) */
    val isadmin: Column[Byte] = column[Byte]("isAdmin", O.Default(0))
    /** Database column isModerator DBType(TINYINT), Default(0) */
    val ismoderator: Column[Byte] = column[Byte]("isModerator", O.Default(0))

    /** Foreign key referencing Tournament (database name user_tournaments_ibfk_1) */
    lazy val tournamentFk = foreignKey("user_tournaments_ibfk_1", tournamentId, Tournament)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Users (database name user_tournaments_ibfk_2) */
    lazy val usersFk = foreignKey("user_tournaments_ibfk_2", usersId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserTournaments */
  lazy val UserTournaments = new TableQuery(tag => new UserTournaments(tag))
}
