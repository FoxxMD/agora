package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

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

class Tournament(val bracketType: BracketType.Value, val registrationType: JoinType.Value, val game: Game,
                  val event: Event, val users: Set[TournamentUser], val teams: Set[TournamentTeam]) {

}

class TournamentDetails(var tournament: Tournament, val name: Option[String], val gamePlayed: Option[String], val description: Option[String], val rules: Option[String], val prizes: Option[String],
                        val streams: Option[String], val servers: Option[String], val timeStart: DateTime, val timeEnd: DateTime)
{

}