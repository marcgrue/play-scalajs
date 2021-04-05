package playing.autowire

import java.nio.ByteBuffer
import autowire.ClientProxy
import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom.ext.AjaxException
import org.scalajs.dom.window
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.typedarray._

case class AutowireAjaxClient(context: String)
  extends autowire.Client[ByteBuffer, Pickler, Pickler]
    with AutowireSerializers {

  override def doCall(req: Request): Future[ByteBuffer] = {
    // Request
    dom.ext.Ajax.post(
      url = s"http://${window.location.host}/$context/" + req.path.mkString("/"),
      data = Pickle.intoBytes(req.args), // Map of param -> value
      responseType = "arraybuffer",
      headers = Map("Content-Type" -> "application/octet-stream")
    ).map(r =>
      // Response
      TypedArrayBuffer.wrap(r.response.asInstanceOf[ArrayBuffer])
    ).recover {
      // Catch ajax exceptions and alert user
      case e@AjaxException(xhr) =>
        val advice = "\nPlease re-start server from terminal and refresh page."
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