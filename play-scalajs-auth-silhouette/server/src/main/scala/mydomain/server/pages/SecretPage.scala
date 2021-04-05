package mydomain.server.pages

import auth.views.SilhouettePage
import play.api.mvc.RequestHeader
import scalatags.Text
import scalatags.Text.all.{head, _}
import utils.BasePage

case class SecretPage(request: RequestHeader)
  extends BasePage(request) with SilhouettePage {

  def apply(): Text.TypedTag[String] = page(
    // Import transpiled scala JS code
    fastOptJS,
    // Load client page
    body(safeScript(s"SecretClient.load()"))
  )
}
