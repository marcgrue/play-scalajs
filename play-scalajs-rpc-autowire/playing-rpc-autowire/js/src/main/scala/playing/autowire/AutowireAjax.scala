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

// Generic autowire Ajax wiring base classes

case class AutowireAjax[Api](context: String) {
  private val client = AutowireAjaxClient(context).apply[Api]
  def apply(): ClientProxy[Api, ByteBuffer, Pickler, Pickler] = client
}
