package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.CustomOptionParser

object StreamingOptionParser
  extends CustomOptionParser[StreamingAppArguments] {

  val PropertiesFileOption: TypedRequiredWithValidation[String] = new TypedRequiredWithValidation[String] {
    override def shortOption: Char = CustomOptionParser.PropertiesFileShort
    override def longOption: String = CustomOptionParser.PropertiesFileLong
    override def description: String = CustomOptionParser.PropertiesFileDescription
    override def validation: String => Either[String, Unit] = CustomOptionParser.PropertiesFileValidation
    override def action: (String, StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(propertiesFile = s)
  }

  addOpt(PropertiesFileOption)

  val DatasourceFileOption: TypedRequiredWithValidation[String] = new TypedRequiredWithValidation[String] {
    override def shortOption: Char = 'j'
    override def longOption: String = "json"
    override def description: String = "Name of .json file with available datasources"
    override def validation: String => Either[String, Unit] =
      s => if (s.endsWith(".json")) success else failure(s"A .json file was expected. Found $s")

    override def action: (String, StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(dataSourcesFile = s)
  }

  addOpt(DatasourceFileOption)
}
