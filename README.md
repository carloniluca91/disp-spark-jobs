# Disp Spark Jobs

`Scala` project aggregating two subprojects

* one for implementing a set of streaming jobs that combine both
`Kafka` and `Spark` APIs in order to consume data from `Kafka` and write processed data on `Hive`
* one that implements a `Spark` job for monitoring and eventually merging
  small `parquet` files stored in `Hive` by previous subproject

Each streaming job consists of a consumer that
polls records from a topic, deserializes values of Kafka messages
(stored as `json` strings) as `POJOs`, convert such POJOs to `DataFrames` and
save them a table

Since data ingestion occurs frequently and stored data are usually small, each Hive table 
is monitored by the job defined in second subproject. If many small files 
(i.e., whose size is below a given threshold) are found, such job takes care of 
merging these files and overwrite them back

Both projects are a personal, free-time rework of a real life scenario
I had extensively worked on during my career ;)

Main modules

* `core` which defines base classes shared between both projects
* `streaming-data-model` which defines POJOs consumer by streaming jobs
* `streaming-core` which defines base classes for creating custom streaming jobs
* `streaming-app` which defines the application running the streaming jobs
* `file-merger-core` which defines base classes used by file merging application
* `file-merger-app` which defines the application running the file merging job

Some details

* written in `Scala 2.11.12`
* developed, tested and deployed on a `CDH 6.3.2` environment (`Spark 2.4.0`, `Kafka 2.2.1`)
* `impala` folder contains the SQL scripts for creating all Hive tables
* `oozie` folder contains all the configurations related to job submission and scheduling
