package com.esports.gtplatform.dao.slick

import models._
import scala.slick.jdbc.JdbcBackend
import scala.slick._
import scala.slick.lifted._
import scala.slick.lifted.ExtensionMethods

/**
 * Created by Matthew on 10/29/2014.
 */
trait TablesWithCustomQueries extends Tables with ColumnExtensionMethods {

    implicit class GamesExtensions[C[_]](q: Query[Games, Game, C]) {

        def hydrated(implicit session: JdbcBackend.Session): List[Game] = {
            q.leftJoin(GamesTournamentsTypes).on { case (g, gt) => g.id === gt.gamesId}
                .leftJoin(TournamentsTypes).on { case ((g, gt), tt) => gt.tournamenttypesId === tt.id}
                .mapResult { case ((g, gt), tt) =>
                g.tournamentTypes = g.tournamentTypes.:+(tt)
                g.ttLink = g.ttLink.:+(gt)
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
