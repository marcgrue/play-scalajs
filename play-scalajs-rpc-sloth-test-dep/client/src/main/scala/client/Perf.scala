package client

import boopickle.Default._
import client.wire.{fooAjax, fooWs}
import org.scalajs.dom.html.{Paragraph, Span}
import org.scalajs.dom.{Element, document}
import scalatags.JsDom.all._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.Date

object Perf {

  def render: Span = span(
    h3("Ajax"),
    ajax.test,
    ajax.incr,
    h3("WebSocket"),
    ws.test,
    ws.incr,
  ).render


  object ajax {

    def ajaxCalls(elem: Element, t0: Int, i: Int): Unit = {
      fooAjax.incr(i).foreach { n =>
        if (i % 10 == 0)
          appendValue(elem, ".")
        if (n < iterations) {
          ajaxCalls(elem, t0, n)
        } else {
          appendValue(elem, s" ${ms - t0} ms ")
        }
      }
    }

    def test: Paragraph = par(
      s"Call server $iterations times via Ajax: ",
      { () => ajaxCalls(element("v1"), ms, 0) }, "v1", newLine = true
    )

    def incr: Paragraph = par(
      "Pull data from server via Ajax: ", { () =>
        val elem      = element("v2")
        val curNumber = elem.textContent.toInt
        fooAjax.incr(curNumber).foreach { n =>
          setValue(elem, s"$n")
        }
      }, "v2", "0"
    )
  }


  object ws {

    def wsCalls(elem: Element, t0: Int, i: Int): Unit = {
      fooWs.incr(i).foreach { n =>
        if (i % 10 == 0)
          appendValue(elem, ".")
        if (n < iterations) {
          wsCalls(elem, t0, n)
        } else {
          appendValue(elem, s" ${ms - t0} ms ")
        }
      }
    }

    def test: Paragraph = par(
      s"Call server $iterations times via WebSocket: ",
      { () => wsCalls(element("v3"), ms, 0) }, "v3", newLine = true
    )

    def incr: Paragraph = par(
      "Pull data from server via WebSocket: ", { () =>
        val elem      = element("v4")
        val curNumber = elem.textContent.toInt
        fooWs.incr(curNumber).foreach { n =>
          setValue(elem, s"$n")
        }
      }, "v4", "0"
    )
  }

  // Helpers -----------------------------------------------

  def par(
    label: String,
    click: () => Unit,
    idStr: String = "",
    default: String = "",
    newLine: Boolean = false
  ) =
    p(
      a(href := "#", label, onclick := click, marginRight := 8),
      if (newLine) br() else (),
      span(id := idStr, default)
    ).render

  def element(idStr: String) = document.getElementById(idStr)

  def appendValue(elem: Element, s: String): Unit = {
    elem.textContent = elem.textContent + s
  }

  def ms: Int = {
    val d = new Date()
    d.getHours() * 60 * 60 * 1000 +
      d.getMinutes() * 60 * 1000 +
      d.getSeconds() * 1000 +
      d.getMilliseconds()
  }.toInt

  val iterations = 1000

  def setValue(elem: Element, newValue: String) = {
    elem.innerHTML = ""
    elem.textContent = newValue
  }
}
