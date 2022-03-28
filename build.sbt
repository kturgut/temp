name := "avlino-housekeeping"
organization := "com.avlino"
ThisBuild / version := "2.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

Compile / scalacOptions ++= Seq(
  "-target:11",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")

Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
Test / parallelExecution := false
Test / testOptions += Tests.Argument("-oDF")
Test / logBuffered := false

run / fork := false
Global / cancelable := false // ctrl-c

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"
val AkkaManagementVersion = "1.1.2"
val AkkaPersistenceJdbcVersion = "5.0.4"
val AlpakkaKafkaVersion = "2.0.7"
val AkkaKafkaProjectionVersion = "1.2.3"
val AkkaProjectionVersion = "1.2.3"
val ScalikeJdbcVersion = "3.5.0"
val SparkVersion = "3.2.1"


dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot"
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
ThisBuild / dynverSeparator := "-"


lazy val managementSettings = Seq(
  // Akka Management powers Health Checks and Akka Cluster Bootstrapping
  "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % AkkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % AkkaManagementVersion,
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % AkkaManagementVersion,
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion
)


lazy val streamSettings = Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)


lazy val clusterSettings = Seq(
    // 1. Basic dependencies for a clustered application
    "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
    // the following needed for web house-keeping-web-client only
    // "com.typesafe.akka" %% "akka-cluster-tools" % AkkaVersion,
    // "com.typesafe.akka" %% "akka-multi-node-testkit" % AkkaVersion % Test,
)

lazy val commonSettings = clusterSettings ++ managementSettings ++ Seq(
    // Common dependencies for logging and testing
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.9",
    "org.scalatest" %% "scalatest" % "3.1.2" % Test,
)


lazy val kafkaSettings = Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
  "com.lightbend.akka" %% "akka-projection-kafka" % AkkaKafkaProjectionVersion
)

lazy val httpSettings = Seq(
  // Using gRPC and/or protobuf
  "com.typesafe.akka" %% "akka-http2-support" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

  // for grpc-web
  "ch.megard" %% "akka-http-cors" % "0.4.2"
)

lazy val sparkSettings = Seq(
  "org.apache.spark" %% "spark-sql" % SparkVersion
)


lazy val persistenceAndProjectionSettings = Seq(
    // Using Akka Persistence
    "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
    "com.lightbend.akka" %% "akka-persistence-jdbc" % AkkaPersistenceJdbcVersion,
    "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
    "org.postgresql" % "postgresql" % "42.2.18",
    // Querying or projecting data from Akka Persistence
    "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
    "com.lightbend.akka" %% "akka-projection-eventsourced" % AkkaProjectionVersion,
    "com.lightbend.akka" %% "akka-projection-jdbc" % AkkaProjectionVersion,
    "org.scalikejdbc" %% "scalikejdbc" % ScalikeJdbcVersion,
    "org.scalikejdbc" %% "scalikejdbc-config" % ScalikeJdbcVersion,
    "com.lightbend.akka" %% "akka-projection-testkit" % AkkaProjectionVersion % Test,
    // cassandra otionally to test
    "com.lightbend.akka" %% "akka-projection-cassandra" % "1.2.3"
)


//
lazy val `connector-n4-service` = project
  .settings(libraryDependencies ++= commonSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)

lazy val `house-keeping-analytics` = project
  .settings(libraryDependencies ++= commonSettings ++ streamSettings ++ persistenceAndProjectionSettings ++ kafkaSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)

lazy val `house-keeping-service` = project
  .settings(libraryDependencies ++= commonSettings ++ streamSettings ++ persistenceAndProjectionSettings ++ kafkaSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)


//import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
//import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

lazy val `house-keeping-web-client` = project
  .settings(libraryDependencies ++= commonSettings ++ streamSettings ++ persistenceAndProjectionSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)
//  .settings(multiJvmSettings: _*)
//  .configs(MultiJvm)

lazy val `dwell-analytics` = project
  .settings(libraryDependencies ++= commonSettings ++ streamSettings ++ persistenceAndProjectionSettings ++ kafkaSettings ++ sparkSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)


lazy val `ingest-navis-cdc` = project
  .settings(libraryDependencies ++= commonSettings ++ streamSettings ++ persistenceAndProjectionSettings ++ kafkaSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)

lazy val `service-bus-event-producer` = project
  .settings(libraryDependencies ++= commonSettings ++ kafkaSettings)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)

lazy val root = (project in file("."))
  .aggregate(`connector-n4-service`, `house-keeping-analytics`,`house-keeping-service`,
    `house-keeping-web-client`, `ingest-navis-cdc`, `service-bus-event-producer`, `dwell-analytics`)
  .settings(publish / skip := true)
  .enablePlugins(AkkaGrpcPlugin, JavaAppPackaging, DockerPlugin)
//  .enablePlugins(ScalaUnidocPlugin)
//  .disablePlugins(SitePlugin, MimaPlugin)

