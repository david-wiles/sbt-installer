{
  val pluginVersion = sys.props.getOrElse("plugin.version", throw new RuntimeException(
    """|The system property 'plugin.version' is not defined.
       |Specify this property using the scriptedLaunchOpts -D.""".stripMargin))

  addSbtPlugin("net.davidwiles" % "sbt-scalajs-installer" % pluginVersion)
}
