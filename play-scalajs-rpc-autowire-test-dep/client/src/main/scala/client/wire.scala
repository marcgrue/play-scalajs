package client

import playing.autowire._
import shared.FooApi

object ajaxFoo extends AutowireAjax[FooApi]("foo")

object wsFoo extends AutowireWebSocket[FooApi]("foo")

