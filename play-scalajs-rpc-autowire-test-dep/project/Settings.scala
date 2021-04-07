import com.typesafe.sbt.web.Import.{Assets, pipelineStages}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import play.sbt.PlayImport.guice
import sbt.Keys._
import sbt._
import webscalajs.WebScalaJS.autoImport.scalaJSPipeline

object Settings {

  private val common: Seq[Def.Setting[_]] = Seq(
    organization := "com.marcgrue",
    version := "0.2.0",
    scalaVersion := "2.13.5"
  )

  val client: Seq[Def.Setting[_]] = common ++ Seq(
    // transitive dependency imported with playing-rpc-autowire
    // But you can import them explicitly too
    //    libraryDependencies ++= Seq(
    //      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    //    )
  )

  val server: Seq[Def.Setting[_]] = common ++ Seq(
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
  )

  val shared: Seq[Def.Setting[_]] = common ++ Seq(
    libraryDependencies ++= Seq(
      // transitive dependencies imported with playing-rpc-autowire
      // But you can import them explicitly too
      //      "com.lihaoyi" %%% "scalatags" % "0.9.4",
      //      "io.suzaku" %%% "boopickle" % "1.3.3",
      //      "com.lihaoyi" %%% "autowire" % "0.3.3",
      //      "com.marcgrue" %%% "playing-utils" % "0.1.0",
      "com.marcgrue" %%% "playing-rpc-autowire" % "0.2.0"
    )
  )
}
