package models

import com.esports.gtplatform.business._
import com.esports.gtplatform.models._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
case class Tournament(tournamentTypeId: Int, registrationType: String = "Public", gameId: Int, eventId: Int, bracketId: Option[String] = None, id: Option[Int] = None, private var _users: Option[List[TournamentUser]] = None, private var _teams: Option[List[Team]] = None, private[this] var _brackets: Option[List[Bracket]] = None) extends DomainEntity[Tournament] {

    import com.esports.gtplatform.dao.Squreyl._
    import com.esports.gtplatform.dao.SquerylDao._

    private[this] val tourDetailRepo: TournamentDetailsRepo = new TournamentDetailRepository
    private[this] val eventRepo: EventRepo = new EventRepository
    private[this] val gameRepo: GameRepo = new GameRepository

    lazy val game: Game = gameRepo.get(gameId).get
    lazy val event: Event = eventRepo.get(eventId).get
    def details: Option[TournamentDetail] = tourDetailRepo.getByTournament(id.get)
    def users: List[TournamentUser] = if(this._users.isDefined) this._users.get else inTransaction {
        _users = Option(tournamentToTournamentUsers.left(this).associations.toList)
        _users.get
    }
    def teams: List[Team] = if (this._teams.isDefined) this._teams.get else inTransaction {
        _teams = Option(tournamentToTeams.left(this).toList)
        _teams.get
    }
    def brackets: List[Bracket] = this._brackets.getOrElse{
        inTransaction(tournamentToBrackets.left(this).toList)
    }


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
