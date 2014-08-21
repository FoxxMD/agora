package com.esports.gtplatform.models

import models.{Team, Event, Tournament, User}
import org.joda.time.DateTime

/**
 * Created by Matthew on 8/18/2014.
 */
trait Inviteable

trait Invitee

trait Requestable

/*abstract class InviteT[T <% Inviteable, U <% Invitee] extends AppliedActivityT[T, U] {
  val message: String
}*/
abstract class InviteT  {
  val Author: Inviteable
  val Receiver: Invitee
  val message: String
  val Time: DateTime
}


abstract class InviteOnBehalfT[T <% Inviteable, U <% Invitee, V <% Invitee] extends InviteT {
  val Mediator: V
}

abstract class RequestT[T <% Invitee, U <% Requestable] extends AppliedActivityT[T, U] {
  val message: String
}

case class TeamInvite(
                       Author: Team,
                       Receiver: User,
                       message: String = null,
                       Time: DateTime
                       ) extends InviteT//[Team, User]

case class TournamentUserInvite(
                                 Author: Tournament,
                                 Receiver: User,
                                 Mediator: User,
                                 message: String = null,
                                 Time: DateTime
                                 ) extends InviteOnBehalfT[Tournament, User, User]

case class TournamentTeamInvite(
                                 Author: Tournament,
                                 Receiver: Team,
                                 Mediator: Team,
                                 message: String = null,
                                 Time: DateTime
                                 ) extends InviteOnBehalfT[Tournament, Team, Team]

case class EventInvite(
                        Author: Event,
                        Receiver: User,
                        //Mediator: User,
                        message: String = null,
                        Time: DateTime
                        ) extends InviteT//[Event, User]

case class TeamRequest(
                        Author: User,
                        Receiver: Team,
                        message: String = null,
                        Time: DateTime
                        ) extends RequestT[User, Team]

case class EventRequest(
                         Author: User,
                         Receiver: Event,
                         message: String = null,
                         Time: DateTime
                         ) extends RequestT[User, Event]
case class TournamentTeamRequest(
                                  Author: Team,
                                  Receiver: Tournament,
                                  message: String = null,
                                  Time: DateTime
                                  ) extends RequestT[Team, Tournament]