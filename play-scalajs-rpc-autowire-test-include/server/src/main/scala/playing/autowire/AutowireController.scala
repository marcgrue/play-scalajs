package playing.autowire

import java.nio.ByteBuffer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import boopickle.Default._
import play.api.http.websocket.{BinaryMessage, Message, TextMessage}
import play.api.mvc._
import playing.scalatags.HtmlTag
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._


trait AutowireController extends InjectedController with HtmlTag {

  /** Instantiate router in inheriting Controller with
   * {{{
   *   val autowireRouter = AutowireServer.route[YourApi](yourApiServerImplementation)
   * }}}
   *
   * OBS: Some hints to make autowire happy:
   * - no `val` declarations in shared api interface
   * - passing `None` results in `InvalidInput: null`
   * - default arguments only in shared api interface, NOT in implementation!
   */
  val autowireRouter: AutowireServer.Router

  // Type safe receiving end of Ajax post from client
  def autowireAjax(pathStr: String): Action[RawBuffer] = {
    Action.async(parse.raw) { implicit ajaxRequest =>
      // The path (last segments of the ajax url) is used to identify which
      // server method we want to call.
      val path = pathStr.split("/").toSeq

      // Unpickle Map of param -> value
      val pickler  = Unpickle.apply[Map[String, ByteBuffer]]
      val argsData = ajaxRequest.body.asBytes(parse.UNLIMITED).get
      val args     = pickler.fromBytes(argsData.asByteBuffer)

      // Autowire Request holding server method coordinates and unpickled args
      val methodRequest = autowire.Core.Request(path, args)

      /*
       * Invoke server method
       *
       * The autowire Router is a macro-generated collection of partial functions
       * that match autowire Requests as input and calls the corresponding server
       * method with the args provided in the Request. Applying the method request
       * to the router then:
       * - validates bindings
       * - wraps the invocation in a Future
       * - invokes the method with the args
       * - write/pickle the result into a ByteBuffer (when using boopickle)
       */
      val futResult: Future[ByteBuffer] = autowireRouter.apply(methodRequest)

      // Result of server method invocation
      futResult.map { byteBufferResult =>
        // Convert ByteBuffer to Array of Bytes
        val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
        byteBufferResult.get(dataAsByteArray)

        // Send byte Array to HTTP response to Client that can received it
        // as an ArrayBuffer
        Ok(dataAsByteArray)
      }
    }
  }


  // Type safe receiving end of websocket messages from client
  def autowireWebSocket: WebSocket = {
    WebSocket.accept[Message, Message] { requestHeader =>
      Flow[Message].mapAsync(parallelism = 1) {
        case BinaryMessage(byteString) =>
          val pickler       = Unpickle.apply[(Seq[String], Map[String, ByteBuffer])]
          val (path, args)  = pickler.fromBytes(byteString.asByteBuffer)
          val methodRequest = autowire.Core.Request(path, args)
          val futResult     = autowireRouter.apply(methodRequest)
          futResult.map(result => BinaryMessage(ByteString(result)))

        case other =>
          throw new IllegalArgumentException("Unexpected Websocket Message: " + other)
      }.keepAlive(20.seconds, () => TextMessage("keepalive"))
    }
  }
}

