package net.davidwiles.sbt.installer

import sbt._
import net.davidwiles.sbt.installer.common.InstallerKeys
import net.davidwiles.sbt.installer.unix.ScalaJSInstaller
import org.scalajs.sbtplugin.ScalaJSPlugin

object ScalaJSInstallerPlugin extends AutoPlugin {

  override def requires: Plugins = ScalaJSPlugin

  override def trigger = noTrigger

  object autoImport extends InstallerKeys

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inTask(install)(baseInstallSettings) ++ Seq(
      install := ScalaJSInstaller(install).value
    )

}
