ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "net.davidwiles"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    version := "0.1.0-SNAPSHOT",
    name := "sbt-installer",
    libraryDependencies += "com.eed3si9n" %% "sbt-assembly_sbt1" % "2.1.1",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.2.8" // set minimum sbt version
      }
    }
  )
