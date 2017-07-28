organization := "ch.datascience"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.11.8"

lazy val projectName = "deploy-service"
name := projectName
//lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val root = Project(
    id   = projectName,
    base = file(".")
).dependsOn(
  core,
  serviceCommons
).enablePlugins(
    PlayScala
)

lazy val core = RootProject(file("../graph-core"))
lazy val serviceCommons = RootProject(file("../service-commons"))

resolvers += DefaultMavenRepository

lazy val play_slick_version = "2.1.0"


libraryDependencies += filters
libraryDependencies += "com.typesafe.play" %% "play-slick" % play_slick_version
//libraryDependencies += cache
//libraryDependencies += ws
//libraryDependencies += filters
//libraryDependencies += "org.pac4j" % "play-pac4j" % "3.0.0-RC2"
//libraryDependencies += "org.pac4j" % "pac4j-jwt" % "2.0.0-RC2"
//libraryDependencies += "org.pac4j" % "pac4j-http" % "2.0.0-RC2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "io.fabric8" % "kubernetes-client" % "2.3.1"

libraryDependencies += "com.spotify" % "docker-client" % "8.8.1"

lazy val janusgraph_version = "0.1.0"
libraryDependencies += "org.janusgraph" % "janusgraph-cassandra" % janusgraph_version

import com.typesafe.sbt.packager.docker._

dockerBaseImage := "openjdk:8-jre-alpine"
//dockerBaseImage := "openjdk:8-jre"

dockerCommands ~= { cmds => cmds.head +: ExecCmd("RUN", "apk", "add", "--no-cache", "bash") +: cmds.tail }

// Root user to enable docker
dockerCommands ~= { cmds => cmds :+ Cmd("USER", "root") }
// Replace entry point
//dockerCommands ~= { cmds =>
//  cmds.map {
//    case ExecCmd("ENTRYPOINT", args@_*) => ExecCmd("ENTRYPOINT", args ++ Seq("-Dconfig.resource=application.docker.conf"): _*)
//    case cmd => cmd
//  }
//}

// Source code formatting
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

val preferences =
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference( AlignArguments,                               true  )
    .setPreference( AlignParameters,                              true  )
    .setPreference( AlignSingleLineCaseStatements,                true  )
    .setPreference( AlignSingleLineCaseStatements.MaxArrowIndent, 40    )
    .setPreference( CompactControlReadability,                    true  )
    .setPreference( CompactStringConcatenation,                   false )
    .setPreference( DanglingCloseParenthesis,                     Force )
    .setPreference( DoubleIndentConstructorArguments,             true  )
    .setPreference( DoubleIndentMethodDeclaration,                true  )
    .setPreference( FirstArgumentOnNewline,                       Force )
    .setPreference( FirstParameterOnNewline,                      Force )
    .setPreference( FormatXml,                                    true  )
    .setPreference( IndentPackageBlocks,                          true  )
    .setPreference( IndentSpaces,                                 2     )
    .setPreference( IndentWithTabs,                               false )
    .setPreference( MultilineScaladocCommentsStartOnFirstLine,    false )
    .setPreference( NewlineAtEndOfFile,                           true  )
    .setPreference( PlaceScaladocAsterisksBeneathSecondAsterisk,  false )
    .setPreference( PreserveSpaceBeforeArguments,                 false )
    .setPreference( RewriteArrowSymbols,                          false )
    .setPreference( SpaceBeforeColon,                             false )
    .setPreference( SpaceBeforeContextColon,                      true  )
    .setPreference( SpaceInsideBrackets,                          false )
    .setPreference( SpaceInsideParentheses,                       true  )
    .setPreference( SpacesAroundMultiImports,                     true  )
    .setPreference( SpacesWithinPatternBinders,                   false )

SbtScalariform.scalariformSettings ++ Seq(preferences)
