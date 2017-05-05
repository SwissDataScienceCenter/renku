organization := "ch.datascience"
name := "graph-all"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.11.8"

packagedArtifacts := Map.empty

lazy val core = Project(
  id   = "graph-core",
  base = file("graph-core")
)

// Add initProjects task and make compile depend on it
lazy val initProjects = taskKey[Unit]("Execute the init script")
compile <<= (compile in Compile) dependsOn initProjects

initProjects := {
  "scripts/init.sh" !
}
