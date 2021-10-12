package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.{ApplicationArguments, CliOption}

case class StreamingAppArguments(override val propertiesFile: String = "N.A.",
                                 dataSourcesFile: String = "N.A.")
  extends ApplicationArguments(propertiesFile) {

  override protected def getLinkedOptions: Map[CliOption[_, _], String] = {

    Map(StreamingOptionParser.PropertiesFileOption -> propertiesFile,
      StreamingOptionParser.DatasourceFileOption -> dataSourcesFile)
  }
}
