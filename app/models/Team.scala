package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */
trait TeamT {
  var name:String
  val createdDate: DateTime
  var game: Game
  var teamPlayers: Set[TeamUser]

  def setCaptain(newCap:User):Boolean
  def getCaptain:User
  def getPlayers:Set[User]
  def getTournaments: List[Tournament]
}

class Team(
            var name: String,
            val createdDate: DateTime,
            var game: Game,
            var teamPlayers: Set[TeamUser])
extends TeamT {

  def setCaptain(newCap:User) = {
    val tu = teamPlayers.find(u => u.user.equals(newCap)).headOption
   if(tu.isDefined)
   {
     tu.get.isCaptain = true
     true
   }
    else
     false
  }
  def getCaptain = teamPlayers.find(u => u.isCaptain).get.user
  def getPlayers = teamPlayers.map(u => u.user)
  def getTournaments = ???
}
