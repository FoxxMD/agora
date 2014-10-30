package com.esports.gtplatform.dao.slick

import models._
import scala.slick.jdbc.JdbcBackend

/**
 * Created by Matthew on 10/29/2014.
 */
trait TablesWithCustomQueries extends Tables{
    import profile.simple._

    implicit class GamesExtensions[C[_]](q: Query[Games, Game, C]) {

        def hydrated(implicit session: JdbcBackend.Session): List[Game] = {
            q.leftJoin(GamesTournamentsTypes).on { case (g, gt) => g.id === gt.gamesId}
                .leftJoin(TournamentsTypes).on { case ((g, gt), tt) => gt.tournamenttypesId === tt.id}
                .mapResult { case ((g, gt), tt) =>
                g.tournamentTypes = g.tournamentTypes.+(tt)
                g.ttLink = g.ttLink.+(gt)
                g
            }
                .list.groupBy(_.id).mapValues { groupsWithSameId =>
                groupsWithSameId.reduce { (previousGroup, group) =>
                    previousGroup.tournamentTypes = previousGroup.tournamentTypes.++(group.tournamentTypes)
                    previousGroup.ttLink = previousGroup.ttLink.++(group.ttLink)
                    previousGroup
                }
            }.values.toList
        }
    }
    implicit class GuildUsersExtensions[C[_]](q: Query[GuildsUsers, GuildUser, C]) {

        def hydrated(implicit session: JdbcBackend.Session): List[GuildUser] = {
            q.leftJoin(Users).on { case (gu, user) => gu.usersId === user.id}
                .leftJoin(Guilds).on { case ((gu, user), guild) => gu.guildsId === guild.id}
                .mapResult { case (((gu, user), guild)) =>
                gu.user = user
               gu.guild = guild
                gu
            }
/*                .list.groupBy(_.id).mapValues { groupsWithSameId =>
                groupsWithSameId.reduce { (previousGroup, group) =>
                    previousGroup.tournamentTypes = previousGroup.tournamentTypes.++(group.tournamentTypes)
                    previousGroup.ttLink = previousGroup.ttLink.++(group.ttLink)
                    previousGroup
                }
            }.values.toList*/
        }
    }
    implicit class UsersExtensions[C[_]](q: Query[Users, User, C]) {

        def hydrated(implicit session: JdbcBackend.Session): List[User] = {
            GuildsUsers.filter(x => x.usersId).hydrated
            //TODO da fuq
            q.leftJoin(GuildsUsers).on { case (user, gu) => user.id === gu.usersId}
            .leftJoin(Guilds).on { case ((user, gu), guild) => gu.guildsId === guild.id}
            .leftJoin(UsersPlatformProfile).on {case (((user,gu), guild), plat) => user.id === plat.usersId}
                .mapResult { case (((user, gu), guild), plat) =>
                user.guilds = user.guilds.:+(gu)
                user.gameProfiles = g.ttLink.+(gt)
                g
            }
                .list.groupBy(_.id).mapValues { groupsWithSameId =>
                groupsWithSameId.reduce { (previousGroup, group) =>
                    previousGroup.tournamentTypes = previousGroup.tournamentTypes.++(group.tournamentTypes)
                    previousGroup.ttLink = previousGroup.ttLink.++(group.ttLink)
                    previousGroup
                }
            }.values.toList
        }
    }
}
trait Profile {
    val profile: scala.slick.driver.JdbcProfile
    val simple:profile.simple.type = profile.simple
}

object CrudComponent extends Profile { this: Profile =>
    override val profile = super.profile
    import simple._

    abstract class Crud[T <: Table[E] with IdentifiableTable[PK], E <: Entity[PK], PK: BaseColumnType](implicit session: Session) {
        val query: TableQuery[T]
        def count: Int = query.length.run
        def getAll: List[E] = query.list
        def getPaginated(pageNo: Int, pageSize: Int): List[E] = query.drop((pageNo-1)*pageSize).take(pageSize).list
        def queryById(id: PK) = query.filter(_.id === id)
        def get(id: PK): Option[E] = queryById(id).firstOption
        def create(m: E): E = (query returning query.map(_.id) into ((m,id) => m)) += m

        def extractId(m: E): Option[PK] = m.id
        def update(m: E): E = extractId(m) match {
            case Some(id) =>
                queryById(id).update(m)
                m
            case None => throw new SlickException("Trying to update a model that doesn't exist!")//get(m, add(m))
        }
        //def saveAll(ms: E*): Option[Int] = query ++= ms
        def delete(id: PK): Unit = queryById(id).delete
        def delete(m: E): Unit = extractId(m) match {
            case Some(id) => delete(id)
            case None =>
        }
    }
}

trait Entity[PK] {
    def id: Option[PK]
}

trait IdentifiableTable[I] {
    def id: scala.slick.lifted.Column[I]
}
