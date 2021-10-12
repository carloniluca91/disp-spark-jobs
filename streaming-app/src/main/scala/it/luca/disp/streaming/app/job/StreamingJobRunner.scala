package it.luca.disp.streaming.app.job

import it.luca.disp.core.Logging
import it.luca.disp.core.utils.{initConnection, initSparkSession, loadProperties}
import it.luca.disp.streaming.app.datasource.DataSourceCollection
import it.luca.disp.streaming.app.option.StreamingAppArguments
import it.luca.disp.streaming.core.ObjectDeserializer.deserializeString
import it.luca.disp.streaming.core.job.Consumer
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.SparkSession

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.collection.JavaConversions._
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

class StreamingJobRunner
  extends Logging {

  def run(arguments: StreamingAppArguments): Unit = {

    Try {

      // Read .properties file and use it to interpolate datasource .json file
      val properties: PropertiesConfiguration = loadProperties(arguments.propertiesFile)
      val bufferedSource: BufferedSource = Source.fromFile(arguments.dataSourcesFile)
      val datasourcesJsonString: String = StreamingJobRunner.replaceTokensWithProperties(bufferedSource.getLines().mkString("\n"), properties)
      log.info(s"Successfully interpolated all tokens within file ${arguments.dataSourcesFile}")
      bufferedSource.close()

      // Deserialize datasource .json file and initalize requested datasources
      val collection: DataSourceCollection = deserializeString(datasourcesJsonString, classOf[DataSourceCollection])
      val connection = initConnection(properties)
      val sparkSession: SparkSession = initSparkSession(properties)

      val consumers: Seq[Consumer[_]] = collection.getDataSourcesForIds(properties.getList(classOf[String], "spark.streaming.jobs"))
        .map { _.initConsumer(sparkSession, connection, properties) }

      val sleepTimeInSeconds: Int = properties.getInt("spark.streaming.sleepTimeInSeconds")
      val lifetimeAmout: Int = properties.getInt("spark.streaming.lifetime.amount")
      val chronoUnit: ChronoUnit = ChronoUnit.valueOf(properties.getString("spark.streaming.lifetime.unit").toUpperCase)
      val applicationEndTime: LocalDateTime = LocalDateTime.now().plus(lifetimeAmout, chronoUnit)
      log.info(s"Configured lifetime is $lifetimeAmout $chronoUnit. Starting to run the circus of ${classOf[Consumer[_]].getSimpleName}(s)")

      // Start the circus
      while (LocalDateTime.now.isBefore(applicationEndTime)) {

        val shouldStayAwake: Boolean = consumers.map { _.poll() }.exists(identity)
        if (shouldStayAwake) {
          log.info("Some consumers has polled data from their topics. There should still be some work to do.")
        } else {
          log.info(s"None of the ${consumers.size} consumer(s) has polled data. Stopping the circus for $sleepTimeInSeconds second(s)")
          Thread.sleep(sleepTimeInSeconds * 1000L)
          log.info(s"Waking up the circus once again after $sleepTimeInSeconds second(s) of rest")
        }
      }
    } match {
      case Failure(exception) => log.error("Caught exception while trying to kick off the circus. Stack trace: ", exception)
      case Success(_) => log.info("Application end time has come. Circus is leaving town. Goodbye ;)")
    }
  }
}

object StreamingJobRunner {

  /**
   * Interpolates given string using an instance of [[PropertiesConfiguration]]
   * @param string input string
   * @param properties instance of [[PropertiesConfiguration]]
   * @return interpolated string (e.g. a token like ${a.property} is replaced with the value of property
   *         'a.property' retrieved from the instance of [[PropertiesConfiguration]]
   */

  def replaceTokensWithProperties(string: String, properties: PropertiesConfiguration): String = {

    "\\$\\{([\\w.]+)}".r
      .replaceAllIn(string, m => s"${properties.getString(m.group(1))}")
  }
}
