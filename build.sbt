organization := "ch.datascience"
version := "0.0.1"
scalaVersion := "2.11.8"

resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.mavenLocal
)

val janusgraph_version = "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "org.janusgraph" % "janusgraph-core" % janusgraph_version,
  "com.h2database" % "h2" % "1.4.194",
  "org.slf4j" % "slf4j-nop" % "1.7.24",
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
)
