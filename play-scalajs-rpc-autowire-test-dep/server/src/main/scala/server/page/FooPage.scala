package server.page

import controllers.routes
import scalatags.Text
import scalatags.Text.all._

object FooPage {

  def apply(): Text.TypedTag[String] =
    html(
      head(
        script(
          tpe := "text/javascript",
          src := routes.Assets.at("client-fastopt.js").url
        )
      ),

      body(
        h1("playing-rpc-autowire-test-dep"),
        script(s"FooClient.load()")
      )
    )
}