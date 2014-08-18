package models

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
                 teams: Set[TournamentTeam],
                 id: Int = 0) {
}

case class TournamentDetails(tournament: Tournament, name: Option[String], gamePlayed: Option[String], description: Option[String], rules: Option[String], prizes: Option[String],
                        streams: Option[String], servers: Option[String], timeStart: DateTime, timeEnd: DateTime)
{

}