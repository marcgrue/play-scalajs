package shared

import scala.concurrent.Future

trait FooApi {
  def incr(i: Int): Future[Int]
}


