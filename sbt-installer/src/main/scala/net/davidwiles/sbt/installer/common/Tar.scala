package net.davidwiles.sbt.installer.common

import net.davidwiles.sbt.installer.common.InstallerError._
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class Tar(tarball: File) {

  type TarArchiveStreamEntry = (TarArchiveInputStream, TarArchiveEntry)

  case class UnsuccessfulUntarError(throwable: Throwable) extends InstallerError {

    override val explanation: String = "Unable to extract tar file"

  }

  def entries: Iterator[TarArchiveStreamEntry] =
    Try {
      new TarArchiveInputStream(
        new GzipCompressorInputStream(
          new BufferedInputStream(
            new FileInputStream(
              tarball
            )
          )
        )
      )
    }.map { stream =>
      Iterator
        .continually(stream.getNextTarEntry)
        .takeWhile(_ != null)
        .map(entry => (stream, entry))
    }.getOrElse(Iterator.empty)

  /** Extract the tarball to the given location, preserving the directory structure present in the archive file. If `to`
    * is /ab/c, and the tarball is named d, then the directory /ab/c/d will be created an all files will be extracted
    * into that location.
    *
    * @param to
    *   The directory which the tarball will be extracted into. Must be a directory
    * @return
    *   The root directory for the archive
    */
  def extract(to: Path): Either[InstallerError, Path] = {
    Try(to.toFile.isDirectory) match {
      case Failure(se: SecurityException) => Left(InsufficientPermissions(s"Unable to open ${to.toAbsolutePath}"))
      case Failure(ex: Throwable) =>
        Left(CaughtException(ex, s"Unable to verify if ${to.toAbsolutePath} is a directory"))
      case Success(false) => Left(InvalidFileError(to, s"${to.toAbsolutePath} must be a directory"))
      case Success(true) =>
        entries.map { case (stream, entry) =>
          Try {
            val output = to.resolve(entry.getName)
            if (entry.isDirectory) {
              Files.createDirectories(output)
            } else {
              Files.createDirectories(output.getParent)
              IOUtils.copy(stream, new FileOutputStream(output.toFile))
            }
          }
        }.collectFirst { case Failure(exception) => exception }
          .map(ex => Left(UnsuccessfulUntarError(ex)))
          .getOrElse(Right(to.resolve(trimExtension(tarball.getName))))
    }
  }

  // Remove the extension from the filename by removing the section of the filename after the last dot
  private def trimExtension(filename: String): String = {
    val lastDot = filename.lastIndexOf('.')
    if (lastDot > 0 && lastDot < (filename.length - 1))
      filename.substring(0, lastDot)
    else
      filename
  }

}

object Tar {

  def apply(tarball: File) = new Tar(tarball: File)

}
