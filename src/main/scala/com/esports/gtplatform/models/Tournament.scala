package models

import com.esports.gtplatform.models.{Team, Requestable, Inviteable}
import monocle._
import monocle.syntax._
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
case class Tournament(tournamentType: TournamentType = TournamentType(),
                      registrationType: JoinType.Value = JoinType.Public,
                      game: Game = Game(),
                      event: Event = Event(),
                      details: Option[TournamentDetails] = None,
                      users: Set[TournamentUser] = Set(),
                      teams: Set[Team] = Set(),
                      id: Int = 0) extends Inviteable with Requestable {

    private[this] val DetailsLens: SimpleLens[Tournament, Option[TournamentDetails]] = SimpleLens[Tournament](_.details)((e, newDetails) => e.copy(details = newDetails))

    def setDetails(e: TournamentDetails): Tournament = this applyLens DetailsLens set Option(e)
}

case class TournamentDetails(tournament: Tournament = Tournament(),
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
                                teamMinSize: Int = 0,
                                teamMaxSize: Int = 0,
                                playerMinSize: Int = 0,
                                playerMaxSize: Int = 0) {

}
