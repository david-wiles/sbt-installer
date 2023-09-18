package net.davidwiles.sbt.installer

import sbt._
import net.davidwiles.sbt.installer.common.InstallerKeys

// Dummy plugin to trigger the key importer
object InstallerKeyImport extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends InstallerKeys

  import autoImport._

}
