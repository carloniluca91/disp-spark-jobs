// Dependencies versions
val sparkVersion = "2.4.0-cdh6.3.2"
val kafkaVersion = "2.2.1-cdh6.3.2"
val scalaTestVersion = "3.2.0"
val scalaMockVersion = "5.1.0"
val scoptVersion = "4.0.0"
val lombokVersion = "1.18.10"

// Compile dependencies
lazy val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion % Provided
lazy val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion % Provided
lazy val kafkaClients = "org.apache.kafka" % "kafka-clients" % kafkaVersion % Provided
lazy val scopt = "com.github.scopt" %% "scopt" % scoptVersion
lazy val lombok = "org.projectlombok" % "lombok" % lombokVersion % Provided

// Test dependencies
lazy val scalacTic = "org.scalactic" %% "scalactic" % scalaTestVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
lazy val scalaMock = "org.scalamock" %% "scalamock" % scalaMockVersion % Test

// Common settings
lazy val commonSettings = Seq(
  organization := "it.luca",
  scalaVersion := "2.11.12",
  version := "0.1",

  // Java compiler options
  javacOptions ++= "-source" :: "1.8" ::
    "-target" :: "1.8" :: Nil,

  // Scala options
  scalacOptions ++= "-encoding" :: "UTF-8" ::
    "-target:jvm-1.8" ::
    "-feature" :: "-language:implicitConversions" :: Nil,

  // Compile Java sources first
  compileOrder := CompileOrder.JavaThenScala,

  // Cloudera Repo (for Spark and Kafka dependencies)
  resolvers += "Cloudera Repo" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
)

lazy val extensionsToExclude: Seq[String] = "properties" :: "json" :: "xml" :: "yaml" :: "yml" :: Nil

lazy val root = (project in file("."))
  .settings(
    name := "disp-spark-jobs")
  .aggregate(core, streamingDataModel, streamingApp)

lazy val streamingApp = (project in file("streaming-app"))
  .settings(
    name := "streaming-app",
    commonSettings,
    libraryDependencies ++= sparkCore :: sparkSql :: scopt :: lombok:: scalacTic :: scalaTest :: scalaMock :: Nil
  ).dependsOn(core, streamingDataModel)

lazy val streamingDataModel = (project in file("streaming-data-model"))
  .settings(
    name := "streaming-data-model",
    commonSettings,
    libraryDependencies ++= sparkCore :: lombok:: scalacTic :: scalaTest :: scalaMock :: Nil
  )

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++=
      sparkCore :: sparkSql :: lombok :: scopt :: scalacTic :: scalaTest :: scalaMock :: Nil
  )
