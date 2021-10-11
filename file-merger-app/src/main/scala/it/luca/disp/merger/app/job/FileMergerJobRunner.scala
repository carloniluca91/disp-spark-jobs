package it.luca.disp.merger.app.job

import it.luca.disp.core.Logging
import it.luca.disp.core.utils.{initConnection, initSparkSession, loadProperties}
import it.luca.disp.merger.app.option.MergerAppArguments
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.SparkSession

import java.sql.Connection
import scala.util.{Failure, Success, Try}

object FileMergerJobRunner
  extends Logging {

  def run(arguments: MergerAppArguments): Unit = {

    Try {

      // Initialize required stuff for triggering file merger job
      val properties: PropertiesConfiguration = loadProperties(arguments.propertiesFile)
      val impalaConnection: Connection = initConnection(properties)
      val sparkSession: SparkSession = initSparkSession

      new FileMergerJob(sparkSession, impalaConnection, properties).run()
    } match {
      case Failure(exception) => log.error("Caught exception while running file merger job. Stack trace: ", exception)
      case Success(_) => log.info("Successfully executed file merger job")
    }
  }
}
