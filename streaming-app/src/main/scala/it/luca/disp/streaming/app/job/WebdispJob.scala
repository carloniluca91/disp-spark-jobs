package it.luca.disp.streaming.app.job

import it.luca.disp.core.implicits._
import it.luca.disp.streaming.app.utils.toTimestamp
import it.luca.disp.streaming.core.job.StreamingJob
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.webdisp.{WebdispNomina, WebdispPayload}
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.functions.{col, date_format, lit, to_timestamp}
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

import java.sql.Connection

class WebdispJob(override protected val sparkSession: SparkSession,
                 override protected val impalaConnection: Connection,
                 override protected val properties: PropertiesConfiguration)
  extends StreamingJob[WebdispPayload](sparkSession, impalaConnection, properties, classOf[WebdispPayload]) {

  override protected val targetTable: String = properties.getString("webdisp.spark.target.table")
  override protected val saveMode: SaveMode = SaveMode.valueOf(properties.getString("spark.saveMode.append"))

  override protected def toDataFrame(payload: WebdispPayload): DataFrame = {

    val dataset: DataFrame = sparkSession.createDataFrame(payload.getNomine, classOf[WebdispNomina])
      .coalesce(1)
      .withSqlNamingConvention()

    val dataOraInvioCol: Column = lit(toTimestamp(payload.getDataOraInvio, DatePattern.WEBDISP_DATA_ORA_INVIO))
    val dataElaborazioneCol: Column = to_timestamp(col("data_elaborazione"), DatePattern.WEBDISP_DATA_ELABORAZIONE)
    val dataDecorrenzaCol: Column = to_timestamp(col("data_decorrenza"), DatePattern.WEBDISP_DATA_DECORRENZA)
    val giornoGasCol: Column = gasDayUdf(col("data_decorrenza"))
    val meseCol: Column = date_format(giornoGasCol, DatePattern.DEFAULT_MONTH)

    dataset
      .withColumn("data_ora_invio", dataOraInvioCol)
      .withColumn("data_elaborazione", dataElaborazioneCol)
      .withColumn("data_decorrenza", dataDecorrenzaCol)
      .withColumn("giorno_gas", giornoGasCol)
      .withColumn("mese", meseCol)
  }
}
