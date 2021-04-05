package playing.sloth

import java.nio.ByteBuffer
import boopickle.Default._
import cats.implicits._
import sloth.{Client, ClientException}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait WebClient extends Serializations {

  protected val clientAjax: Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](WebTransportAjax)

  protected val clientWs: Client[ByteBuffer, Future, ClientException] =
    Client[ByteBuffer, Future, ClientException](WebTransportWebSocket)
}