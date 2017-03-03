organization := "ch.datascience"
version := "0.0.1"
scalaVersion := "2.11.8"

resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.mavenLocal,
  "Local Maven Repository" at "" + Path.userHome.asFile.toURI.toURL + "/.m2/repository"
)


val janusgraph_version = "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.janusgraph" % "janusgraph-core" % janusgraph_version,
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
)