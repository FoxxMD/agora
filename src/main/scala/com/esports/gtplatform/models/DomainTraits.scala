package com.esports.gtplatform.models

import models.{Tournament, Team, User}

/**
 * Created by Matthew on 8/26/2014.
 */
trait GroupT[T] {
  def addUser(u: User): T
  def removeUser(u: User): T
}
trait MeetingT[T]{
  //p is a string representing the role of the user IE 'admin' or 'moderator' or 'user'
  def setUserPrivilege(u: User, p: String = "user"): T
  def getModerators: List[User]
  def getAdmins: List[User]
  def getPresentUsers: List[User]
}
trait TournamentT {
  def getPresentTeams: List[Team]
  def addTeam(t: Team): Tournament
  def removeTeam(t: Team): Tournament
}
