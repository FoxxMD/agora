package models

import com.esports.gtplatform.models._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
case class Tournament(tournamentTypeId: Int, registrationType: String = "Public", gameId: Int, eventId: Int, id: Option[Int] = None) {

    var game: Game = null
    var event: Event = null
    var details: Option[TournamentDetail] = None
    var users: Set[TournamentUser] = Set()
    var teams: Set[Team] = Set()

    //private[this] val DetailsLens: SimpleLens[Tournament, Option[TournamentDetail]] = SimpleLens[Tournament](_.details)((e, newDetails) => e.copy(details = newDetails))

    //def setDetails(e: TournamentDetail): Tournament = this applyLens DetailsLens set Option(e)
    //def isAdmin(u: User) = this.users.exists(x => x.userId.id == u.id && x.isAdmin)
    //def isModerator(u: User) = this.users.exists(x => x.userId.id == u.id && (x.isModerator || x.isAdmin))

    //needed for squeryl table initialization. See "Nullable columns are mapped with Option[] fields http://squeryl.org/schema-definition.html
    def this() = this(tournamentTypeId = 0, registrationType = "", gameId = 0, eventId = 0, id = Some(0))
}

case class TournamentDetail(tournamentId: Option[Int] = None,
                            name: Option[String] = None,
                            gamePlayed: Option[String] = None,
                            description: Option[String] = None,
                            location: Option[String] = None,
                            locationsub: Option[String] = None,
                            rules: Option[String] = None,
                            prizes: Option[String] = None,
                            streams: Option[String] = None,
                            servers: Option[String] = None,
                            timeStart: Option[DateTime] = None,
                            timeEnd: Option[DateTime] = None,
                            teamMinSize: Option[Int] = None,
                            teamMaxSize: Option[Int] = None,
                            playerMinSize: Option[Int] = None,
                            playerMaxSize: Option[Int] = None) {

    var tournament: Tournament = null

    def this() = this(tournamentId = Some(0), name = Some(""), gamePlayed = Some(""), location = Some(""), locationsub = Some(""), description = Some(""), rules = Some(""), prizes = Some(""), streams = Some(""), servers = Some(""), timeStart = Some(DateTime.now), timeEnd = Some(DateTime.now), teamMaxSize = Some(0), teamMinSize = Some(0), playerMaxSize = Some(0), playerMinSize = Some(0))
}
