package net.davidwiles.sbt.installer.unix

import sbt.File

import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import scala.util.Try

object Installer {

  // Currently only for java-based executables
  def apply(executable: File, installLocation: File, jarFile: File): Unit = {
    (for {
      _ <- Try(Files.createDirectories(installLocation.toPath.resolve("lib")))
      _ <- Try(Files.createDirectories(installLocation.toPath.resolve("conf")))
      _ <- Try(Files.copy(jarFile.toPath, installLocation.toPath.resolve("lib").resolve(jarFile.getName), REPLACE_EXISTING))
      _ <- Try(Files.write(executable.toPath, shellScript(jarFile.getAbsolutePath), TRUNCATE_EXISTING))
      _ <- Try(executable.setExecutable(true))
    } yield ())
      .recover {
        case securityException: SecurityException =>
          println(s"you can't do that! $securityException")
        case ex: Exception =>
          println(s"Something went wrong: $ex")
      }
  }

  private def shellScript(jar: String): Array[Byte] =
    s"""
       |#!/bin/sh
       |java -jar $jar $$@
       |""".stripMargin
      .getBytes
}
