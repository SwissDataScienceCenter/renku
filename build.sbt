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
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)
