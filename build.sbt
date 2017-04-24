organization := "ch.datascience"
name := "graph-type-manager"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

resolvers ++= Seq(
  DefaultMavenRepository,
  "SDSC Snapshots" at "https://internal.datascience.ch:8081/nexus/content/repositories/snapshots/",
  "jitpack" at "https://jitpack.io",
  "Oracle Released Java Packages" at "http://download.oracle.com/maven"
)

val janusgraph_version = "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "ch.datascience" %% "graph-type-utils" % version.value,
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.janusgraph" % "janusgraph-core" % janusgraph_version,
  "org.janusgraph" % "janusgraph-berkeleyje" % janusgraph_version,
  "org.janusgraph" % "janusgraph-es" % janusgraph_version,
  "com.h2database" % "h2" % "1.4.193",
  "org.slf4j" % "slf4j-nop" % "1.7.24",

  "com.h2database" % "h2" % "1.4.193" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

logBuffered in Test := false
parallelExecution in Test := false
