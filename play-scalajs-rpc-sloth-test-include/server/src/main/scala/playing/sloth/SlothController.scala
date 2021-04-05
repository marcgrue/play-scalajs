package playing.sloth

import java.nio.ByteBuffer
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import boopickle.Default._
import play.api.http.websocket.{BinaryMessage, Message, TextMessage}
import play.api.mvc.{Action, InjectedController, RawBuffer, WebSocket}
import playing.scalatags.HtmlTag
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


trait SlothController extends InjectedController with Serializations with HtmlTag {

  // Router instance to be implemented in custom Controllers
  val router: Router[ByteBuffer, Future]


  // Type safe receiving end of Ajax post from client
  def slothAjax(pathStr: String): Action[RawBuffer] = {
    Action.async(parse.raw) { implicit ajaxRequest =>
      // The path (last segments of the ajax url) is used to identify which
      // server method we want to call.
      val path = pathStr.split("/").toList

      // Unpickle param values
      val pickler  = Unpickle.apply[ByteBuffer]
      val argsData = ajaxRequest.body.asBytes(parse.UNLIMITED).get
      val args     = pickler.fromBytes(argsData.asByteBuffer)

      val routerResult: RouterResult[ByteBuffer, Future] =
        router.apply(Request[ByteBuffer](path, args))

      routerResult.toEither match {
        case Right(byteBufferResultFuture) =>
          byteBufferResultFuture.map { byteBufferResult =>
            // Convert ByteBuffer to Array of Bytes
            val dataAsByteArray = Array.ofDim[Byte](byteBufferResult.remaining())
            byteBufferResult.get(dataAsByteArray)

            // Send byte Array to HTTP response to Client that can received it
            // as an ArrayBuffer
            Ok(dataAsByteArray)
          }

        case Left(_) =>
          Future(InternalServerError("ouch"))
      }
    }
  }


  // Type safe receiving end of websocket messages from client
  def slothWebSocket: WebSocket = {
    WebSocket.accept[Message, Message] { requestHeader =>
      Flow[Message].mapAsync(parallelism = 2) {
        case BinaryMessage(byteString) =>
          val pickler      = Unpickle.apply[(List[String], ByteBuffer)]
          val (path, args) = pickler.fromBytes(byteString.asByteBuffer)
          val request      = Request[ByteBuffer](path, args)
          val routerResult = router.apply(request)
          routerResult.toEither match {
            case Right(byteBufferResultFuture) =>
              byteBufferResultFuture.map { byteBufferResult =>
                BinaryMessage(ByteString(byteBufferResult))
              }

            case Left(serverFailure) =>
              Future(BinaryMessage(ByteString("Websocket server failure: " + serverFailure)))
          }

        case other =>
          throw new IllegalArgumentException("Unexpected Websocket Message: " + other)
      }.keepAlive(20.seconds, () => TextMessage("keepalive"))
    }
  }
}

