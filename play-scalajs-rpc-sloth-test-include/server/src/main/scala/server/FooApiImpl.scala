package server

import shared.FooApi
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FooApiImpl extends FooApi {
  def incr(i: Int): Future[Int] = Future(i + 1)
}
