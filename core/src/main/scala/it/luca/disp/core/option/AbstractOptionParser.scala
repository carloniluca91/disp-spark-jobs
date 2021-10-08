package it.luca.disp.core.option

import scopt.{OptionDef, OptionParser, Read}

abstract class AbstractOptionParser[C <: CommonArguments with Product with Serializable]
  extends OptionParser[C]("scopt 4.0") {

  final val PropertiesFileOption: RequiredWithValidation[String] = new RequiredWithValidation[String] {
    override def shortOption: Char = 'p'
    override def longOption: String = "properties"
    override def description: String = "Name of .properties file for Spark application"
    override def validation: String => Either[String, Unit] =
      s => if (s.endsWith(".properties")) success else failure(s"A .properties file was expected. Found $s")
  }

  protected def toPartialOptionDef[A](cliOption: CliOption[A])(implicit evidence$2: Read[A]): OptionDef[A, C] = {

    val basicOptionDef: OptionDef[A, C] = opt[A](cliOption.shortOption, cliOption.longOption)
      .text(cliOption.description)

    val optionMaybeRequired: OptionDef[A, C] = if (cliOption.required) basicOptionDef.required() else basicOptionDef.optional()
    cliOption.optionalValidation
      .map{optionMaybeRequired.validate}
      .getOrElse(optionMaybeRequired)
  }
}
