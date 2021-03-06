package playing.sloth

import java.net.URI
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon.{Deserializer, Serializer, SerializerDeserializer}
import scala.util.{Failure, Success, Try}


trait Serializations {

  // Copying this method so that we can avoid `import chameleon.ext.boopickle._`
  // in all custom SlothControllers and WebClients
  implicit def boopickleSerializerDeserializer[T: Pickler]: SerializerDeserializer[T, ByteBuffer] =
    new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
      override def serialize(arg: T): ByteBuffer = Pickle.intoBytes(arg)
      override def deserialize(arg: ByteBuffer): Either[Throwable, T] =
        Try(Unpickle[T].fromBytes(arg)) match {
          case Success(arg) => Right(arg)
          case Failure(t)   => Left(t)
        }
    }

  // Some common pickle transformers
  implicit val datePickler = transformPickler((t: Long) => new java.util.Date(t))(_.getTime)
  implicit val uriPickler  = transformPickler((t: String) => new URI(t))(_.toString)
}
