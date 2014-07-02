package models

/**
 * Created by Matthew on 6/30/2014.
 */
class TeamUser(val team: Team, val user: User, val isCaptain: Boolean) {

}
class EventUser(val event: Event, val user: User, val isPresent: Boolean){

}
class TournamentTeam(val tournament: Tournament, val team: Team, val isPresent: Boolean){

}

class TournamentUser(val tournament: Tournament, val user: User, val isPresent: Boolean){

}