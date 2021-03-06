import com.typesafe.sbt.web.Import.{Assets, pipelineStages}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import play.sbt.PlayImport.guice
import sbt.Keys._
import sbt._
import webscalajs.WebScalaJS.autoImport.{scalaJSPipeline, scalaJSProjects}


object Settings {

  private val common: Seq[Def.Setting[_]] = Seq(
    organization := "com.marcgrue",
    version := "0.2.0",
    scalaVersion := "2.13.5"
  )

  val client: Seq[Def.Setting[_]] = common ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )
  )

  val server: Seq[Def.Setting[_]] = common ++ Seq(
    libraryDependencies ++= Seq(
      guice
    ),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
  )

  val shared: Seq[Def.Setting[_]] = common ++ Seq(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      "io.suzaku" %%% "boopickle" % "1.3.3",
      "com.github.cornerman" %%% "sloth" % "0.3.0",
      "com.marcgrue" %%% "playing-utils" % "0.1.0"
    )
  )
}
