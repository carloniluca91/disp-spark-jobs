CREATE TABLE IF NOT EXISTS t_ingested_webdisp (

    codice_remi STRING,
    descrizione_remi STRING,
    tipologia_punto STRING,
    descrizione_punto STRING,
    ciclo_nomina STRING,
    tipo_nomina STRING,
    data_ora_invio TIMESTAMP,
    data_elaborazione TIMESTAMP,
    data_decorrenza TIMESTAMP,
    valore_energia DOUBLE,
    unita_misura_energia STRING,
    valore_volume DOUBLE,
    unita_misura_volume STRING,
    pcs DOUBLE,
    giorno_gas STRING COMMENT "Pattern yyyy-MM-dd",
    record_offset BIGINT COMMENT "Consumer record's offset",
    record_topic STRING COMMENT "Consumer record's topic",
    record_partition INT COMMENT "Consumer record's partition",
    record_ts TIMESTAMP COMMENT "Consumer record's timestamp",
    record_dt STRING COMMENT "Consumer record's date (pattern yyyy-MM-dd)",
    application_id STRING COMMENT "Spark application's id",
    insert_ts TIMESTAMP COMMENT "Record's insert time",
    insert_dt STRING COMMENT "Record's insert date (pattern yyyy-MM-dd)"
)
PARTITIONED BY (mese STRING COMMENT "Pattern yyyy-MM")
STORED AS PARQUET