package models

import com.esports.gtplatform.models.{Team, Requestable, Inviteable}
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
}

case class TournamentDetails(tournament: Tournament = Tournament(),
                             name: Option[String] = None,
                             gamePlayed: Option[String] = None,
                             description: Option[String] = None,
                             location: Option[String] = None,
                             rules: Option[String] = None,
                             prizes: Option[String] = None,
                             streams: Option[String] = None,
                             servers: Option[String] = None,
                             timeStart: Option[DateTime] = None,
                             timeEnd: Option[DateTime] = None,
                                teamMinSize: Int = 0,
                                teamMaxSize: Int = 0) {

}
