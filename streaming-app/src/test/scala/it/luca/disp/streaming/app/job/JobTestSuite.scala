package it.luca.disp.streaming.app.job

import it.luca.disp.core.BaseTestSuite
import it.luca.disp.core.utils.loadProperties
import it.luca.disp.streaming.core.StringConsumerRecord
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.BeforeAndAfterAll

class JobTestSuite
  extends BaseTestSuite
    with BeforeAndAfterAll {

  protected final val sparkSession: SparkSession = SparkSession.builder()
    .master("local[1]")
    .appName(classOf[JobTestSuite].getSimpleName)
    .getOrCreate()

  protected final val properties: PropertiesConfiguration = loadProperties("spark_application.properties")

  override protected def afterAll(): Unit = {

    sparkSession.stop()
    super.afterAll()
  }

  s"A ${nameOf[JobTestSuite]}" should
    s"check that a ${nameOf[WebdispJob]} successfully converts a ${nameOf[StringConsumerRecord]} to a ${nameOf[DataFrame]}" in {

    new WebdispJobTest(new WebdispJob(sparkSession, null, properties)).run()
  }

  it should s"check that a ${nameOf[JarvisJob]} successfully converts a ${nameOf[StringConsumerRecord]} to a ${nameOf[DataFrame]}" in {

    new JarvisJobTest(new JarvisJob(sparkSession, null, properties)).run()
  }

  it should s"check that a ${nameOf[Int002Job]} successfully converts a ${nameOf[StringConsumerRecord]} to a ${nameOf[DataFrame]}" in {

    new Int002JobTest(new Int002Job(sparkSession, null, properties)).run()
  }

  it should s"check that a ${nameOf[ConduzioneJob]} successfully converts a ${nameOf[StringConsumerRecord]} to a ${nameOf[DataFrame]}" in {

    new ConduzioneJobTest(new ConduzioneJob(sparkSession, null, properties)).run()
  }

}
