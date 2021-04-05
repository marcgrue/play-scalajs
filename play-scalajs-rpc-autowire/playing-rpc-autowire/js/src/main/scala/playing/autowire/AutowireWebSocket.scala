package playing.autowire

import java.nio.ByteBuffer
import autowire.ClientProxy
import boopickle.Default._
import org.scalajs.dom.raw.{CloseEvent, Event, MessageEvent, WebSocket}
import org.scalajs.dom.window
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray._


// Generic autowire WebSocket wiring base classes

case class AutowireWebSocket[Api](ctx: String) {
  private var client            = newClient
  private var socket: WebSocket = _

  def apply(): ClientProxy[Api, ByteBuffer, Pickler, Pickler] = {
    socket.readyState match {
      case WebSocket.CLOSING | WebSocket.CLOSED =>
        client = newClient
        client
      case _                                    =>
        client
    }
  }

  def newSocket: WebSocket = {
    val socket = new WebSocket(s"ws://${window.location.host}/$ctx/ws")
    socket.binaryType = "arraybuffer"
    socket.onerror = { e: Event =>
      println(s"WebSocket error: $e!")
      socket.close(0, e.toString)
    }
    socket.onclose = { _: CloseEvent =>
      println("WebSocket closed")
    }
    socket
  }

  def newClient: ClientProxy[Api, ByteBuffer, Pickler, Pickler] = {
    socket = newSocket
    AutowireWebSocketClient(socket).apply[Api]
  }
}
