package models

import org.joda.time.DateTime

/**
 * Created by Matthew on 6/30/2014.
 */

/* Not sure if I will use this method. Can safely ignore for now.*/
trait TeamT {
  val name:String
  val createdDate: DateTime
  val game: Game
  val teamPlayers: Set[TeamUser]

  def setCaptain(newCap:User):Boolean
  def getCaptain:User
  def getPlayers:Set[User]
  def getTournaments: List[Tournament]
}

case class Team(
            name: String,
            createdDate: DateTime,
            games: List[Game],
            teamPlayers: List[TeamUser] = List(),
            id: Int = 0)
{

  def setCaptain(newCap:User) = {
    val tu = teamPlayers.find(u => u.user.equals(newCap)).headOption
   if(tu.isDefined)
   {
     //tu.get.isCaptain = true
     true
   }
    else
     false
  }
  def getCaptain = teamPlayers.find(u => u.isCaptain).get.user
  def getPlayers = teamPlayers.map(u => u.user)
  def getTournaments = ??? //You can use ??? to let Scala throw a NotImplementedException, this acts as a stub
}
