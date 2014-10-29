package models

import com.esports.gtplatform.models.{Team, Requestable, Inviteable}
import monocle._
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
case class Tournament(id: Int = 0, tournamentTypeId: Int = TournamentType(), registrationType: String = "Public", gameId: Int = Game(), eventId: Int = Event()) extends Inviteable with Requestable {

    var game: Game = null
    var event: Event = null
    var details: Option[TournamentDetail] = None
    var users: Set[TournamentUser] = Set()
    var teams: Set[Team] = Set()

    private[this] val DetailsLens: SimpleLens[Tournament, Option[TournamentDetail]] = SimpleLens[Tournament](_.details)((e, newDetails) => e.copy(details = newDetails))

    def setDetails(e: TournamentDetail): Tournament = this applyLens DetailsLens set Option(e)
    def isAdmin(u: User) = this.users.exists(x => x.userId.id == u.id && x.isAdmin)
    def isModerator(u: User) = this.users.exists(x => x.userId.id == u.id && (x.isModerator || x.isAdmin))
}

case class TournamentDetail(tournamentId: Int = Tournament(),
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
                            teamMinSize: Option[Int] = 0,
                            teamMaxSize: Option[Int] = 0,
                            playerMinSize: Option[Int] = 0,
                            playerMaxSize: Option[Int] = 0) {

    var tournament: Tournament = null
}
