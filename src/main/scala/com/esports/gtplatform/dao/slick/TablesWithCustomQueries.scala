package com.esports.gtplatform.dao.slick

import io.strongtyped.active.slick.ActiveSlick
import io.strongtyped.active.slick.models.Identifiable
import models._
import scala.slick.driver.JdbcDriver
import scala.slick.jdbc.JdbcBackend

/**
 * Created by Matthew on 10/29/2014.
 */
trait TablesWithCustomQueries {
    this: ActiveSlick with Schema =>
    import JdbcDriver.simple._


/*    implicit class QueryExtensions2[T,E]
    ( val q: Query[T,E] ){
        def autoJoin[T2,E2]
        ( q2:Query[T2,E2] )
        ( implicit condition: (T,T2) => Column[Boolean] )
        : Query[(T,T2),(E,E2)]
        = q.join(q2).on(condition)
    }*/

    implicit class GamesExtensions(val model: Game) extends ActiveRecord[Game] {

        override val table = Games
        def withRelationships = {
            model.table.join(GamesTournamentsTypes).on(_.id === _.gamesId)
                .join(TournamentsTypes).on(_._2.tournamenttypesId === _.id)
                .mapResult[Game]{
                case ((g,link),tt) =>
                   model.ttLink = model.ttLink + link.copy()
                   model.tournamentTypes = model.tournamentTypes + tt.copy()
                model
            }.list
        }
    }
    implicit class  GuildUsersExtensions(val model: GuildUser) extends ActiveRecord[GuildUser] {
        override val table = GuildsUsers
        def withRelationships = {
            model.table.join(Guilds).on(_.guildsId === _.id)
            .join(Users).on(_._1.usersId === _.id)
            .mapResult[GuildUser] {
                case((gu,g),u) =>
                model.guild = g
                model.user = u
                model
            }.list
        }
    }
    implicit class UsersExtensions(val model: User) extends ActiveRecord[User] {
        override val table = Users
        def withRelationships = {
            model.table.join(GuildsUsers).on(_.id === _.usersId)
                .join(UsersPlatformProfile).on(_._1.id === _.usersId)
                .mapResult[User]{
                case ((u, gu),p) =>
                    model.guilds = model.guilds ++ gu.withRelationships
                    model.gameProfiles = model.gameProfiles.::(p)
                model
            }.list
        }
    }
/*    implicit class GamesExtensions[C[_]](q: Query[Games, Game, C]) {

        def withGameTournaments = {
            q.join(GamesTournamentsTypes).on(_.id === _.gamesId)
            .join(TournamentsTypes).on(_._2.tournamenttypesId === _.id)
                .mapResult[Game]{
                case ((g,link),tt) =>
                g.ttLink = link
                g.tournamentTypes = tt
            }.list

                /*.list.groupBy(_.id).mapValues { groupsWithSameId =>
                groupsWithSameId.reduce { (previousGroup, group) =>
                    previousGroup.tournamentTypes = previousGroup.tournamentTypes.++(group.tournamentTypes)
                    previousGroup.ttLink = previousGroup.ttLink.++(group.ttLink)
                    previousGroup
                }
            }.values.toList*/
        }
            }*/
/*    implicit class GuildUsersExtensions[C[_]](q: Query[GuildsUsers, GuildUser, C]) {

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
    }*/
/*    implicit class UsersExtensions[C[_]](q: Query[Users, User, C]) {

        def withRelationships = {
            q.join(GamesTournamentsTypes).on(_.id === _.gamesId)
                .join(TournamentsTypes).on(_._2.tournamenttypesId === _.id)
                .mapResult[Game]{
                case ((g,link),tt) =>
                    g.ttLink = link
                    g.tournamentTypes = tt
            }.list

            /*.list.groupBy(_.id).mapValues { groupsWithSameId =>
            groupsWithSameId.reduce { (previousGroup, group) =>
                previousGroup.tournamentTypes = previousGroup.tournamentTypes.++(group.tournamentTypes)
                previousGroup.ttLink = previousGroup.ttLink.++(group.ttLink)
                previousGroup
            }
        }.values.toList*/
        }
    }*/
/*    implicit class UsersExtensions[C[_]](q: Query[Users, User, C]) {

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
    }*/
    //val Users = EntityTableQuery[User, Users](tag => new Users(tag))

/*    implicit class UsersExtensions(val model: User) extends ActiveRecord[User] {

        /*override def table = Users*/

    }*/
}
/*
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
*/
