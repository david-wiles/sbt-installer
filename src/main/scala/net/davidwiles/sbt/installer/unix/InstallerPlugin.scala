package net.davidwiles.sbt.installer.unix

import sbt._, Keys._
import sbtassembly.AssemblyKeys._

object InstallerPlugin extends AutoPlugin {
  override def requires: Plugins = sbtassembly.AssemblyPlugin

  override def trigger = allRequirements

  object autoImport extends {
    lazy val install = taskKey[Unit]("Install the project on this machine")
    lazy val installExecutable = settingKey[String]("Name of the executable file")
    lazy val installExecutableLocation = settingKey[File]("Parent directory for the installed executable file")
    lazy val installLocation = settingKey[File]("Location of the installed jar and configuration files")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    install := {
      val executable = (install / installExecutableLocation).value / (install / installExecutable).value
      Installer(executable, (install / installLocation).value, (assembly / assemblyOutputPath).value)
    },
    install / installExecutable := name.value,
    install / installExecutableLocation := new File("/usr/local/bin"),
    install / installLocation := new File(s"/opt/${(install / installExecutable).value}")
  )
}
