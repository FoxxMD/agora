package models

import com.esports.gtplatform.models._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
case class Tournament(tournamentTypeId: Int, registrationType: String = "Public", gameId: Int, eventId: Int, id: Option[Int] = None) extends DomainEntity[Tournament] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    lazy val game: Game = inTransaction { games.lookup(this.gameId).get }
    lazy val event: Event = inTransaction { events.lookup(this.eventId).get}
    def details: Option[TournamentDetail] = inTransaction{ from(tournamentDetails)(t => where(t.tournamentId === id.get) select(t)) /*tournamentDetails.where(x => x.tournamentId.get === this.id.get).singleOption*/ }.headOption
    lazy val users: List[TournamentUser] = tournamentToTournamentUsers.left(this).associations.iterator.toList
    lazy val teams: List[Team] = tournamentToTeams.left(this).iterator.toList
    lazy val tournamentType = inTransaction {tournamentTypes.lookup(this.tournamentTypeId).get}


    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(tournamentTypeId = 0, registrationType = "", gameId = 0, eventId = 0, id = Some(0))
}

case class TournamentDetail(tournamentId: Option[Int] = None, name: Option[String] = None, gamePlayed: Option[String] = None, description: Option[String] = None, location: Option[String] = None, locationsub: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None, streams: Option[String] = None, servers: Option[String] = None, timeStart: Option[DateTime] = None, timeEnd: Option[DateTime] = None, teamMinSize: Option[Int] = None, teamMaxSize: Option[Int] = None, playerMinSize: Option[Int] = None, playerMaxSize: Option[Int] = None, id: Option[Int] = None) extends DomainEntity[TournamentDetail] {

    def this() = this(
        tournamentId = Some(0),
        name = Some(""),
        gamePlayed = Some(""),
        location = Some(""),
        locationsub = Some(""),
        description = Some(""),
        rules = Some(""),
        prizes = Some(""),
        streams = Some(""),
        servers = Some(""),
        timeStart = Some(DateTime.now),
        timeEnd = Some(DateTime.now),
        teamMaxSize = Some(0),
        teamMinSize = Some(0),
        playerMaxSize = Some(0),
        playerMinSize = Some(0),
        id = Some(0))
}
