package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
class Team(val id: Int, val name: String, val createdDate: DateTime, val games: Set[Game], val teamPlayers: Set[TeamUser]) {

}
