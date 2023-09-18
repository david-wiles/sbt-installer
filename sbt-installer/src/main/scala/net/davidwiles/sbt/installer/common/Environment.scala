package net.davidwiles.sbt.installer.common

import java.nio.file.{Path, Paths}

object Environment {
  lazy val username: Option[String] = Option(System.getProperty("user.name"))
  lazy val home: Option[String] = Option(System.getProperty("user.home"))

  lazy val defaults: PlatformDefaults = {
    System.getProperty("os.name").toLowerCase match {
      case mac if mac.contains("mac") => UnixDefaults
//      case win if win.contains("win") =>
      case linux if linux.contains("linux") => UnixDefaults
      case osName => throw new RuntimeException(s"Unknown operating system $osName")
    }
  }

  /**
   * Determine the default installation root path based on the user's name
   * and home directory. If the user or home can't be determined, then it will
   * default as if the user is root
   * @return The path to the installation root directory
   */
  def getDefaultInstallRoot: Path = {
    username match {
      case Some("root") | None =>
        defaults.installDirectory
      case _ =>
        home
          .map(dir => Paths.get(dir).resolve(".local"))
          .getOrElse(defaults.installDirectory)
    }
  }

  sealed trait PlatformDefaults {
    val installDirectory: Path
  }

  private object UnixDefaults extends PlatformDefaults {
    override lazy val installDirectory: Path = Paths.get("/opt")
  }
}
