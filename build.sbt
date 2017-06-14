organization := "ch.datascience"
name := "graph-typesystem-service"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository
//resolvers += "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val play_slick_version = "2.1.0"
lazy val postgresql_version = "42.0.0"

libraryDependencies += filters
libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
//libraryDependencies += "ch.datascience" %% "graph-type-utils" % version.value
//libraryDependencies += "ch.datascience" %% "graph-type-manager" % version.value
libraryDependencies += "org.postgresql" % "postgresql" % postgresql_version

// Runtime dependencies (runtime removed to load them when sbt console; I am too lazy to redefine console)
lazy val h2_version = "1.4.193"
lazy val janusgraph_version = "0.1.0"

libraryDependencies += "org.janusgraph" % "janusgraph-cassandra" % janusgraph_version //% Runtime
//libraryDependencies += "com.h2database" % "h2" % h2_version //% Runtime

lazy val scalatestplus_play_version = "2.0.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplus_play_version % Test

lazy val initDB = taskKey[Unit]("Initialize database")

fullRunTask(initDB, Runtime, "init.Main")

import com.typesafe.sbt.packager.docker._

// Allows for alpine images
//enablePlugins(AshScriptPlugin)

dockerBaseImage := "openjdk:8-jre-alpine"
//dockerBaseImage := "openjdk:8-jre"

dockerCommands ~= { cmds => cmds.head +: ExecCmd("RUN", "apk", "add", "--no-cache", "bash") +: cmds.tail }
// Replace entry point
dockerCommands ~= { cmds =>
  cmds.map {
    case ExecCmd("ENTRYPOINT", args@_*) => ExecCmd("ENTRYPOINT", args ++ Seq("-Dconfig.resource=application.docker.conf"): _*)
    case cmd => cmd
  }
}
