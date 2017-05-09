organization := "ch.datascience"
name := "graph-all"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

// This project contains nothing to package, like pure POM maven project
packagedArtifacts := Map.empty

lazy val root = Project(
  id   = "graph-all",
  base = file(".")
).aggregate(core, typesystem)

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
).aggregate(typesystemPersistence)

lazy val typesystemPersistence = Project(
  id   = "graph-typesystem-persistence",
  base = file("graph-typesystem") / "graph-typesystem-persistence"
).settings(
  commonSettings
).dependsOn(core)

lazy val updateProjects = taskKey[Unit]("Execute the update script")

lazy val scriptsSettings = Seq(
  updateProjects := {
    println(s"Calling: scripts/update.sh ${name.value}")
    s"scripts/update.sh ${name.value}" !
  }
, update := (update dependsOn updateProjects).value
)

lazy val commonSettings = Seq(
  organization := "ch.datascience"
, scalaVersion := "2.11.8"
)
