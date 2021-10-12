CREATE TABLE IF NOT EXISTS t_file_merger_log (

	table_name STRING COMMENT "Name of processed table",
	is_partitioned BOOLEAN COMMENT "True if table is partitioned, false otherwise",
	partition_column STRING COMMENT "Partition column name (null if table is not partitioned)",
	partition_value STRING COMMENT "Partition column value (null if table is not partitioned)",
	operation_code STRING COMMENT "OK|KO",
	exception_cls STRING COMMENT "FQ class name of generated exception (if any)",
    exception_msg STRING COMMENT "Message of generated exception (if any)",
    application_id STRING COMMENT "Spark application's id",
    application_name STRING COMMENT "Spark application's name",
    application_start_time TIMESTAMP COMMENT "Spark application's start time",
    application_start_date STRING COMMENT "Spark application's start date (pattern yyyy-MM-dd)",
    yarn_application_log_ui_url STRING COMMENT "URL for viewing Yarn application's log",
    yarn_application_log_cmd STRING COMMENT "Command to execute for retrieving Yarn application's log from edge node",
    insert_ts TIMESTAMP COMMENT "Record's insert time",
    insert_dt STRING COMMENT "Record's insert date (pattern yyyy-MM-dd)"
)
PARTITIONED BY (month STRING COMMENT "Record's insert month (pattern yyyy-MM)")
STORED AS PARQUET