lazy val root = (project in file("."))
  .aggregate(`playing-utils`.js, `playing-utils`.jvm)
  .settings(publish / skip := true)


lazy val `playing-utils` = crossProject(JSPlatform, JVMPlatform)
  .settings(Settings.shared)
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)