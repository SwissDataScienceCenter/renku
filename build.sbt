organization := "ch.datascience"
name := "graph-all"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

// This project contains nothing to package, like pure POM maven project
packagedArtifacts := Map.empty

lazy val core = Project(
  id   = "graph-core",
  base = file("graph-core")
).settings(
  commonSettings,
  scriptsSettings
)

lazy val typesystem = Project(
  id   = "graph-typesystem",
  base = file("graph-typesystem")
).settings(
  commonSettings,
  scriptsSettings
).dependsOn(core)

lazy val initProjects = taskKey[Unit]("Execute the init script")
lazy val updateProjects = taskKey[Unit]("Execute the pull script")

lazy val scriptsSettings = Seq(
  initProjects := {
    println(s"Calling: scripts/init.sh ${name.value} ${baseDirectory.value}")
    s"scripts/init.sh ${name.value} ${baseDirectory.value}" !
  }
, updateProjects := {
    println(s"Calling: scripts/pull.sh ${name.value} ${baseDirectory.value}")
    s"scripts/pull.sh ${name.value} ${baseDirectory.value}" !
  }
, updateProjects := (updateProjects dependsOn initProjects).value
, compile in Compile := ((compile in Compile) dependsOn initProjects).value
)

lazy val commonSettings = Seq(
  organization := "ch.datascience"
, scalaVersion := "2.11.8"
)
