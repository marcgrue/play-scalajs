import com.typesafe.sbt.web.Import.{Assets, pipelineStages}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import play.sbt.PlayImport.{PlayKeys, ehcache, filters, guice, specs2}
import sbt.Keys._
import sbt.{Def, _}
import webscalajs.WebScalaJS.autoImport.scalaJSPipeline

object Settings {

  val base: Seq[Def.Setting[_]] = Seq(
    name := "playing-auth-silhouette",
    organization := "com.marcgrue",
    version := "0.2.0-SNAPSHOT",
    ThisBuild / scalaVersion := "2.13.5",
    scalacOptions := Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-language:postfixOps"
    )
  )

  val client: Seq[Def.Setting[_]] = Seq(
    name := "client",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )

  val server: Seq[Def.Setting[_]] = Seq(
    name := "server",
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= Seq(
      "com.mohiva" %% "play-silhouette" % "7.0.0" ,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
      "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
      "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
      "com.mohiva" %% "play-silhouette-totp" % "7.0.0",

      "net.codingwell" %% "scala-guice" % "4.2.11",
      "com.iheart" %% "ficus" % "1.5.0",
      "com.typesafe.play" %% "play-mailer" % "8.0.1",
      "com.typesafe.play" %% "play-mailer-guice" % "8.0.1",
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",

      "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test",
      specs2 % Test,
      ehcache,
      guice,
      filters
    ),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    Global / onLoad := (Global / onLoad).value andThen { s: State => "project server" :: s }
  )

  val shared: Seq[Def.Setting[_]] = base ++ Seq(
    name := "shared",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
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
    description := "playing-auth-silhouette",
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
