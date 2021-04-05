import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import play.sbt.PlayImport.guice
import sbt.Keys.{name, _}
import sbt.{url, _}


object Settings {

  private val base: Seq[Def.Setting[_]] = Seq(
    name := "playing-rpc-autowire",
    organization := "com.marcgrue",
    version := "0.2.0",
    ThisBuild / scalaVersion := "2.13.5",
    crossScalaVersions := Seq("2.12.13", "2.13.5")
  )

  val js: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )

  val jvm: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      guice
    )
  )

  val shared: Seq[Def.Setting[_]] = base ++ publish ++ Seq(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      "com.lihaoyi" %%% "autowire" % "0.3.3",
      "io.suzaku" %%% "boopickle" % "1.3.3",
      "com.marcgrue" %%% "playing-utils" % "0.1.0"
    )
  )

  lazy private val snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  lazy private val releases  = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

  lazy val publish: Seq[Def.Setting[_]] = Seq(
    publishMavenStyle := true,
    ThisBuild / publishTo := (if (isSnapshot.value) Some(snapshots) else Some(releases)),
    ThisBuild / versionScheme := Some("semver-spec"),
    Test / publishArtifact := false,
    pomIncludeRepository := (_ => false),
    homepage := Some(url("http://marcgrue.com")),
    description := "playing-rpc-autowire",
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/marcgrue/playing"),
      "scm:git:git@github.com:marcgrue/playing.git"
    )),
    developers := List(
      Developer(
        id = "marcgrue",
        name = "Marc Grue",
        email = "marcgrue@gmail.com",
        url = url("http://marcgrue.com")
      )
    )
  )
}
