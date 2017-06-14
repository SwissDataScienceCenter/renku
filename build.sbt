organization := "ch.datascience"
name := "graph-mutation-service"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository

lazy val play_slick_version = "2.1.0"
lazy val rabbitmq_version = "4.1.0"
lazy val postgresql_version = "42.0.0"

libraryDependencies += filters
libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
//libraryDependencies += "com.rabbitmq" % "amqp-client" % rabbitmq_version
//libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.24"
libraryDependencies += "org.postgresql" % "postgresql" % postgresql_version

lazy val scalatestplus_play_version = "2.0.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplus_play_version % Test

lazy val janusgraph_version = "0.1.0"

libraryDependencies += "org.janusgraph" % "janusgraph-cassandra" % janusgraph_version //% Runtime

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "datascience.ch.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "datascience.ch.binders._"

// HTTP server port, will be ignored in production mode and use system settings or application.conf instead
//PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")

//resolvers ++= Seq(
//  DefaultMavenRepository,
//  "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/",
//  Resolver.mavenLocal
//)

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
