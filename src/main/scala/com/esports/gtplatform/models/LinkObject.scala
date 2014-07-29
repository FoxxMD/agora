package models

/**
 * Created by Matthew on 6/30/2014.
 */

/* Link objects exist to provide extra information about a relationship between two domain objects. Currently it's only
 * simple things like isPresent for Teams at a Tournament, or Users at an Event -- but I'm sure eventually the relationship will
  * grow more complex and require more information.
  *
  * These really should be case classes but ATM they are easier to handle as mutable datas.*/

class TeamUser(val team: Team, val user: User, var isCaptain: Boolean) {

}
class EventUser(val event: Event, val user: User, val isPresent: Boolean){

}
class TournamentTeam(val tournament: Tournament, val team: Team, val isPresent: Boolean){

}

class TournamentUser(val tournament: Tournament,
                     val user: User,
                     val isPresent: Boolean){
}