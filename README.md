# sbt-installer

An SBT plugin which allows you to install an executable program into your path directly.

## Pre-requisites

* SBT 1.0.0 or higher
* If default install locations are used, the `~/.local/bin` directory must be in your path. 

You can setup your machine to use the by running: 

```
curl -sLk https://raw.githubusercontent.com/david-wiles/sbt-installer/main/scripts/setup.sh | sh -
```

This file is located at scripts/setup.sh in this repository if you wish to inspect it.


## Usage (JVM)

Add the following to your `project/plugins.sbt` file:

```scala
addSbtPlugin("net.davidwiles" % "sbt-installer" % "0.1.2")
```

Then, in your `build.sbt` file, add one of the following:

```scala
enablePlugins(AssemblyInstallerPlugin)
```

or

```scala
enablePlugins(TarballInstallerPlugin)
```

Now, you can run `sbt install` to install your program into your path.

By default, and executable file will be written to `$HOME/.local/bin`. You can change this by setting the `installDir` setting key:

```scala
install / installExecutableLocation := new File("/usr/local/bin")
```

You can also change the name of the executable file by setting the `installExecutableName` setting key:

```scala
install / installExecutableName := "my-program"
```

By default, the jar files will be installed to `$HOME/.local/opt/my-program`. You can change this by setting the `installLocation` setting key:

```scala
install / installLocation := new File("/usr/local/opt")
```

## Usage (Native)

Add the following to your `project/plugins.sbt` file:

```scala
addSbtPlugin("net.davidwiles" % "sbt-native-installer" % "0.1.2")
```

Then, in your `build.sbt` file, add the following:

```scala
enablePlugins(NativeInstallerPlugin)
```

## License

This project uses the Apache 2.0 license. See the LICENSE file for more details.
