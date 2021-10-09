package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.app.utils.{formatDate, toTimestamp}
import it.luca.disp.streaming.core.job.StreamingJobTest
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.jarvis.JarvisPayload
import org.apache.spark.sql.DataFrame

import java.sql.Timestamp

class JarvisJobTest(override protected val streamingJob: JarvisJob)
  extends StreamingJobTest[JarvisPayload, JarvisJob](streamingJob, classOf[JarvisPayload], "jarvis.json"){

  override protected def testGeneratedDataFrame(dataFrame: DataFrame, instance: JarvisPayload): Unit = {

    testConstantColumn(dataFrame.select("ambito_flusso"), _.getString(0), instance.getAmbitoFlusso)
    testConstantColumn(dataFrame.select("nome_flusso"), _.getString(0), instance.getNomeFlusso)
    testConstantColumn(dataFrame.select("impresa_mittente"), _.getString(0), instance.getImpresaMittente)
    testConstantColumn(dataFrame.select("numero_dati"), _.getInt(0), instance.getNumeroDati)

    val expectedDataCreazione: Timestamp = toTimestamp(instance.getDataDiCreazione, DatePattern.JARVIS_DATA_DI_CREAZIONE)
    val expectedDataProcedura: String = formatDate(instance.getDataProcedura, DatePattern.JARVIS_DATA_PROCEDURA, DatePattern.DEFAULT_DATE)
    val expectedGiornoGas: String = formatDate(instance.getGiornoGas, DatePattern.JARVIS_GIORNO_GAS, DatePattern.DEFAULT_DATE)
    val expectedMese: String = formatDate(instance.getGiornoGas, DatePattern.JARVIS_GIORNO_GAS, DatePattern.DEFAULT_MONTH)

    testConstantColumn(dataFrame.select("data_creazione"), _.getTimestamp(0), expectedDataCreazione)
    testConstantColumn(dataFrame.select("data_procedura"), _.getString(0), expectedDataProcedura)
    testConstantColumn(dataFrame.select("giorno_gas"), _.getString(0), expectedGiornoGas)
    testConstantColumn(dataFrame.select("mese"), _.getString(0), expectedMese)
  }
}
