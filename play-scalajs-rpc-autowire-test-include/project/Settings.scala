import com.typesafe.sbt.web.Import.{Assets, pipelineStages}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import play.sbt.PlayImport.{PlayKeys, guice}
import sbt.Keys._
import sbt._
import webscalajs.WebScalaJS.autoImport.scalaJSPipeline

object Settings {

  private val common: Seq[Def.Setting[_]] = Seq(
    name := "playing-rpc-autowire-test-include",
    organization := "com.marcgrue",
    version := "0.2.0",
    scalaVersion := "2.13.5"
  )

  val client: Seq[Def.Setting[_]] = common ++ Seq(
    name := "client",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )

  val server: Seq[Def.Setting[_]] = common ++ Seq(
    name := "server",
    libraryDependencies ++= Seq(
      guice
    ),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    Global / onLoad := (Global / onLoad).value andThen { s: State => "project server" :: s }
  )

  val shared: Seq[Def.Setting[_]] = common ++ Seq(
    name := "shared",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      "com.lihaoyi" %%% "autowire" % "0.3.3",
      "io.suzaku" %%% "boopickle" % "1.3.3",
      "com.marcgrue" %%% "playing-utils" % "0.1.0"
    )
  )
}
