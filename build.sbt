import sbtassembly.{AssemblyOption, MergeStrategy}

// Dependencies versions
val sparkVersion = "2.4.0-cdh6.3.2"
val kafkaVersion = "2.2.1-cdh6.3.2"
val scalaTestVersion = "3.2.0"
val scalaMockVersion = "5.1.0"
val scoptVersion = "4.0.0"
val lombokVersion = "1.18.10"

// Compile dependencies
lazy val sparkCore: ModuleID = "org.apache.spark" %% "spark-core" % sparkVersion % Provided
lazy val sparkSql: ModuleID  = "org.apache.spark" %% "spark-sql" % sparkVersion % Provided
lazy val kafkaClients: ModuleID = "org.apache.kafka" % "kafka-clients" % kafkaVersion % Provided
lazy val scopt: ModuleID = "com.github.scopt" %% "scopt" % scoptVersion
lazy val lombok: ModuleID = "org.projectlombok" % "lombok" % lombokVersion % Provided

// Test dependencies
lazy val scalacTic: ModuleID = "org.scalactic" %% "scalactic" % scalaTestVersion
lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % scalaTestVersion % Test
lazy val scalaMock: ModuleID = "org.scalamock" %% "scalamock" % scalaMockVersion % Test

// Common settings
lazy val commonSettings = Seq(
  organization := "it.luca",
  scalaVersion := "2.11.12",
  version := "5.0",

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

// Exclude all resources related to extensions to exclude
lazy val resourcesExtensions: Seq[String] = "properties" :: "json" :: "xml" :: "yaml" :: "yml" :: Nil
lazy val excludeResources: Setting[Task[Seq[File]]] = (Compile / unmanagedResources) := (Compile / unmanagedResources).value
  .filterNot(x => resourcesExtensions.map {
    extension => x.getName.endsWith(s".$extension")
  }.reduce(_ || _))

// Assembly settings: exclude Scala library and MergeStrategy
lazy val excludeScalaLibrary: Setting[Task[AssemblyOption]] = assembly / assemblyOption := (assemblyOption in assembly)
  .value.copy(includeScala = false)

lazy val mergeStrategy: Setting[String => MergeStrategy] = assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x) }

lazy val root = (project in file("."))
  .settings(
    name := "disp-spark-jobs")
  .aggregate(streamingApp, fileMergerApp)

// Streaming app
lazy val streamingApp = (project in file("streaming-app"))
  .settings(
    name := "streaming-app",
    commonSettings,
    libraryDependencies ++= sparkCore ::
      sparkSql ::
      kafkaClients ::
      scopt ::
      lombok::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil,

    excludeScalaLibrary,
    excludeResources,
    assembly / assemblyJarName := "disp-spark-streaming.jar",
    mergeStrategy
  ).dependsOn(streamingCore % "test->test;compile->compile")
  .aggregate(core, streamingDataModel, streamingCore)

lazy val streamingCore = (project in file("streaming-core"))
  .settings(
    name := "streaming-core",
    commonSettings,
    libraryDependencies ++= sparkCore ::
      sparkSql ::
      kafkaClients ::
      lombok::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil
  ).dependsOn(
  core % "test->test;compile->compile",
  streamingDataModel)

lazy val streamingDataModel = (project in file("streaming-data-model"))
  .settings(
    name := "streaming-data-model",
    commonSettings,
    libraryDependencies ++= sparkCore ::
      lombok::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil
  )

// File merger app

lazy val fileMergerApp = (project in file("file-merger-app"))
  .settings(
    name := "file-merger-app",
    commonSettings,
    libraryDependencies ++= sparkCore ::
      sparkSql ::
      scopt ::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil,

    excludeScalaLibrary,
    excludeResources,
    assembly / assemblyJarName := "disp-file-merger.jar",
    mergeStrategy
  ).dependsOn(core % "test->test;compile->compile", fileMergerCore)
  .aggregate(core, fileMergerCore)

lazy val fileMergerCore = (project in file("file-merger-core"))
  .settings(
    name := "file-merger-core",
    commonSettings,
    libraryDependencies ++= sparkCore ::
      sparkSql ::
      scopt ::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil
  ).dependsOn(core % "test->test;compile->compile")

lazy val core = (project in file("core"))
  .settings(
    commonSettings,
    libraryDependencies ++= sparkCore ::
      sparkSql ::
      lombok ::
      scopt ::
      scalacTic ::
      scalaTest ::
      scalaMock :: Nil
  )
