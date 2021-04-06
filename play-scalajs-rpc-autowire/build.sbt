lazy val root = (project in file("."))
  .aggregate(`playing-rpc-autowire`.js, `playing-rpc-autowire`.jvm)
  .settings(publish / skip := true)

lazy val `playing-rpc-autowire` = crossProject(JSPlatform, JVMPlatform)
  .settings(Settings.shared)
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)