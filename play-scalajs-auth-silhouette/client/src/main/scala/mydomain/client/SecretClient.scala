package mydomain.client

import org.scalajs.dom.document
import scalatags.JsDom.all.{p, _}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("SecretClient")
object SecretClient {

  @JSExport
  def load(): Unit = {
    document.body.appendChild(
      div(
        h3("Secret page"),
        p(mydomain.shared.Dialogue.answer),
        p(a(href := "/public", "What was the question?")),
        p(a(href := "/signOut", "Sign out"))
      ).render
    )
  }
}
