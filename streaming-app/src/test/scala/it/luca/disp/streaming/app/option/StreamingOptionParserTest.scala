package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.CustomOptionParserTest

class StreamingOptionParserTest
  extends CustomOptionParserTest {

  s"A ${nameOf[StreamingOptionParser.type]}" should s"correctly validate arguments" in {

    testOptionValidation[String](StreamingOptionParser.PropertiesFileOption, "file.properties", "file.txt")
    testOptionValidation[String](StreamingOptionParser.DatasourceFileOption, "file.json", "file.xml")
  }

  it should "correctly parse main class arguments" in {

    val propertiesFile = "file.properties"
    val dataSourceFile = "file.json"

    val args: Seq[String] = getArgs(Map(StreamingOptionParser.PropertiesFileOption -> propertiesFile,
      StreamingOptionParser.DatasourceFileOption -> dataSourceFile))

    val optionalArguments: Option[StreamingAppArguments] = StreamingOptionParser.parse(args, StreamingAppArguments())
    optionalArguments shouldBe Some(_: StreamingAppArguments)
    val streamingAppArguments: StreamingAppArguments = optionalArguments.get

    streamingAppArguments.propertiesFile shouldBe propertiesFile
    streamingAppArguments.dataSourcesFile shouldBe dataSourceFile
  }
}
