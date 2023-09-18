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
    organization := "net.davidwiles",
    scalafmtOnCompile := true,
    version := "0.1.0-SNAPSHOT",
  )
)

lazy val common = (project in file("sbt-installer"))
  .enablePlugins(SbtPlugin)
  .settings(
    publish := false,
    libraryDependencies += "org.apache.commons" % "commons-compress" % "1.21",
  )

lazy val jvm = (project in file("sbt-jvm-installer"))
  .enablePlugins(SbtPlugin)
  .dependsOn(common)
  .settings(
    moduleName := "sbt-installer",
    scriptedLaunchOpts ++= scriptedOpts.value,
    addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.4")
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
  .aggregate(common, jvm, js, native)
