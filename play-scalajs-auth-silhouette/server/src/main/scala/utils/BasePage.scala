package utils

import controllers.routes
import play.api.mvc.RequestHeader
import play.api.mvc.request.RequestAttrKey
import scalatags.Text
import scalatags.Text.all._

abstract class BasePage(request: RequestHeader) {

  def safeScript: Text.TypedTag[String] = {
    request.attrs.get(RequestAttrKey.CSPNonce) match {
      case Some(nonce) => script(attr("nonce") := nonce)
      case None        => script()
    }
  }

  def safeLink: Text.TypedTag[String] = {
    request.attrs.get(RequestAttrKey.CSPNonce) match {
      case Some(nonce) => link(attr("nonce") := nonce)
      case None        => link()
    }
  }

  val fastOptJS: Text.TypedTag[String] = safeScript(
    tpe := "text/javascript",
    src := routes.Assets.versioned("client-fastopt.js").toString
  )
}
