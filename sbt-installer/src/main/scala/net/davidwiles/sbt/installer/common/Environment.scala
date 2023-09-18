package net.davidwiles.sbt.installer.common

import java.nio.file.Path
import java.nio.file.Paths

object Environment {

  lazy val username: Option[String] = Option(System.getProperty("user.name"))

  lazy val home: Option[String] = Option(System.getProperty("user.home"))

  lazy val defaults: PlatformDefaults = {
    System.getProperty("os.name").toLowerCase match {
      case mac if mac.contains("mac") => UnixDefaults
//      case win if win.contains("win") =>
      case linux if linux.contains("linux") => UnixDefaults
      case osName                           => throw new RuntimeException(s"Unknown operating system $osName")
    }
  }

  /** Determine the default installation root path based on the user's name and home directory. If the user or home
    * can't be determined, then it will default as if the user is root. If the user is root, then it will default to
    * /opt, if the user is not root, then it will default to ~/.local/opt.
    * @return
    *   The path to the installation root directory
    */
  def getDefaultInstallRoot: Path =
    username match {
      case Some("root") | None =>
        defaults.installDirectory
      case _ =>
        home
          .map(dir => Paths.get(dir).resolve(".local/opt"))
          // If the user's home directory can't be determined, then default to /opt
          .getOrElse(defaults.installDirectory)
    }

  /** Determine the default executable root path based on the user's name and home directory. If the user or home can't
    * be determined, then it will default as if the user is root. This is the directory where the executable will be
    * symlinked to. If the user is root, then it will default to /usr/local/bin, if the user is not root, then it will
    * default to ~/.local/bin.
    * @return
    *   The path to the executable root directory
    */
  def getDefaultExecutableRoot: Path =
    username match {
      case Some("root") | None =>
        Paths.get("/usr/local/bin")
      case _ =>
        home
          .map(dir => Paths.get(dir).resolve(".local/bin"))
          // If the user's home directory can't be determined, then default to /usr/local/bin
          .getOrElse(Paths.get("/usr/local/bin"))
    }

  sealed trait PlatformDefaults {

    val installDirectory: Path

  }

  private object UnixDefaults extends PlatformDefaults {

    override lazy val installDirectory: Path = Paths.get("/opt")

  }

}
