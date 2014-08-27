package com.esports.gtplatform.models

import models.{Game, Tournament, Team, User}

/**
 * Created by Matthew on 8/26/2014.
 */
trait GroupT[T] {
  def addUser(u: User): T
  def removeUser(u: User): T
}
trait MeetingT[T] extends GroupT[T]{
  //p is a string representing the role of the user IE 'admin' or 'moderator' or 'user'
  //def setUserPrivilege(u: User, p: String = "user"): T
  def getModerators: Set[User]
  def isModerator(u: User): Boolean
  def getAdmins: Set[User]
  def isAdmin(u: User): Boolean
  def getPresentUsers: Set[User]
  def isPresent(u: User): Boolean
}
trait TournamentT extends MeetingT[Tournament] {
  def getPresentTeams: List[Team]
  def addTeam(t: Team): Tournament
  def removeTeam(t: Team): Tournament
}
trait TeamT extends GroupT[Team] {
  def setCaptain(u: User): Team
  def getCaptain: User
  def addGame(g: Game): Team
  def removeGame(g: Game): Team
}

