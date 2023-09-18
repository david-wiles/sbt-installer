package net.davidwiles.sbt.installer.unix

import net.davidwiles.sbt.installer.common.InstallerError.*
import net.davidwiles.sbt.installer.common.InstallerKeys.*
import sbt.Def
import sbt.File
import sbt.Task
import sbt.TaskKey
import sbt.Def.Initialize
import sbt.Keys.streams
import sbt.Keys.version
import sbtassembly.AssemblyKeys.assembly
import sbtassembly.AssemblyKeys.assemblyOutputPath
import sbt.util.Level

import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.Files
import java.nio.file.Path
import scala.util.Try

object AssemblyInstaller {

  def apply(task: TaskKey[File]): Initialize[Task[File]] = Def.task {
    installUberJar(
      name = (task / installExecutableName).value,
      exeRoot = (task / installExecutableLocation).value,
      installDest = (task / installLocation).value,
      jar = (assembly / assemblyOutputPath).value,
      version = version.value,
      logger = (task / streams).value.log
    )
  }

  /** Installs an uber jar from the output of sbt-assembly
    *
    * @param name
    *   Name of the executable file
    * @param exeRoot
    *   Root directory to create the executable file in
    * @param installDest
    *   Root directory to copy the jar to
    * @param jar
    *   Jarfile to install
    * @param logger
    *   sbt.Logger corresponding to this task
    * @return
    *   Path to the executable file
    */
  private def installUberJar(
      name: String,
      exeRoot: File,
      installDest: File,
      jar: File,
      version: String,
      logger: sbt.Logger
  ): File = {
    val packageRoot = installDest.toPath.resolve(s"$name-$version").toAbsolutePath
    val jarPath     = packageRoot.resolve("lib").resolve(jar.getName).toAbsolutePath
    val result = for {
      _ <- checkDirectory(exeRoot.toPath)
      _ <- checkDirectory(installDest.toPath)
      _ <- Try(Files.createDirectories(packageRoot.resolve("lib"))).map { p =>
             logger.log(Level.Debug, s"Created directory $p")
           }.toError(s"Failed to create directory ${installDest.toPath.resolve("lib")}")
      _ <- Try(Files.copy(jar.toPath, jarPath, REPLACE_EXISTING, COPY_ATTRIBUTES)).map { p =>
             logger.log(Level.Debug, s"Copied $jar to $p")
           }.toError(s"Failed to copy $jar to $jarPath")
      executable = exeRoot.toPath.resolve(name).toAbsolutePath
      _ <- Try(Files.write(executable, shellScript(jarPath)), TRUNCATE_EXISTING, CREATE).map { p =>
             logger.log(Level.Debug, s"Wrote shell script to $p")
           }.toError(s"Failed to write shell script to $executable")
      _ <- Try(executable.toFile.setExecutable(true)).map { _ =>
             logger.log(Level.Debug, s"Set executable bit on $executable")
           }.toError(s"Failed to set executable bit on $executable")
    } yield executable.toFile

    result.fold(
      error => throw new InstallerFailure(error),
      identity
    )
  }

  private def shellScript(jar: Path): Array[Byte] =
    s"""
       |#!/bin/sh
       |java -jar ${jar.toAbsolutePath.toString} $$@
       |""".stripMargin.getBytes

}
