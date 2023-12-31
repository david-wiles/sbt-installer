val scriptedOpts = Def.setting(
  Seq(
    "-Xmx1024M",
    "-XX:ReservedCodeCacheSize=256m",
    "-Dplugin.version=" + (ThisBuild / version).value
  )
)

inThisBuild(
  Def.settings(
    scalaVersion := "2.12.18",
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-feature",
      "-encoding",
      "utf8"
    ),
    homepage := Some(url("https://github.com/david-wiles/sbt-installer")),
    isSnapshot := Option(System.getProperty("packaging.isSnapshot")).getOrElse("true").toBoolean,
    organization := "net.davidwiles",
    developers := List(
      Developer(
        id = "david-wiles",
        name = "David Wiles",
        email = "me@davidwiles.net",
        url = url("https://www.davidwiles.net")
      )
    ),
    scalafmtOnCompile := true,
    pomIncludeRepository := { _ => false },
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/david-wiles/sbt-installer"),
        "git@github.com:david-wiles/sbt-installer.git"
      )
    ),
    version := {
      val old = (ThisBuild / version).value
      if ((ThisBuild / isSnapshot).value) s"$old-SNAPSHOT"
      else old
    },
    licenses := Seq("Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    Test / publishArtifact := false,
  )
)

lazy val common = (project in file("sbt-installer"))
  .enablePlugins(SbtPlugin)
  .settings(
    moduleName := "sbt-installer-common",
    libraryDependencies += "org.apache.commons" % "commons-compress" % "1.21",
  )

lazy val jvm = (project in file("sbt-jvm-installer"))
  .enablePlugins(SbtPlugin)
  .dependsOn(common)
  .settings(
    moduleName := "sbt-installer",
    scriptedLaunchOpts ++= scriptedOpts.value,
    addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.4"),
    addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1")
  )

lazy val js = (project in file("sbt-scalajs-installer"))
  .enablePlugins(SbtPlugin)
  .dependsOn(common)
  .settings(
    moduleName := "sbt-scalajs-installer",
    scriptedLaunchOpts ++= scriptedOpts.value,
    addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.2")
  )

lazy val native = (project in file("sbt-scala-native-installer"))
  .enablePlugins(SbtPlugin)
  .dependsOn(common)
  .settings(
    moduleName := "sbt-native-installer",
    scriptedLaunchOpts ++= scriptedOpts.value,
    addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.15")
  )

lazy val root = (project in file("."))
  .settings(pomConsistency2021DraftSettings)
  .aggregate(common, jvm, js, native)

// See https://eed3si9n.com/pom-consistency-for-sbt-plugins
lazy val pomConsistency2021Draft = settingKey[Boolean]("experimental")

/**
 * this is an unofficial experiment to re-publish plugins with better Maven compatibility
 */
def pomConsistency2021DraftSettings: Seq[Setting[_]] = Seq(
  pomConsistency2021Draft := Set("true", "1")(sys.env.getOrElse("POM_CONSISTENCY", "false")),
  moduleName := {
    if (pomConsistency2021Draft.value)
      sbtPluginModuleName2021Draft(moduleName.value,
        (pluginCrossBuild / sbtBinaryVersion).value)
    else moduleName.value
  },
  projectID := {
    if (pomConsistency2021Draft.value) sbtPluginExtra2021Draft(projectID.value)
    else projectID.value
  },
)

def sbtPluginModuleName2021Draft(n: String, sbtV: String): String =
  s"""${n}_sbt${if (sbtV == "1.0") "1" else if (sbtV == "2.0") "2" else sbtV}"""

def sbtPluginExtra2021Draft(m: ModuleID): ModuleID =
  m.withExtraAttributes(Map.empty)
    .withCrossVersion(CrossVersion.binary)
