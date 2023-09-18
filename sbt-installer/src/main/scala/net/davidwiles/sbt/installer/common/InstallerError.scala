package net.davidwiles.sbt.installer.common

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait InstallerError {

  val explanation: String

}

case class InvalidFileError(path: Path, detail: String) extends InstallerError {

  override val explanation: String = s"Provided path $path is not a valid argument: $detail"

}

case class InsufficientPermissions(detail: String) extends InstallerError {

  override val explanation: String = s"SBT process does not have sufficient permissions to access: $detail"

}

case class CaughtException(throwable: Throwable) extends InstallerError {

  override val explanation: String = s"An unexpected exception was thrown: ${throwable.getMessage}"

}

object InstallerError {

  def apply(throwable: Throwable, detail: String = ""): InstallerError = throwable match {
    case e: SecurityException                   => InsufficientPermissions(detail)
    case e: java.nio.file.AccessDeniedException => InsufficientPermissions(e.getMessage)
    case e: Throwable                           => CaughtException(e)
  }

  def checkDirectory(path: Path): Either[InstallerError, Unit] = {
    for {
      _ <- Try(Files.exists(path)) match {
             case Failure(e)     => Left(InstallerError(e, path.toAbsolutePath.toString))
             case Success(false) => Left(InvalidFileError(path, "does not exist"))
             case Success(true)  => Right(())
           }
      _ <- Try(Files.isDirectory(path)) match {
             case Failure(e)     => Left(InstallerError(e, path.toAbsolutePath.toString))
             case Success(false) => Left(InvalidFileError(path, "is not a directory"))
             case Success(true)  => Right(())
           }
    } yield ()
  }

}

class InstallerFailure(val error: InstallerError) extends Exception(error.explanation) {

  override def getMessage: String = s"Fatal exception: ${error.explanation}"

}
