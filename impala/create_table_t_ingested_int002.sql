CREATE TABLE IF NOT EXISTS t_ingested_int002 (

    giorno_ora_riferimento TIMESTAMP,
    valore_1 DOUBLE,
    progressivo_1 DOUBLE,
    unita_misura_1 STRING,
    valore_2 DOUBLE,
    progressivo_2 DOUBLE,
    unita_misura_2 STRING,
    valore_3 DOUBLE,
    progressivo_3 DOUBLE,
    unita_misura_3 STRING,
    valore_4 DOUBLE,
    progressivo_4 DOUBLE,
    unita_misura_4 STRING,
    pcs_250 DOUBLE,
    wobbe_2515 DOUBLE,
    wobbe_250 DOUBLE,
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