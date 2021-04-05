package playing.autowire

import java.nio.ByteBuffer
import boopickle.Default._
import org.scalajs.dom.raw.{Event, MessageEvent, WebSocket}
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray._


case class AutowireWebSocketClient(socket: WebSocket)
  extends autowire.Client[ByteBuffer, Pickler, Pickler]
    with AutowireSerializers {

  override def doCall(req: Request): Future[ByteBuffer] = {
    // Request
    socket.readyState match {
      case WebSocket.OPEN =>
        socket.send(
          Pickle.intoBytes((req.path, req.args)).typedArray().buffer
        )

      case WebSocket.CONNECTING =>
        println("WebSocket connecting...")
        socket.onopen = { _: Event =>
          println("WebSocket connected")
          socket.send(
            Pickle.intoBytes((req.path, req.args)).typedArray().buffer
          )
        }

      case _ =>
        throw new IllegalStateException("Unexpected close/closing WebSocket")
    }

    // Response
    val promise = Promise[ByteBuffer]
    socket.onmessage = { e: MessageEvent =>
      promise.trySuccess(
        TypedArrayBuffer.wrap(e.data.asInstanceOf[ArrayBuffer])
      )
    }
    promise.future
  }
}