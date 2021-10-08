package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.core.job.StreamingJob
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.conduzione.{ConduzionePayload, ConduzioneRecord}
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.functions.{col, date_format, to_timestamp}
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

import java.sql.Connection

class ConduzioneJob(override protected val sparkSession: SparkSession,
                    override protected val impalaConnection: Connection,
                    override protected val properties: PropertiesConfiguration)
  extends StreamingJob[ConduzionePayload](sparkSession, impalaConnection, properties, classOf[ConduzionePayload]) {

  override protected val targetTable: String = properties.getString("conduzione.spark.target.table")
  override protected val saveMode: SaveMode = SaveMode.valueOf(properties.getString("spark.saveMode.append"))

  override protected def toDataFrame(payload: ConduzionePayload): DataFrame = {

    val dataRiferimentoCol: Column = to_timestamp(col("drif"), DatePattern.CONDUZIONE_DRIF)
    val dataEmissioneCol: Column = to_timestamp(col("dre"), DatePattern.CONDUZIONE_DRE)
    val giornoGasCol: Column = gasDayUdf(dataRiferimentoCol)
    val meseCol: Column = date_format(giornoGasCol, DatePattern.DEFAULT_MONTH)

    sparkSession.createDataFrame(payload.getRecords, classOf[ConduzioneRecord])
      .coalesce(1)
      .withColumn("data_riferimento", dataRiferimentoCol)
      .withColumn("data_emissione", dataEmissioneCol)
      .withColumn("giorno_gas", giornoGasCol)
      .withColumn("mese", meseCol)
      .withColumnRenamed("vstockCor", "valore_stoccaggio_corrente")
      .withColumnRenamed("vprdn", "valore_previsione_distribuzione")
      .withColumnRenamed("viniet", "valore_iniettato")
      .withColumnRenamed("vpcsStocg", "valore_pcs_stoccaggio")
      .withColumnRenamed("vpcsRcp", "valore_pcs_rcp")
      .withColumnRenamed("pver", "numero_versione")
      .withColumnRenamed("qrecTot", "quantita_record_totali")
      .withColumnRenamed("tipoAggiornamento", "tipo_aggiornamento")
      .withColumnRenamed("ccmp", "codice_campo")
      .withColumnRenamed("ncmp", "nome_campo")
      .withColumnRenamed("vcnsm", "valore_consumo")
      .withColumnRenamed("vstockTot", "valore_stoccaggio_totale")
  }
}
