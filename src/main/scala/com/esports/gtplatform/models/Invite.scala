package com.esports.gtplatform.models

import models.{Guild, Event, Tournament, User}
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

case class GuildInvite(
                       Author: Guild,
                       Receiver: User,
                       message: String = null,
                       Time: DateTime,
                       id: Int = 0) extends InviteT//[Team, User]

case class TournamentUserInvite(
                                 Author: Tournament,
                                 Receiver: User,
                                 Mediator: User,
                                 message: String = null,
                                 Time: DateTime
                                 ) extends InviteOnBehalfT[Tournament, User, User]

case class TournamentTeamInvite(
                                 Author: Tournament,
                                 Receiver: Guild,
                                 Mediator: Guild,
                                 message: String = null,
                                 Time: DateTime
                                 ) extends InviteOnBehalfT[Tournament, Guild, Guild]

case class EventInvite(
                        Author: Event,
                        Receiver: User,
                        //Mediator: User,
                        message: String = null,
                        Time: DateTime,
                        id: Int = 0) extends InviteT//[Event, User]

case class TeamRequest(
                        Author: User,
                        Receiver: Guild,
                        message: String = null,
                        Time: DateTime
                        ) extends RequestT[User, Guild]

case class EventRequest(
                         Author: User,
                         Receiver: Event,
                         message: String = null,
                         Time: DateTime
                         ) extends RequestT[User, Event]
case class TournamentTeamRequest(
                                  Author: Guild,
                                  Receiver: Tournament,
                                  message: String = null,
                                  Time: DateTime
                                  ) extends RequestT[Guild, Tournament]