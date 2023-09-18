package net.davidwiles.sbt.installer.unix

class UberJarInstaller(logger: sbt.Logger) {}

object UberJarInstaller {

  def apply(logger: sbt.Logger) = new UberJarInstaller(logger)

}
