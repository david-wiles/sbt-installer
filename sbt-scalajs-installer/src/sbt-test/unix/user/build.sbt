lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSInstallerPlugin)
  .settings(
    name := "unix-user-test",
    version := "0.1",
    scalaVersion := "2.12.15",
    scalaJSUseMainModuleInitializer := true,
    install / installLocation := new File("opt"),
    install / installExecutableLocation := new File("bin"),
  )
