name := """deployer-service"""
organization := "ch.datascience"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val play_slick_version = "2.1.0"

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % play_slick_version,
  "ch.datascience" %% "graph-core" % version.value,
  //"ch.datascience" %% "graph-type-utils" % version.value,
  //"ch.datascience" %% "graph-type-manager" % version.value,
  cache,
  ws,
  "org.pac4j" % "play-pac4j" % "3.0.0-RC2",
  "org.pac4j" % "pac4j-jwt" % "2.0.0-RC2",
  "org.pac4j" % "pac4j-http" % "2.0.0-RC2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)

resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.mavenLocal
)
