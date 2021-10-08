package it.luca.disp.core.option

trait ApplicationArguments {

  protected def formatOptions(seq: Seq[(CliOption[_, _], String)]): String = {

    seq.map { case (option, value) => s"  $option = $value" }.mkString("\n").concat("\n")
  }
}

object ApplicationArguments {

  val PropertiesFileShort: Char = 'p'
  val PropertiesFileLong: String = "properties"
  val PropertiesFileDescription: String = "Name of .properties file for Spark application"
  val PropertiesFileValidation: String => Either[String, Unit] =
    s => if (s.endsWith(".properties")) Right() else Left(s"A .properties file was expected. Found $s")
}
