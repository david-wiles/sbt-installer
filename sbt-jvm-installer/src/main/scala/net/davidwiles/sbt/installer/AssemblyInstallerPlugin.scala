package net.davidwiles.sbt.installer

import sbt._
import net.davidwiles.sbt.installer.common.InstallerKeys
import net.davidwiles.sbt.installer.unix.AssemblyInstaller
import sbtassembly.AssemblyKeys.assembly

object AssemblyInstallerPlugin extends AutoPlugin {

  override def requires: Plugins = sbtassembly.AssemblyPlugin

  override def trigger = noTrigger

  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq.empty

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inTask(InstallerKeys.install)(InstallerKeys.baseInstallSettings) ++ Seq(
      InstallerKeys.install := AssemblyInstaller(InstallerKeys.install).value,
      InstallerKeys.install := InstallerKeys.install.dependsOn(sbtassembly.Assembly.assemblyTask(assembly)).value
    )

}
