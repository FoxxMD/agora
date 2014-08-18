package models

/**
 * Created by Matthew on 6/30/2014.
 */

/* Link objects exist to provide extra information about a relationship between two domain objects. Currently it's only
 * simple things like isPresent for Teams at a Tournament, or Users at an Event -- but I'm sure eventually the relationship will
  * grow more complex and require more information.
  *
  * These really should be case classes but ATM they are easier to handle as mutable datas.*/

case class TeamUser(team: Team, user: User, isCaptain: Boolean) {

}

case class EventUser(event: Event, user: User, isPresent: Boolean){

}
case class TournamentTeam(tournament: Tournament, team: Team, isPresent: Boolean){

}

case class TournamentUser(tournament: Tournament,
                     user: User,
                     isPresent: Boolean){
}