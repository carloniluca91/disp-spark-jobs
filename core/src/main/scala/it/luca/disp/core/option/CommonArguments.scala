package it.luca.disp.core.option

abstract class CommonArguments(val propertiesFile: String) {

  protected def formatOptions(seq: Seq[(CliOption[_], String)]): String = {

    seq.map { case (option, value) => s"  $option = $value" }
      .mkString("\n")
      .concat("\n")
  }
}
