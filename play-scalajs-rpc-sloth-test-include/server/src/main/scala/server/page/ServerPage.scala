package server.page

import controllers.routes
import scalatags.Text
import scalatags.Text.all._

object ServerPage {

  def apply(): Text.TypedTag[String] =
    html(
      head(
        script(
          tpe := "text/javascript",
          src := routes.Assets.at("client-fastopt.js").url
        )
      ),

      body(
        h1("playing-rpc-sloth-test-include" +
          ""),
        script(s"FooClient.load()")
      )
    )
}