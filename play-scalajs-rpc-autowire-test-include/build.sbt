lazy val client = (project in file("client"))
  .dependsOn(shared.js)
  .enablePlugins(ScalaJSPlugin, ScalaJSWeb)
  .settings(Settings.client)


lazy val server = (project in file("server"))
  .dependsOn(shared.jvm)
  .settings(Settings.server, scalaJSProjects := Seq(client))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .jsConfigure(_.enablePlugins(ScalaJSWeb)) // Configures sourcemaps for shared code on remote hosts too
  .settings(Settings.shared)