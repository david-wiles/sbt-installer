lazy val root = (project in file("."))
  .enablePlugins(InstallerPlugin)
  .settings(
    name := "unix-user-test",
    version := "0.1",
    scalaVersion := "2.12.15",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.29" % "runtime",
    assembly / assemblyJarName := "foo.jar",
    install / installLocation := new File("opt"),
    install / installExecutableLocation := new File("bin"),
  )
