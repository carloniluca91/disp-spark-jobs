package it.luca.disp.streaming.app.option

import it.luca.disp.core.option.ApplicationArguments

import java.time.temporal.ChronoUnit

case class StreamingAppArguments(propertiesFile: String = "N.A.",
                                 dataSourcesFile: String = "N.A.",
                                 lifetimeAmount: Int = 0,
                                 chronoUnit: ChronoUnit = ChronoUnit.SECONDS,
                                 jobIds: Seq[String] = Seq.empty[String],
                                 sleepTime: Int = 0)
  extends ApplicationArguments {

  override def toString: String = {

    super.formatOptions((StreamingOptionParser.PropertiesFileOption, propertiesFile) ::
      (StreamingOptionParser.DatasourceFileOption, dataSourcesFile) ::
      (StreamingOptionParser.LifetimeAmountOption, lifetimeAmount.toString) ::
      (StreamingOptionParser.ChronoUnitOption, chronoUnit.name()) ::
      (StreamingOptionParser.JobIdsOption, jobIds.mkString(", ")) ::
      (StreamingOptionParser.SleepTimeOption, sleepTime.toString) :: Nil)
  }
}
