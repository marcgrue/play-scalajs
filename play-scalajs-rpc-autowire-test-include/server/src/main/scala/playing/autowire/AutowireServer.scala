package playing.autowire

import java.nio.ByteBuffer
import boopickle.Default._

object AutowireServer
  extends autowire.Server[ByteBuffer, Pickler, Pickler]
  with AutowireSerializers