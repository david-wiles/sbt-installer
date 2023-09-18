package net.davidwiles.sbt.installer

import sbt.*
import Keys.*
import net.davidwiles.sbt.installer.unix.TarballInstaller
import com.typesafe.sbt.packager.universal.UniversalPlugin
import net.davidwiles.sbt.installer.common.Environment
import net.davidwiles.sbt.installer.common.InstallerKeys

object InstallerPlugin extends AutoPlugin {

  override def requires: Plugins = UniversalPlugin

  override def trigger = allRequirements

  object autoImport extends InstallerKeys

  import autoImport._

  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq.empty

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseInstallerSettings

  lazy val baseInstallerSettings: Seq[Def.Setting[_]] = Seq(
    install                             := TarballInstaller(install).value,
    install / installExecutableName     := name.value,
    install / installExecutableLocation := Environment.getDefaultExecutableRoot.toFile,
    install / installLocation           := Environment.getDefaultInstallRoot.toFile
  )

}
