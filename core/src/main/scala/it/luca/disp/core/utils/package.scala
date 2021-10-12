package it.luca.disp.core

import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.spark.sql.SparkSession

import java.io.File
import java.sql.{Connection, DriverManager, SQLException}

package object utils
  extends Logging {

  /**
   * Initializes a [[Connection]] to Impala
   * @param properties instance of [[PropertiesConfiguration]]
   * @throws java.lang.ClassNotFoundException if JDBC driver class is not found
   * @throws java.sql.SQLException if connection's initialization fails
   * @return instance of [[Connection]]
   */

  @throws[ClassNotFoundException]
  @throws[SQLException]
  def initConnection(properties: PropertiesConfiguration): Connection = {

    val driverClassName = properties.getString("impala.driver.className")
    val impalaJdbcUrl = properties.getString("impala.jdbc.url")
    Class.forName(driverClassName)
    val connection = DriverManager.getConnection(impalaJdbcUrl)
    log.info("Successfully initialized Impala JDBC connection with URL {}", impalaJdbcUrl)
    connection
  }

  /**
   * Init a [[SparkSession]] and set its current database
   * @param properties [[PropertiesConfiguration]] from which dbName should be extracted
   * @return active [[SparkSession]] with Hive support and current database set
   */

  def initSparkSession(properties: PropertiesConfiguration): SparkSession = {

    val sparkSession = SparkSession.builder
      .enableHiveSupport
      .config("hive.exec.dynamic.partition", "true")
      .config("hive.exec.dynamic.partition.mode", "nonstrict")
      .getOrCreate

    val dbName: String = properties.getString("impala.db.name")
    sparkSession.catalog.setCurrentDatabase(dbName)
    log.info(s"Successfully initialized ${classOf[SparkSession].getSimpleName} with current db $dbName")
    sparkSession
  }

  /**
   * Load given .properties file
   * @param fileName name of .properties file
   * @return instance of [[PropertiesConfiguration]]
   * @throws ConfigurationException if case of issues
   */

  @throws[ConfigurationException]
  def loadProperties(fileName: String): PropertiesConfiguration = {

    val builder = new FileBasedConfigurationBuilder[PropertiesConfiguration](classOf[PropertiesConfiguration])
      .configure(new Parameters().fileBased
        .setThrowExceptionOnMissing(true)
        .setListDelimiterHandler(new DefaultListDelimiterHandler(',')).setFile(new File(fileName)))

    val properties: PropertiesConfiguration = builder.getConfiguration
    log.info(s"Successfully loaded .properties file $fileName")
    properties
  }
}
