import sbtcrossproject.CrossPlugin.autoImport.crossProject


lazy val client = (project in file("client"))
  .dependsOn(shared.js)
  .settings(Settings.client)
  .enablePlugins(ScalaJSWeb)


lazy val server = (project in file("server"))
  .dependsOn(shared.jvm)
  .settings(Settings.server, scalaJSProjects := Seq(client))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(Settings.shared)