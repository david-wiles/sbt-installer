package net.davidwiles.sbt.installer.unix

import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.*
import net.davidwiles.sbt.installer.common.InstallerError
import net.davidwiles.sbt.installer.common.InstallerFailure
import net.davidwiles.sbt.installer.common.Tar
import net.davidwiles.sbt.installer.common.InstallerKeys.*
import sbt.Def
import sbt.File
import sbt.Task
import sbt.TaskKey
import sbt.Def.Initialize
import sbt.Keys.*
import sbt.util.Level

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Try

object TarballInstaller {

  def apply(task: TaskKey[File]): Initialize[Task[File]] = Def.task {
    installTarball(
      name = (task / installExecutableName).value,
      tarball = (Universal / packageZipTarball).value,
      installDest = (task / installLocation).value,
      exeRoot = (task / installExecutableLocation).value,
      logger = (task / streams).value.log
    )
  }

  /** Installs a packaged tarball from the output of universal/packageZipTarball
    *
    * @param name
    *   The name of the application to install
    * @param tarball
    *   The tarball file to install
    * @param installDest
    *   Directory to copy installation files into
    * @param exeRoot
    *   File target to create a symlink to the executable in tarball/bin/<name>
    * @param logger
    *   sbt.Logger corresponding to this task
    * @return
    *   The path to the executable file
    */
  private def installTarball(
      name: String,
      tarball: sbt.File,
      installDest: sbt.File,
      exeRoot: sbt.File,
      logger: sbt.Logger
  ): sbt.File = {
    val exePath = exeRoot.toPath.resolve(name)
    val result = for {
      output <- Tar(tarball).extract(installDest.toPath).map { f =>
                  logger.log(Level.Debug, s"Extracted tarball to $f")
                  f
                }
      binPath = output.resolve("bin").resolve(name).toAbsolutePath
      _ <- Try(binPath.toFile.setExecutable(true)).map { _ =>
             logger.log(Level.Debug, s"Set executable bit on $binPath")
           }.toEither.left.map(InstallerError(_))
      _ <- Try(Files.deleteIfExists(exePath)).map {
             case true  => logger.log(Level.Debug, s"Deleted existing symlink at $exeRoot")
             case false => logger.log(Level.Debug, s"No existing symlink at $exeRoot")
           }.toEither.left.map(InstallerError(_))
      _ <- InstallerError.checkDirectory(binPath.getParent)
      _ <- InstallerError.checkDirectory(exeRoot.toPath)
      exe <- Try {
               Files.createSymbolicLink(
                 exePath, // link
                 binPath  // target
               )
             }.map { exe =>
               logger.log(Level.Debug, s"Created symlink $exe")
               exe
             }.toEither.left.map(InstallerError(_))
    } yield exe

    result.fold(
      err => throw new InstallerFailure(err),
      f => f.toFile
    )
  }

}
