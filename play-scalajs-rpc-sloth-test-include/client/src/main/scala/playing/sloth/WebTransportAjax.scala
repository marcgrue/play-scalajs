package playing.sloth

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException
import org.scalajs.dom.window
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.typedarray._

object WebTransportAjax extends RequestTransport[ByteBuffer, Future] {

  override def apply(req: Request[ByteBuffer]): Future[ByteBuffer] = {
    // Request
    dom.ext.Ajax.post(
      url = s"http://${window.location.host}/ajax/" + req.path.mkString("/"),
      data = Pickle.intoBytes(req.payload), // Param values
      responseType = "arraybuffer",
      headers = Map("Content-Type" -> "application/octet-stream")
    ).map(r =>
      // Response
      TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer])
    ).recover {
      // Catch ajax exceptions and alert user
      case e@AjaxException(xhr) =>
        val advice = "\nNew api methods might not be visible as js-code upon a refresh " +
          "since the sloth macro needs to generate them first. " +
          "\nCached js-code can also become out of date. " +
          "\nSo you can try to stop the server from the terminal (ctrl-c twice), run `sbt`, `clean` and then `run`. " +
          "\nThis should allow new signatures to propagate to js-code."
        val msg    = xhr.status match {
          case 0 => s"Ajax call failed: server not responding. $advice"
          case n => s"Ajax call failed: XMLHttpRequest.status = $n. $advice"
        }
        println(msg)
        window.alert(msg)
        throw e
    }
  }
}
