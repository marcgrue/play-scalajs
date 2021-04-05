package server
import shared.FooApi

class FooApiImpl extends FooApi {
  def incr(i: Int): Int = i + 1
}
