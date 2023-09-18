package net.davidwiles.sbt.installer.unix

import net.davidwiles.sbt.installer.common.{InstallerError, Tar}

import java.io.File
import java.nio.file.{Files, Path}
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import scala.util.Try

class Installer(logger: sbt.Logger) {

  /** Installs a packaged tarball from the output of universal/packageZipTarball
    *
    * @param name
    *   The name of the application to install
    * @param tarball
    *   The tarball file to install
    * @param destination
    *   Directory to copy installation files into
    * @param logger
    *   sbt.Logger corresponding to this task
    * @return
    *   The path to the executable file
    */
  def installTarball(name: String, tarball: File, destination: Path): Either[InstallerError, File] = {
    val result = for {
      output <- Tar(tarball).extract(destination)
      // link start script to path
    } yield output.toFile

    result
  }

}

object Installer {

  def apply(logger: sbt.Logger) = new Installer(logger)

}
