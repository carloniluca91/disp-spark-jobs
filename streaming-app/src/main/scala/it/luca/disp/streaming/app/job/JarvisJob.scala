package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.app.utils.{formatDate, toTimestamp}
import it.luca.disp.streaming.core.job.StreamingJob
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.jarvis.{JarvisCiclo, JarvisPayload}
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

import java.sql.Connection

class JarvisJob(override protected val sparkSession: SparkSession,
                override protected val impalaConnection: Connection,
                override protected val properties: PropertiesConfiguration)
  extends StreamingJob[JarvisPayload](sparkSession, impalaConnection, properties, classOf[JarvisPayload]) {

  override protected val targetTable: String = properties.getString("jarvis.spark.target.table")
  override protected val saveMode: SaveMode = SaveMode.valueOf(properties.getString("spark.saveMode.append"))

  override protected def toDataFrame(payload: JarvisPayload): DataFrame = {

    val dataFrame: DataFrame = sparkSession.createDataFrame(payload.getCicli, classOf[JarvisCiclo])
      .coalesce(1)
      .withColumnRenamed("cicloDiRiferimento", "ciclo_riferimento")
      .withColumnRenamed("rinominaEnergia", "rinomina_energia")
      .withColumnRenamed("unitaDiMisuraRinominaEnergia", "unita_misura_rinomina_energia")
      .withColumnRenamed("limiteMinimoEnergia", "limite_minimo_energia")
      .withColumnRenamed("unitaDiMisuraLimiteMinimoEnergia", "unita_misura_limite_minimo_energia")
      .withColumnRenamed("limiteMassimoEnergia", "limite_massimo_energia")
      .withColumnRenamed("unitaDiMisuraLimiteMassimoEnergia", "unita_misura_limite_massimo_energia")

    val ambitoFlussoCol: Column = lit(payload.getAmbitoFlusso)
    val nomeFlussoCol: Column = lit(payload.getNomeFlusso)
    val impresaMittenteCol: Column = lit(payload.getImpresaMittente)
    val numeroDatiCol: Column = lit(payload.getNumeroDati)
    val dataDiCreazioneCol: Column = lit(toTimestamp(payload.getDataDiCreazione, DatePattern.JARVIS_DATA_DI_CREAZIONE))
    val dataProceduraCol: Column = lit(formatDate(payload.getDataProcedura, DatePattern.JARVIS_DATA_PROCEDURA, DatePattern.DEFAULT_DATE))
    val giornoGasCol: Column = lit(formatDate(payload.getGiornoGas, DatePattern.JARVIS_GIORNO_GAS, DatePattern.DEFAULT_DATE))
    val meseCol: Column = lit(formatDate(payload.getGiornoGas, DatePattern.JARVIS_GIORNO_GAS, DatePattern.DEFAULT_MONTH))

    dataFrame
      .withColumn("ambito_flusso", ambitoFlussoCol)
      .withColumn("nome_flusso", nomeFlussoCol)
      .withColumn("impresa_mittente", impresaMittenteCol)
      .withColumn("numero_dati", numeroDatiCol)
      .withColumn("data_creazione", dataDiCreazioneCol)
      .withColumn("data_procedura", dataProceduraCol)
      .withColumn("giorno_gas", giornoGasCol)
      .withColumn("mese", meseCol)
  }
}
