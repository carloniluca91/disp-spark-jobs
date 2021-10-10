package it.luca.disp.core.option

import scopt.{OptionDef, OptionParser, Read}

abstract class CustomOptionParser[C]
  extends OptionParser[C]("scopt 4.0") {

  type TypedCliOption[A] = CliOption[A, C]
  type TypedRequiredWithValidation[A] = RequiredWithValidation[A, C]
  type TypedRequiredWithoutValidation[A] = RequiredWithoutValidation[A, C]

  protected def addOpt[A](cliOption: TypedCliOption[A])(implicit evidence$2: Read[A]): OptionDef[A, C] = {

    val basicOptionDef: OptionDef[A, C] = opt[A](cliOption.shortOption, cliOption.longOption)
      .text(cliOption.description)

    val optionMaybeRequired: OptionDef[A, C] = if (cliOption.required) basicOptionDef.required() else basicOptionDef.optional()
    cliOption.optionalValidation
      .map{optionMaybeRequired.validate}
      .getOrElse(optionMaybeRequired)
      .action(cliOption.action)
  }
}
