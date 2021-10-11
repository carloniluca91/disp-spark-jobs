package it.luca.disp.merger.app.option

import it.luca.disp.core.option.CustomOptionParser

object MergerAppOptionParser
  extends CustomOptionParser[MergerAppArguments] {

  val PropertiesFileOption: TypedRequiredWithValidation[String] = new TypedRequiredWithValidation[String] {
    override def shortOption: Char = CustomOptionParser.PropertiesFileShort
    override def longOption: String = CustomOptionParser.PropertiesFileLong
    override def description: String = CustomOptionParser.PropertiesFileDescription
    override def validation: String => Either[String, Unit] = CustomOptionParser.PropertiesFileValidation
    override def action: (String, MergerAppArguments) => MergerAppArguments = (s, c) => c.copy(propertiesFile = s)
  }

  addOpt(PropertiesFileOption)

}
