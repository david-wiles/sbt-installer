package net.davidwiles.sbt.installer.common

import java.nio.file.Path

trait InstallerError {

  val explanation: String

}

case class InvalidFileError(path: Path, detail: String) extends InstallerError {

  override val explanation: String = "Provided path is not a valid argument"

}

case class InsufficientPermissions(detail: String) extends InstallerError {

  override val explanation: String = "SBT process does not have sufficient permissions to complete"

}

case class CaughtException(throwable: Throwable) extends InstallerError {

  override val explanation: String = "An unexpected exception was thrown"

}
