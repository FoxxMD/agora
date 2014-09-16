package models

import com.esports.gtplatform.models.{Team, Requestable, Inviteable}
import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
/* This will eventually be fleshed out into a full bracket system. Need to get the basics down first though!*/
object BracketType extends Enumeration {
  type BracketType = Value
  val Elimination, RoundRobin = Value

  def toString(j: BracketType) = j match {
    case Elimination => "Elim"
    case RoundRobin => "Round"
  }

  def fromString(j: String): BracketType = j match {
    case "Elim" => Elimination
    case "Round" => RoundRobin
  }
}

case class Tournament(bracketType: BracketType.Value,
                 registrationType: JoinType.Value,
                 game: Game,
                 event: Event,
                 details: TournamentDetails,
                 users: Set[TournamentUser],
                 teams: Set[Team],
                 id: Int = 0) extends Inviteable with Requestable {
}

case class TournamentDetails(tournament: Tournament, name: Option[String] = None, gamePlayed: Option[String] = None, description: Option[String] = None, rules: Option[String] = None, prizes: Option[String] = None,
                        streams: Option[String] = None, servers: Option[String] = None, timeStart: DateTime, timeEnd: DateTime)
{

}