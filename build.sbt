organization := "ch.datascience"
name := "graph-core"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository

lazy val play_version = "2.5.14"

libraryDependencies += "com.typesafe.play" %% "play-json" % play_version

lazy val scalatest_version = "3.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % scalatest_version % Test

