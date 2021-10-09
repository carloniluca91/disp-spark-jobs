package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.app.utils.{formatDate, toTimestamp}
import it.luca.disp.streaming.core.job.{StreamingJob, StreamingJobTest}
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.webdisp.{WebdispNomina, WebdispPayload}
import org.apache.spark.sql.DataFrame

import java.sql.Timestamp
import scala.collection.JavaConversions._

class WebdispJobTest(override protected val streamingJob: WebdispJob)
  extends StreamingJobTest[WebdispPayload, WebdispJob](streamingJob, classOf[WebdispPayload], "webdisp.json"){

  override protected def testGeneratedDataFrame(dataFrame: DataFrame, instance: WebdispPayload): Unit = {

    val expectedDataOraInvio: Timestamp = toTimestamp(instance.getDataOraInvio, DatePattern.WEBDISP_DATA_ORA_INVIO)
    testConstantColumn(dataFrame.select("data_ora_invio"), _.getTimestamp(0), expectedDataOraInvio)

    val nomine: Seq[WebdispNomina] = instance.getNomine
    val expectedDataElaborazione: Seq[Timestamp] = nomine.map { x => toTimestamp(x.getDataElaborazione, DatePattern.WEBDISP_DATA_ELABORAZIONE) }
    val expectedDataDecorrenza: Seq[Timestamp] = nomine.map { x => toTimestamp(x.getDataDecorrenza, DatePattern.WEBDISP_DATA_DECORRENZA) }
    val expectedGiorniGas: Seq[String] = nomine.map{ x => StreamingJob.gasDay(toTimestamp(x.getDataDecorrenza, DatePattern.WEBDISP_DATA_DECORRENZA)) }
    val expectedMesi: Seq[String] = expectedGiorniGas.map { x => formatDate(x, DatePattern.DEFAULT_DATE, DatePattern.DEFAULT_MONTH) }

    testNonConstantColumn(dataFrame.select("data_elaborazione"), _.getTimestamp(0), expectedDataElaborazione)
    testNonConstantColumn(dataFrame.select("data_decorrenza"), _.getTimestamp(0), expectedDataDecorrenza)
    testNonConstantColumn(dataFrame.select("giorno_gas"), _.getString(0), expectedGiorniGas)
    testNonConstantColumn(dataFrame.select("mese"), _.getString(0), expectedMesi)
  }
}
