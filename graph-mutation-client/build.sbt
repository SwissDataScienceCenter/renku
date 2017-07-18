organization := "ch.datascience"
name := "graph-mutation-client"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.8"

lazy val root = Project(
  id   = "graph-mutation-client",
  base = file(".")
).dependsOn(
  core
)

lazy val core = RootProject(file("../graph-core"))

resolvers += DefaultMavenRepository
