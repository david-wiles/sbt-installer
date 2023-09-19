package net.davidwiles.sbt.installer.unix

import net.davidwiles.sbt.installer.common.InstallerError._
import sbt._
import sbt.Keys._
import sbt.Def.Initialize
import net.davidwiles.sbt.installer.common.InstallerKeys._
import scala.util.Try
import java.nio.file.Files
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.nativeLink

object NativeInstaller {

  def apply(task: TaskKey[File]): Initialize[Task[File]] = Def.task {
    installNative(
      name = (task / installExecutableName).value,
      version = version.value,
      binary = (Compile / nativeLink).value,
      exeRoot = (task / installExecutableLocation).value,
      installRoot = (task / installLocation).value,
      logger = (task / streams).value.log
    )
  }

  /** Install a native executable to this machine
    * @param name
    *   Name of the executable file
    * @param version
    *   Version of this build
    * @param binary
    *   Compiled binary file from scala-native plugin's nativeLink
    * @param installRoot
    *   Root directory to place the binary file
    * @param exeRoot
    *   Root directory to place the executable symlink file
    * @param logger
    *   Logger for this task
    * @return
    *   Path to the executable file which was installed
    */
  private def installNative(
      name: String,
      version: String,
      binary: File,
      installRoot: File,
      exeRoot: File,
      logger: sbt.Logger
  ): File = {
    val destination = installRoot.toPath.resolve(s"$name-$version").toAbsolutePath
    val link        = exeRoot.toPath.resolve(name)
    val result = for {
      _ <- checkDirectory(exeRoot.toPath)
      _ <- checkDirectory(installRoot.toPath)
      _ <- Try(Files.copy(binary.toPath, destination, COPY_ATTRIBUTES, REPLACE_EXISTING)).map { _ =>
             logger.log(Level.Debug, s"Copied ${binary.toPath} to $destination")
           }.toError(s"Failed to copy ${binary.toPath} to $destination")
      executable <- Try(Files.createSymbolicLink(link, destination)).map { exe =>
                      logger.log(Level.Debug, s"Created symlink $exe")
                      exe
                    }.toError(s"Failed to create symlink $link")
    } yield executable

    result.fold(
      err => throw new InstallerFailure(err),
      f => f.toFile
    )
  }

}
