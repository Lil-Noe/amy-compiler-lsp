version := "1.7"
organization := "ch.epfl.lara"
scalaVersion := "3.5.2"
assembly / test := {}
name := "amyc"

Compile / scalaSource := baseDirectory.value / "src"
scalacOptions ++= Seq("-feature")

Test / scalaSource := baseDirectory.value / "test" / "scala"
Test / parallelExecution := false
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4" % "test"
libraryDependencies += "org.eclipse.lsp4j" % "org.eclipse.lsp4j" % "0.21.1"
testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

assembly / mainClass := Some("amyc.Main")

// assembly / assemblyMergeStrategy  := {
//   {
//     case _ => MergeStrategy.first
//   }
// }
