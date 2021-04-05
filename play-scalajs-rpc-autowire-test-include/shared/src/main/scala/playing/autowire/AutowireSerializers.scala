package playing.autowire

import java.nio.ByteBuffer
import boopickle.Default._

trait AutowireSerializers
  extends autowire.Serializers[ByteBuffer, Pickler, Pickler] {

  override def read[Result: Pickler](p: ByteBuffer): Result =
    Unpickle.apply[Result].fromBytes(p)

  override def write[Result: Pickler](r: Result): ByteBuffer =
    Pickle.intoBytes(r)
}
