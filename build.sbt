organization := "ch.datascience"
name := "graph-init"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

//lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.11.8"

resolvers += DefaultMavenRepository

//lazy val play_slick_version = "2.1.0"
//
//libraryDependencies += filters
//libraryDependencies += cache
//libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
//
//lazy val scalatestplus_play_version = "2.0.0"
//
//libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % scalatestplus_play_version % Test
//
//lazy val janusgraph_version = "0.1.0"
//
//libraryDependencies += "org.janusgraph" % "janusgraph-cassandra" % janusgraph_version //% Runtime
//
import com.typesafe.sbt.packager.docker._

dockerBaseImage := "openjdk:8-jre-alpine"
////dockerBaseImage := "openjdk:8-jre"
//
dockerCommands ~= { cmds => cmds.head +: ExecCmd("RUN", "apk", "add", "--no-cache", "bash") +: cmds.tail }
//// Replace entry point
//dockerCommands ~= { cmds =>
//  cmds.map {
//    case ExecCmd("ENTRYPOINT", args@_*) => ExecCmd("ENTRYPOINT", args ++ Seq("-Dconfig.resource=application.docker.conf"): _*)
//    case cmd => cmd
//  }
//}
