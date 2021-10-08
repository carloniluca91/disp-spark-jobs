package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.{AbstractOptionParser, CliOption}

import java.time.temporal.ChronoUnit
import scala.util.{Failure, Success, Try}

object StreamingOptionParser
  extends AbstractOptionParser[StreamingAppArguments] {

  toPartialOptionDef(PropertiesFileOption)
    .action((s, c) => c.copy(propertiesFile = s))

  val DatasourceFileOption: CliOption[String] = new CliOption[String] {
    override def shortOption: Char = 'j'
    override def longOption: String = "json"
    override def optionDescription: String = "Name of .json file with available datasources"
    override def validation: Option[String => Either[String, Unit]] =
      Some(s => if (s.endsWith(".json")) success else failure(s"A .json file was expected. Found $s"))
  }

  toPartialOptionDef(DatasourceFileOption)
    .action((s, c) => c.copy(dataSourcesFile = s))

  val LifetimeAmountOption: CliOption[Int] = new CliOption[Int] {
    override def shortOption: Char = 'l'
    override def longOption: String = "lifetime"
    override def optionDescription: String = "Lifetime of Spark streaming jobs"
    override def validation: Option[Int => Either[String, Unit]] =
      Some(s => if (s > 0) success else failure(s"Invalid lifetime. Expected to be greater than 0. Found $s"))
  }

  toPartialOptionDef(LifetimeAmountOption)
    .action((s, c) => c.copy(lifetimeAmount = s))

  val ChronoUnitOption: CliOption[String] = new CliOption[String] {
    override def shortOption: Char = 'c'
    override def longOption: String = "chrono-unit"
    override def optionDescription: String = s"${classOf[ChronoUnit].getSimpleName} for measuring lifetime of streaming jobs"
    override def validation: Option[String => Either[String, Unit]] =
      Some(s => Try {
        ChronoUnit.valueOf(s.toUpperCase)
      } match {
        case Failure(_) => failure(s"Unable to convert given value to a ${classOf[ChronoUnit].getSimpleName}. Found $s")
        case Success(_) => success
      })
  }

  toPartialOptionDef(ChronoUnitOption)
    .action((s, c) => c.copy(chronoUnit = ChronoUnit.valueOf(s.toUpperCase)))

  val JobIdsOption: CliOption[Seq[String]] = new CliOption[Seq[String]] {
    override def shortOption: Char = 'j'
    override def longOption: String = "jobs"
    override def optionDescription: String = "Ids of streaming jobs to trigger"
    override def validation: Option[Seq[String] => Either[String, Unit]] = None
  }

  toPartialOptionDef(JobIdsOption).
    action((s, c) => c.copy(jobIds = s))

  val SleepTimeOption: CliOption[Int] = new CliOption[Int] {
    override def shortOption: Char = 's'
    override def longOption: String = "sleep-time"
    override def optionDescription: String = "Sleep time (in seconds) for streaming jobs if no data are polled"
    override def validation: Option[Int => Either[String, Unit]] =
      Some(s => if (s > 0) success else failure(s"Invalid sleeptime. Expected to be greater than 0. Found $s"))
  }

  toPartialOptionDef(SleepTimeOption)
    .action((s, c) => c.copy(sleepTime = s))
}
