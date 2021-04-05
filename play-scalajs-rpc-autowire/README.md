# Play ScalaJS Autowire rpc boilerplate library

This [Scala-js](https://www.scala-js.org) Play project demonstrates how to transfer data between the client and server with [Autowire](https://github.com/lihaoyi/autowire) using a pickling library like [BooPickle](https://boopickle.suzaku.io) in a generic easy way with both Ajax and/or WebSockets.

Specially large projects with multiple apis can benefit from only having to configure the moving parts of the autowiring.

Import the following library to have the boilerplate code available:

    "com.marcgrue" %%% "playing-rpc-autowire" % "0.2.0"

The `play-scalajs-rpc-autowire-test-dep` project shows a mminimal example of using this dependency.


## Shared API for both Ajax and WebSocket

Generic parts of the Autowire plumbing have been isolated in util classes in each of the client/server/shared sub projects.


## Setup Play controller with Api

Use a shared API like `FooApi`

```scala
trait FooApi {
  def incr(i: Int): Int
}
```
and a server implementation class like `FooApiImpl` with an `incr` method returning an `Int`:

```scala
class FooApiImpl extends FooApi {
  def incr(i: Int): Int = i + 1
}
```

to create an Autowire-aware Play controller:


```scala
@Singleton
class FooController @Inject()(api: FooApiImpl)(implicit ec: ExecutionContext)
  extends AutowireController {

  // Route api instance with shared Api type
  val autowireRouter = AutowireServer.route[FooApi](api)

  // Actions...
}
```

## Setup routes

Tell Play where to route your api calls:

```                  
# Ajax routing
POST  /foo/*path   controllers.FooController.autowireAjax(path: String)
              
# and/or...

# Websocket routing
GET   /foo/ws      controllers.FooController.autowireWebSocket
```

## Add wire handle(s)

Then you need to create one or more handles in the client for the protocol(s) you want to use to wire your API:

```scala
import util.client.autowire._

// Using Ajax
object fooAjax extends AutowireAjax[FooApi]("foo")

// and/or...

// Using WebSocket
object fooWs extends AutowireWebSocket[FooApi]("foo")
```

That's it.

## Call api from Client

Using the wire handles to call methods of the FooApi and `call()` returns a Future with the result:

```scala
fooAjax().incr(1).call().foreach { n =>
  println("got an incremented number from the server via Ajax: " + n)
}

// Or via WebSocket using a WS wire handle...

fooWs().incr(1).call().foreach { n =>
  println("got an incremented number from the server via WebSocket: " + n)
}
```
