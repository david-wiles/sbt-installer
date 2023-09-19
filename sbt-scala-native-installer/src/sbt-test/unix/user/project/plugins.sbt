{
  val pluginVersion = sys.props.getOrElse("plugin.version", throw new RuntimeException(
    """|The system property 'plugin.version' is not defined.
       |Specify this property using the scriptedLaunchOpts -D.""".stripMargin))

  addSbtPlugin("net.davidwiles" % "sbt-native-installer" % pluginVersion)
  addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.15")
}
