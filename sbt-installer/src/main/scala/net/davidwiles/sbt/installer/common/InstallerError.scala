package net.davidwiles.sbt.installer.common

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object InstallerError {

  trait InstallerError {

    val explanation: String

  }

  case class InvalidFileError(path: Path, detail: String) extends InstallerError {

    override val explanation: String = s"Provided path $path is not a valid argument: $detail"

  }

  case class InsufficientPermissions(detail: String) extends InstallerError {

    override val explanation: String = s"SBT process does not have sufficient permissions to access: $detail"

  }

  case class CaughtException(throwable: Throwable, detail: String) extends InstallerError {

    override val explanation: String = s"$detail. ${throwable.getClass.getName} was thrown: ${throwable.getMessage}"

  }

  class InstallerFailure(error: InstallerError) extends Exception(error.explanation) {

    override def getMessage: String = s"Fatal error: ${error.explanation}"

  }

  implicit class InstallerErrorFromTry[T](t: Try[T]) {

    def toError(detail: String): Either[InstallerError, T] = t match {
      case Failure(e) => Left(InstallerError(e, detail))
      case Success(s) => Right(s)
    }

  }

  def apply(throwable: Throwable, detail: String = ""): InstallerError = throwable match {
    case e: SecurityException                   => InsufficientPermissions(detail)
    case e: java.nio.file.AccessDeniedException => InsufficientPermissions(e.getMessage)
    case e: Throwable                           => CaughtException(e, detail)
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
