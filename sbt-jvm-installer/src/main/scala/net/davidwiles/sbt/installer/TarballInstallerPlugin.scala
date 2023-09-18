package net.davidwiles.sbt.installer

import sbt._
import com.typesafe.sbt.packager.universal.UniversalPlugin
import net.davidwiles.sbt.installer.common.InstallerKeys
import net.davidwiles.sbt.installer.unix.TarballInstaller

object TarballInstallerPlugin extends AutoPlugin {

  override def requires: Plugins = UniversalPlugin

  override def trigger = noTrigger

  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq.empty

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inTask(InstallerKeys.install)(InstallerKeys.baseInstallSettings) ++ Seq(
      InstallerKeys.install := TarballInstaller(InstallerKeys.install).value
    )

}
