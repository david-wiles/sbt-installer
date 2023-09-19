package net.davidwiles.sbt.installer

import sbt._
import net.davidwiles.sbt.installer.common.InstallerKeys
import net.davidwiles.sbt.installer.unix.NativeInstaller

import scala.scalanative.sbtplugin.ScalaNativePlugin

object NativeInstallerPlugin extends AutoPlugin {

  override def requires: Plugins = ScalaNativePlugin

  override def trigger = noTrigger

  object autoImport extends InstallerKeys

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inTask(install)(baseInstallSettings) ++ Seq(
      install := NativeInstaller(install).value
    )

}
