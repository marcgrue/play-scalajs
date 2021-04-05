package shared

import scala.concurrent.Future

trait FooApi {
  def incr(a: Int): Future[Int]
}


