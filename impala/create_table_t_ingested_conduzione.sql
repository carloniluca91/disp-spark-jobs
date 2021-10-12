CREATE TABLE IF NOT EXISTS t_ingested_conduzione (

    codice_campo STRING,
    nome_campo STRING,
    tipo_aggiornamento STRING COMMENT "I (insert)|U (update)",
    data_riferimento TIMESTAMP,
    data_emissione TIMESTAMP,
    numero_versione INT,
    quantita_record_totali INT,
    valore_stoccaggio_corrente DOUBLE,
    valore_stoccaggio_totale DOUBLE,
    valore_iniettato DOUBLE,
    valore_previsione_distribuzione DOUBLE,
    valore_consumo DOUBLE,
    valore_pcs_stoccaggio DOUBLE,
    valore_pcs_rcp DOUBLE,
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