package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.{AbstractOptionParser, RequiredWithValidation, RequiredWithoutValidation}

import java.time.temporal.ChronoUnit
import scala.util.Try

object StreamingOptionParser
  extends AbstractOptionParser[StreamingAppArguments] {

  toPartialOptionDef(PropertiesFileOption)
    .action((s, c) => c.copy(propertiesFile = s))

  val DatasourceFileOption: RequiredWithValidation[String] = new RequiredWithValidation[String] {
    override def shortOption: Char = 'j'
    override def longOption: String = "json"
    override def description: String = "Name of .json file with available datasources"
    override def validation: String => Either[String, Unit] =
      s => if (s.endsWith(".json")) success else failure(s"A .json file was expected. Found $s")
  }

  toPartialOptionDef(DatasourceFileOption)
    .action((s, c) => c.copy(dataSourcesFile = s))

  val LifetimeAmountOption: RequiredWithValidation[Int] = new RequiredWithValidation[Int] {
    override def shortOption: Char = 'l'
    override def longOption: String = "lifetime"
    override def description: String = "Lifetime of Spark streaming jobs"
    override def validation: Int => Either[String, Unit] =
      s => if (s > 0) success else failure(s"Invalid lifetime. Expected to be greater than 0. Found $s")
  }

  toPartialOptionDef(LifetimeAmountOption)
    .action((s, c) => c.copy(lifetimeAmount = s))

  val ChronoUnitOption: RequiredWithValidation[String] = new RequiredWithValidation[String] {
    override def shortOption: Char = 'c'
    override def longOption: String = "chrono-unit"
    override def description: String = s"${classOf[ChronoUnit].getSimpleName} for measuring lifetime of streaming jobs"
    override def validation: String => Either[String, Unit] =
      s => if (Try { ChronoUnit.valueOf(s.toUpperCase) }.isSuccess) success else
        failure(s"Unable to convert given value to a ${classOf[ChronoUnit].getSimpleName}. Found $s")
  }

  toPartialOptionDef(ChronoUnitOption)
    .action((s, c) => c.copy(chronoUnit = ChronoUnit.valueOf(s.toUpperCase)))

  val JobIdsOption: RequiredWithoutValidation[Seq[String]] = new RequiredWithoutValidation[Seq[String]] {
    override def shortOption: Char = 'j'
    override def longOption: String = "jobs"
    override def description: String = "Ids of streaming jobs to trigger"
  }

  toPartialOptionDef(JobIdsOption).
    action((s, c) => c.copy(jobIds = s))

  val SleepTimeOption: RequiredWithValidation[Int] = new RequiredWithValidation[Int] {
    override def shortOption: Char = 's'
    override def longOption: String = "sleep-time"
    override def description: String = "Sleep time (in seconds) for streaming jobs if no data are polled"
    override def validation: Int => Either[String, Unit] =
      s => if (s > 0) success else failure(s"Invalid sleeptime. Expected to be greater than 0. Found $s")
  }

  toPartialOptionDef(SleepTimeOption)
    .action((s, c) => c.copy(sleepTime = s))
}
