/// Modified version of automatically generated build configs from sbt new playframework/play-scala-seed.g8
///
name := """graph-wal"""
organization := "ch.datascience"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= Seq(
  //"com.typesafe.slick" %% "slick" % "3.2.0",
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0",
  "com.rabbitmq" % "amqp-client" % "4.1.0",
  "org.slf4j" % "slf4j-nop" % "1.7.24",
  "org.postgresql" % "postgresql" % "42.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "datascience.ch.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "datascience.ch.binders._"

// HTTP server port, will be ignored in production mode and use system settings or application.conf instead
PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")

resolvers ++= Seq(
  DefaultMavenRepository,
  "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/",
  Resolver.mavenLocal
)
