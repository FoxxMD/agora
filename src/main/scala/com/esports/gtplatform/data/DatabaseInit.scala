package com.esports.gtplatform.data

import java.util.Properties

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.apache.commons.dbcp.BasicDataSourceFactory
import org.slf4j.LoggerFactory
/*Not using this right, IGNORE ME
*
*
*
* */
trait DatabaseInit {
  val logger = LoggerFactory.getLogger(getClass)

  val properties = new Properties
  properties.load(getClass.getResourceAsStream("/jdbc.mysql.properties"))


  def openDb() {
    val datasource = BasicDataSourceFactory.createDataSource(properties)
  }

  val databaseUsername = "gtgamefest"
  val databasePassword = "testing"
  val databaseConnection = "jdbc:mysql://localhost/gtgamefest_new"

  var cpds = new ComboPooledDataSource

  def configureDb() {
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(databaseConnection)
    cpds.setUser(databaseUsername)
    cpds.setPassword(databasePassword)

    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(50)

  }

  def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close()
  }
}



