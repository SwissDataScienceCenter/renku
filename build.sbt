name := """graph-wal"""
organization := "ch.datascience"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.rabittmq" %% "amqp-client" % "4.1.0",
  "org.slf4j" % "slf4j-nop" % "1.7.24",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)

resolvers ++= Seq(
  DefaultMavenRepository,
  "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/",
  Resolver.mavenLocal
)
