package client

import playing.autowire._
import shared.FooApi

object fooAjax extends AutowireAjax[FooApi]("foo")

object fooWs extends AutowireWebSocket[FooApi]("foo")

