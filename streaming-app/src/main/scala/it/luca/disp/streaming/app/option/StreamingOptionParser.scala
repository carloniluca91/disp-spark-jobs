package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.{ApplicationArguments, CustomOptionParser}

import java.time.temporal.ChronoUnit
import scala.util.Try

object StreamingOptionParser
  extends CustomOptionParser[StreamingAppArguments] {

  val PropertiesFileOption: TypedRequiredWithValidation[String] = new TypedRequiredWithValidation[String] {
    override def shortOption: Char = ApplicationArguments.PropertiesFileShort
    override def longOption: String = ApplicationArguments.PropertiesFileLong
    override def description: String = ApplicationArguments.PropertiesFileDescription
    override def validation: String => Either[String, Unit] = ApplicationArguments.PropertiesFileValidation
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

  val JobIdsOption: TypedRequiredWithoutValidation[Seq[String]] = new TypedRequiredWithoutValidation[Seq[String]] {
    override def shortOption: Char = 'd'
    override def longOption: String = "datasources"
    override def description: String = "Ids of streaming jobs to trigger"
    override def action: (Seq[String], StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(jobIds = s)
  }

  addOpt(JobIdsOption)

  val LifetimeAmountOption: TypedRequiredWithValidation[Int] = new TypedRequiredWithValidation[Int] {
    override def shortOption: Char = 'l'
    override def longOption: String = "lifetime"
    override def description: String = "Lifetime of Spark streaming jobs"
    override def validation: Int => Either[String, Unit] =
      s => if (s > 0) success else failure(s"Invalid lifetime. Expected to be greater than 0. Found $s")

    override def action: (Int, StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(lifetimeAmount = s)
  }

  addOpt(LifetimeAmountOption)

  val ChronoUnitOption: TypedRequiredWithValidation[String] = new TypedRequiredWithValidation[String] {
    override def shortOption: Char = 'c'
    override def longOption: String = "chrono-unit"
    override def description: String = s"${classOf[ChronoUnit].getSimpleName} for measuring lifetime of streaming jobs"
    override def validation: String => Either[String, Unit] =
      s => if (Try { ChronoUnit.valueOf(s.toUpperCase) }.isSuccess) success else
        failure(s"Unable to convert given value to a ${classOf[ChronoUnit].getSimpleName}. Found $s")

    override def action: (String, StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(chronoUnit = ChronoUnit.valueOf(s.toUpperCase))
  }

  addOpt(ChronoUnitOption)

  val SleepTimeOption: TypedRequiredWithValidation[Int] = new TypedRequiredWithValidation[Int] {
    override def shortOption: Char = 's'
    override def longOption: String = "sleep-time"
    override def description: String = "Sleep time (in seconds) for streaming jobs if no data are polled"
    override def validation: Int => Either[String, Unit] =
      s => if (s > 0) success else failure(s"Invalid sleeptime. Expected to be greater than 0. Found $s")

    override def action: (Int, StreamingAppArguments) => StreamingAppArguments = (s, c) => c.copy(sleepTime = s)
  }

  addOpt(SleepTimeOption)
}
