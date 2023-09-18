package net.davidwiles.sbt.installer

import sbt.*
import Keys.*
import net.davidwiles.sbt.installer.unix.Installer
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.*
import net.davidwiles.sbt.installer.common.Environment

object InstallerPlugin extends AutoPlugin {

  override def requires: Plugins = UniversalPlugin

  override def trigger = allRequirements

  object autoImport extends {

    lazy val install                   = taskKey[Unit]("Install the project on this machine")

    lazy val installExecutable         = settingKey[String]("Name of the executable file")

    lazy val installExecutableLocation = settingKey[File]("Parent directory for the installed executable file")

    lazy val installLocation           = settingKey[File]("Location of the installed jar and configuration files")

  }

  import autoImport._

  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq.empty

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    install := {
      val installer = Installer((install / streams).value.log)
      installer.installTarball(name.value, (Universal / packageZipTarball).value, Environment.defaults.installDirectory)
    },
    install / installExecutable         := name.value,
    install / installExecutableLocation := new File("/usr/local/bin"),
    install / installLocation           := new File(s"/opt/${(install / installExecutable).value}")
  )

}
