package client

import client.wire._
import org.scalajs.dom.document
import scalatags.JsDom.all._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


@JSExportTopLevel("FooClient")
object FooClient {

  @JSExport
  def load(): Unit = {

    fooAjax.incr(1).foreach { result =>
      document.body.appendChild(p("Ajax: 1 + 1 = " + result).render)
    }

    fooWs.incr(1).foreach { result =>
      document.body.appendChild(p("WebSocket: 1 + 1 = " + result).render)

      document.body.appendChild(Perf.render)
    }
  }
}
