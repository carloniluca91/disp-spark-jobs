package it.luca.disp.streaming.app

import it.luca.disp.core.Logging
import it.luca.disp.core.utils.{initConnection, initSparkSession, loadProperties, replaceTokensWithProperties}
import it.luca.disp.streaming.app.datasource.DataSourcesCollection
import it.luca.disp.streaming.app.option.StreamingAppArguments
import it.luca.disp.streaming.core.ObjectDeserializer.deserializeString
import it.luca.disp.streaming.core.job.Consumer
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.SparkSession

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.JavaConversions._
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

object StreamingJobRunner
  extends Logging {

  def run(arguments: StreamingAppArguments): Unit = {

    Try {

      val properties: PropertiesConfiguration = loadProperties(arguments.propertiesFile)
      val bufferedSource: BufferedSource = Source.fromFile(arguments.dataSourcesFile)
      val datasourcesJsonString: String = replaceTokensWithProperties(bufferedSource.getLines().mkString("\n"), properties)
      log.info(s"Successfully interpolated all tokens within file ${arguments.dataSourcesFile}")
      bufferedSource.close()

      val collection: DataSourcesCollection = deserializeString(datasourcesJsonString, classOf[DataSourcesCollection])
      val connection = initConnection(properties)
      val sparkSession: SparkSession = initSparkSession
      val consumers: Seq[Consumer[_]] = collection.getDataSourcesForIds(arguments.jobIds)
        .map { _.initConsumer(sparkSession, connection, properties) }

      val applicationEndTime: LocalDateTime = LocalDateTime.now().plus(arguments.lifetimeAmount, arguments.chronoUnit)
      val sleepTimeInSeconds = arguments.sleepTime
      log.info(s"Starting to run the circus of ${classOf[Consumer[_]].getSimpleName}(s)")

      while (LocalDateTime.now.isBefore(applicationEndTime)) {

        val shouldStayAwake: Boolean = consumers.map { _.poll() }.forall(identity)
        if (shouldStayAwake) {
          log.info("Some consumers has polled data from their topics. There should still be some work to do.")
        } else {
          log.info(s"None of the ${consumers.size} consumer(s) has polled data. Stopping the circus for $sleepTimeInSeconds second(s)")
          Thread.sleep(sleepTimeInSeconds * 1000L)
          log.info("Waking up the circus once again after {} second(s) of rest", sleepTimeInSeconds)
        }
      }

      val endTime: String = applicationEndTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      log.info("Application end time ({}) has come. Circus is leaving town. Goodbye ;)", endTime)
    } match {
      case Failure(exception) => log.error("Caught exception while trying to kick off the circus. Stack trace: ", exception)
      case Success(_) =>
    }
  }
}
