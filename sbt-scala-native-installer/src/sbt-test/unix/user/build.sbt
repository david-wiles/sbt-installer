lazy val root = (project in file("."))
  .enablePlugins(ScalaNativePlugin, NativeInstallerPlugin)
  .settings(
    name := "unix-user-test",
    version := "0.1",
    scalaVersion := "2.12.15",
    install / installLocation := new File("opt"),
    install / installExecutableLocation := new File("bin"),
  )
