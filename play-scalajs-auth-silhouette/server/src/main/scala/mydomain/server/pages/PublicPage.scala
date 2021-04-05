package mydomain.server.pages

import auth.views.SilhouettePage
import play.api.mvc.RequestHeader
import scalatags.Text
import scalatags.Text.all._
import utils.BasePage

case class PublicPage(request: RequestHeader)
  extends BasePage(request) with SilhouettePage {

  def apply(authenticated: Boolean = false): Text.TypedTag[String] = page(
    // Import transpiled scala JS code
    fastOptJS,
    // Load client page
    body(safeScript(s"PublicClient.load($authenticated)"))
  )
}
