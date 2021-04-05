package client

import boopickle.Default._
import playing.sloth.WebClient
import shared.FooApi

object wire extends WebClient {
  val fooAjax = clientAjax.wire[FooApi]
  val fooWs   = clientWs.wire[FooApi]
}
