name := """deployer-service"""
organization := "ch.datascience"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val play_slick_version = "2.1.0"

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % play_slick_version,
  "ch.datascience" %% "graph-core" % "0.0.1-SNAPSHOT",
  cache,
  ws,
  filters,
  "org.pac4j" % "play-pac4j" % "3.0.0-RC2",
  "org.pac4j" % "pac4j-jwt" % "2.0.0-RC2",
  "org.pac4j" % "pac4j-http" % "2.0.0-RC2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
  "io.fabric8" % "kubernetes-client" % "2.3.1"
)

resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.mavenLocal
)

import com.typesafe.sbt.packager.docker._

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