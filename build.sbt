organization := "ch.datascience"
name := "graph-typesystem-service"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository
//resolvers += "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val play_slick_version = "2.1.0"

libraryDependencies += filters
libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
//libraryDependencies += "ch.datascience" %% "graph-type-utils" % version.value
//libraryDependencies += "ch.datascience" %% "graph-type-manager" % version.value

lazy val scalatestplus_play_version = "2.0.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplus_play_version % Test

lazy val initDB = taskKey[Unit]("Initialize database")

fullRunTask(initDB, Runtime, "init.Main")
