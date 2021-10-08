package it.luca.disp.streaming.app

import it.luca.disp.core.Logging
import it.luca.disp.core.utils.{initSparkSession, loadProperties, replaceTokensWithProperties}
import it.luca.disp.streaming.app.option.StreamingAppArguments
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.SparkSession

import scala.io.{BufferedSource, Source}
import scala.util.Try

object StreamingJobRunner
  extends Logging {

  def run(arguments: StreamingAppArguments): Unit = {

    Try {

      val jobProperties: PropertiesConfiguration = loadProperties(arguments.propertiesFile)
      //val connection = initConnection(jobProperties)
      val bufferedSource: BufferedSource = Source.fromFile(arguments.dataSourcesFile)
      val datasourcesJsonString: String = replaceTokensWithProperties(bufferedSource.getLines().mkString("\n"), jobProperties)
      log.info(s"Successfully interpolated all tokens within file ${arguments.dataSourcesFile}")
      bufferedSource.close()

      val sparkSession: SparkSession = initSparkSession

    }
  }
}
