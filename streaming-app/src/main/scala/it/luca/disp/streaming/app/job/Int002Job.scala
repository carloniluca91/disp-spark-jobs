package it.luca.disp.streaming.app.job

import it.luca.disp.streaming.core.job.StreamingJob
import it.luca.disp.streaming.model.DatePattern
import it.luca.disp.streaming.model.int002.{Int002Ciclo, Int002Payload}
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.spark.sql.functions.{col, date_format, to_timestamp}
import org.apache.spark.sql.{Column, DataFrame, SaveMode, SparkSession}

import java.sql.Connection

class Int002Job(override protected val sparkSession: SparkSession,
                override protected val impalaConnection: Connection,
                override protected val properties: PropertiesConfiguration)
  extends StreamingJob[Int002Payload](sparkSession, impalaConnection, properties, classOf[Int002Payload]) {

  override protected val targetTable: String = properties.getString("int002.spark.target.table")
  override protected val saveMode: SaveMode = SaveMode.valueOf(properties.getString("spark.savemode.append"))

  override protected def toDataFrame(payload: Int002Payload): DataFrame = {

    val giornoOraRiferimentoCol: Column = to_timestamp(col("giornoOraRiferimento"), DatePattern.INT002_GIORNO_ORA_RIFERIMENTO)
    val giornoGasCol: Column = gasDayUdf(giornoOraRiferimentoCol)
    val meseCol: Column = date_format(giornoGasCol, DatePattern.DEFAULT_MONTH)

    sparkSession.createDataFrame(payload.getCicli, classOf[Int002Ciclo])
      .withColumn("giornoOraRiferimento", giornoOraRiferimentoCol)
      .withColumn("giorno_gas", giornoGasCol)
      .withColumn("mese", meseCol)
      .withColumnRenamed("giornoOraRiferimento", "giorno_ora_riferimento")
      .withColumnRenamed("udm1", "unita_misura_1")
      .withColumnRenamed("udm2", "unita_misura_2")
      .withColumnRenamed("udm3", "unita_misura_3")
      .withColumnRenamed("udm4", "unita_misura_4")
      .withColumnRenamed("valore1", "valore_1")
      .withColumnRenamed("progressivo1", "progressivo_1")
      .withColumnRenamed("valore2", "valore_2")
      .withColumnRenamed("progressivo2", "progressivo_2")
      .withColumnRenamed("valore3", "valore_3")
      .withColumnRenamed("progressivo3", "progressivo_3")
      .withColumnRenamed("valore4", "valore_4")
      .withColumnRenamed("progressivo4", "progressivo_4")
      .withColumnRenamed("pcs250", "pcs_250")
      .withColumnRenamed("wobbe2515", "wobbe_2515")
      .withColumnRenamed("wobbe250", "wobbe_250")
  }
}
