package it.luca.disp.streaming.app.job

import it.luca.disp.core.BaseTestSuite
import org.apache.commons.configuration2.PropertiesConfiguration

class StreamingJobRunnerTest
  extends BaseTestSuite {

  s"A ${nameOf[StreamingJobRunner]}" should "properly interpolate a string" in {

    val (k1, v1) = ("k1", "v1")
    val (k2, v2) = ("k2", "v2")
    val properties: PropertiesConfiguration = new PropertiesConfiguration
    properties.addProperty(k1, v1)
    properties.addProperty(k2, v2)
    properties.setThrowExceptionOnMissing(true)

    val string = "${k1} ${k2}"
    StreamingJobRunner.replaceTokensWithProperties(string, properties) shouldBe s"$v1 $v2"
  }
}
