package it.luca.disp.merger.app.option

import it.luca.disp.core.option.{ApplicationArguments, CliOption}

case class MergerAppArguments(override val propertiesFile: String = "N.A")
  extends ApplicationArguments(propertiesFile) {

  override protected def getLinkedOptions: Map[CliOption[_, _], String] = {

    Map(MergerAppOptionParser.PropertiesFileOption -> propertiesFile)
  }
}
