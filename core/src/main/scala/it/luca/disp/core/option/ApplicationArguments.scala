package it.luca.disp.core.option

abstract class ApplicationArguments(val propertiesFile: String) {

  protected def getLinkedOptions: Map[CliOption[_, _], String]

  override def toString: String = {

    getLinkedOptions.map { case (key, value) => s" $key = $value"}
      .mkString("\n")
      .concat("\n")
  }
}
