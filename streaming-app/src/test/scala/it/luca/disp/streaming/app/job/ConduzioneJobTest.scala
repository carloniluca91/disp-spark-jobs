package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.app.utils.{formatDate, toTimestamp}
import it.luca.disp.streaming.core.job.{StreamingJob, StreamingJobTest}
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.conduzione.{ConduzionePayload, ConduzioneRecord}
import org.apache.spark.sql.DataFrame

import java.sql.Timestamp
import scala.collection.JavaConversions._

class ConduzioneJobTest(override protected val streamingJob: ConduzioneJob)
  extends StreamingJobTest[ConduzionePayload, ConduzioneJob](streamingJob, classOf[ConduzionePayload], "conduzione.json") {

  override protected def testGeneratedDataFrame(dataFrame: DataFrame, instance: ConduzionePayload): Unit = {

    val records: Seq[ConduzioneRecord] = instance.getRecords
    val expectedDataRiferimentos: Seq[Timestamp] = records.map {
      x => toTimestamp(x.getDrif, DatePattern.CONDUZIONE_DRIF) }
    testNonConstantColumn(dataFrame.select("data_riferimento"), _.getTimestamp(0), expectedDataRiferimentos)

    val expectedDataEmissiones: Seq[Timestamp] = records.map {
      x => toTimestamp(x.getDre, DatePattern.CONDUZIONE_DRE) }
    testNonConstantColumn(dataFrame.select("data_emissione"), _.getTimestamp(0), expectedDataEmissiones)

    val expectedGiorniGas: Seq[String] = records.map {
      x => StreamingJob.gasDay(toTimestamp(x.getDrif, DatePattern.CONDUZIONE_DRIF)) }
    testNonConstantColumn(dataFrame.select("giorno_gas"), _.getString(0), expectedGiorniGas)

    val expectedMesi: Seq[String] = expectedGiorniGas.map {
      x => formatDate(x, DatePattern.DEFAULT_DATE, DatePattern.DEFAULT_MONTH)}
    testNonConstantColumn(dataFrame.select("mese"), _.getString(0), expectedMesi)
  }
}
