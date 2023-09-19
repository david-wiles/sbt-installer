package net.davidwiles.sbt.installer.unix

import net.davidwiles.sbt.installer.common.InstallerError._
import sbt._
import sbt.Keys._
import sbt.Def.Initialize
import net.davidwiles.sbt.installer.common.InstallerKeys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.fullLinkJSOutput

import scala.util.Try
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING

object ScalaJSInstaller {

  def apply(task: TaskKey[File]): Initialize[Task[File]] = Def.task {
    installJavaScript(
      name = (task / installExecutableName).value,
      version = version.value,
      js = (Compile / fullLinkJSOutput).value,
      exeRoot = (task / installExecutableLocation).value,
      installRoot = (task / installLocation).value,
      logger = (task / streams).value.log
    )
  }

  private def installJavaScript(
      name: String,
      version: String,
      js: File,
      installRoot: File,
      exeRoot: File,
      logger: sbt.Logger
  ): File = {
    val script   = installRoot.toPath.resolve(s"$name-$version.js").toAbsolutePath
    val launcher = exeRoot.toPath.resolve(name)
    val result = for {
      _ <- checkDirectory(exeRoot.toPath)
      _ <- checkDirectory(installRoot.toPath)
      _ <- Try(Files.copy(js.toPath.resolve("main.js"), script, COPY_ATTRIBUTES, REPLACE_EXISTING)).map { _ =>
             logger.log(Level.Debug, s"Copied ${js.toPath} to $script")
           }.toError(s"Failed to copy ${js.toPath} to $script")
      _ <- Try(Files.write(launcher, shellScript(script)), TRUNCATE_EXISTING, CREATE).map { p =>
             logger.log(Level.Debug, s"Wrote shell script to $p")
           }.toError(s"Failed to write shell script to $launcher")
      _ <- Try(launcher.toFile.setExecutable(true)).map { _ =>
             logger.log(Level.Debug, s"Set executable bit on $launcher")
           }.toError(s"Failed to set executable bit on $launcher")
    } yield ()

    result.fold(
      error => throw new InstallerFailure(error),
      _ => launcher.toFile
    )
  }

  private def shellScript(script: Path): Array[Byte] =
    s"""
       |#!/bin/sh
       |node ${script.toAbsolutePath.toString} $$@
       |""".stripMargin.getBytes

}
