package net.davidwiles.sbt.installer.common

import sbt.*

trait InstallerKeys {

  lazy val install = taskKey[File]("Install the project on this machine")

  lazy val installExecutableName = settingKey[String]("Name of the executable file")

  lazy val installExecutableLocation = settingKey[File]("Parent directory for the installed executable file")

  lazy val installLocation = settingKey[File]("Parent directory for any installed files")

  lazy val baseInstallSettings: Seq[Def.Setting[_]] = Seq(
    installExecutableName     := sbt.Keys.name.value,
    installExecutableLocation := Environment.getDefaultExecutableRoot.toFile,
    installLocation           := Environment.getDefaultInstallRoot.toFile
  )

}

object InstallerKeys extends InstallerKeys
