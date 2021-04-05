lazy val root = (project in file("."))
  .aggregate(`playing-rpc-sloth`.js, `playing-rpc-sloth`.jvm)
  .settings(publish / skip := true)


lazy val `playing-rpc-sloth` = crossProject(JSPlatform, JVMPlatform)
  .settings(Settings.shared)
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)