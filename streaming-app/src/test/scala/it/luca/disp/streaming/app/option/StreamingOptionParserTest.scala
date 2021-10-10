package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.CustomOptionParserTest

import java.time.temporal.ChronoUnit

class StreamingOptionParserTest
  extends CustomOptionParserTest {

  s"A ${nameOf[StreamingOptionParser.type]}" should s"correctly validate arguments" in {

    testOptionValidation[String](StreamingOptionParser.PropertiesFileOption, "file.properties", "file.txt")
    testOptionValidation[String](StreamingOptionParser.DatasourceFileOption, "file.json", "file.xml")
    testOptionValidation[Int](StreamingOptionParser.LifetimeAmountOption, 10 :: Nil, -1 :: 0 :: Nil)
    testOptionValidation[String](StreamingOptionParser.ChronoUnitOption, "MINUTES" :: "HOURS" :: "DAYS" :: Nil, "other" :: Nil)
    testOptionValidation[Int](StreamingOptionParser.SleepTimeOption, 30 :: Nil, -1 :: 0 :: Nil)
  }

  it should "correctly parse main class arguments" in {

    val propertiesFile = "file.properties"
    val dataSourceFile = "file.json"
    val jobIds: Seq[String] = "WEBDISP" :: "JARVIS" :: Nil
    val lifetimeAmount = 10
    val chronoUnit = ChronoUnit.MINUTES
    val sleepTime = 30

    val args: Seq[String] = getArgs(Map(StreamingOptionParser.PropertiesFileOption -> propertiesFile,
      StreamingOptionParser.DatasourceFileOption -> dataSourceFile,
      StreamingOptionParser.JobIdsOption -> jobIds.mkString(","),
      StreamingOptionParser.LifetimeAmountOption -> lifetimeAmount.toString,
      StreamingOptionParser.ChronoUnitOption -> chronoUnit.name(),
      StreamingOptionParser.SleepTimeOption -> sleepTime.toString))

    val optionalArguments: Option[StreamingAppArguments] = StreamingOptionParser.parse(args, StreamingAppArguments())
    optionalArguments shouldBe Some(_: StreamingAppArguments)
    val streamingAppArguments: StreamingAppArguments = optionalArguments.get

    streamingAppArguments.propertiesFile shouldBe propertiesFile
    streamingAppArguments.dataSourcesFile shouldBe dataSourceFile
    streamingAppArguments.jobIds shouldBe jobIds
    streamingAppArguments.lifetimeAmount shouldBe lifetimeAmount
    streamingAppArguments.chronoUnit shouldBe chronoUnit
    streamingAppArguments.sleepTime shouldBe sleepTime
  }
}
