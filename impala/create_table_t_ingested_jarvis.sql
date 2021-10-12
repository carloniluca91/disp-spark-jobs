CREATE TABLE IF NOT EXISTS t_ingested_jarvis (

    ambito_flusso STRING,
    nome_flusso STRING,
    impresa_mittente STRING,
    numero_dati INT,
    data_creazione TIMESTAMP,
    data_procedura STRING COMMENT "Pattern yyyy-MM-dd",
    ciclo_riferimento STRING,
    rinomina_energia DOUBLE,
    unita_misura_rinomina_energia STRING,
    limite_minimo_energia DOUBLE,
    unita_misura_limite_minimo_energia STRING,
    limite_massimo_energia DOUBLE,
    unita_misura_limite_massimo_energia STRING,
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