package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.app.utils.{formatDate, toTimestamp}
import it.luca.disp.streaming.core.job.{StreamingJob, StreamingJobTest}
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.int002.{Int002Ciclo, Int002Payload}
import org.apache.spark.sql.DataFrame

import java.sql.Timestamp
import scala.collection.JavaConversions._

class Int002JobTest(override protected val streamingJob: Int002Job)
  extends StreamingJobTest[Int002Payload, Int002Job](streamingJob, classOf[Int002Payload], "int002.json") {

  override protected def testGeneratedDataFrame(dataFrame: DataFrame, instance: Int002Payload): Unit = {

    val cicli: Seq[Int002Ciclo] = instance.getCicli
    val expectedGiornoOraRiferimento: Seq[Timestamp] = cicli
      .map {x => toTimestamp(x.getGiornoOraRiferimento, DatePattern.INT002_GIORNO_ORA_RIFERIMENTO) }
    testNonConstantColumn(dataFrame.select("giorno_ora_riferimento"), _.getTimestamp(0), expectedGiornoOraRiferimento)

    val expectedGiorniGas: Seq[String] = cicli
      .map { x => StreamingJob.gasDay(toTimestamp(x.getGiornoOraRiferimento, DatePattern.INT002_GIORNO_ORA_RIFERIMENTO)) }
    val expectedMesi: Seq[String] = expectedGiorniGas
      .map {x => formatDate(x, DatePattern.DEFAULT_DATE, DatePattern.DEFAULT_MONTH) }

    testNonConstantColumn(dataFrame.select("giorno_gas"), _.getString(0), expectedGiorniGas)
    testNonConstantColumn(dataFrame.select("mese"), _.getString(0), expectedMesi)
  }
}
