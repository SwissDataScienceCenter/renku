organization := "ch.datascience"
name := "graph-type-ws"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers ++= Seq(
  DefaultMavenRepository,
  "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  filters,
  "com.typesafe.play" %% "play-slick" % "2.1.0",
  "ch.datascience" %% "graph-type-utils" % version.value,
  "ch.datascience" %% "graph-type-manager" % version.value,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)

lazy val initDB = taskKey[Unit]("Initialize database")

fullRunTask(initDB, Runtime, "init.Main")
