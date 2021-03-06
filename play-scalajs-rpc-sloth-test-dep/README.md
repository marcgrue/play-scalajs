# Play ScalaJS project using playing-sloth dependency

This [Scala-js](https://www.scala-js.org) Play project demonstrates how to transfer data between the client and server with [Sloth](https://github.com/cornerman/sloth) using a pickling library like [BooPickle](https://boopickle.suzaku.io) in a generic easy way with both Ajax and/or WebSockets.

Specially large projects with multiple apis can benefit from only having to configure the moving parts of the wiring.

Imports the following library to have the boilerplate code available:

    "com.marcgrue" %%% "playing-rpc-sloth" % "0.2.1"


## Shared API for both Ajax and WebSocket

Generic parts of the Sloth plumbing have been isolated in util classes in each of the client/server/shared sub projects.


## Setup Play controller with Api

Use a shared API like `FooApi`

```scala
trait FooApi {
  def incr(i: Int): Future[Int]
}
```
and a server implementation object like `FooApiImpl` with an `incr` method returning a `Future[Int]`:

```scala
object FooApiImpl extends FooApi {
  def incr(i: Int): Future[Int] = Future(i + 1)
}
```

to create a Sloth-aware Play controller:

```scala
class FooController extends SlothController {

  // Router instance with Api and Api implementation
  val router = Router[ByteBuffer, Future].route[FooApi](FooApiImpl)

  // Actions...
}
```

## Setup routes

Tell Play where to route your api calls:

```                  
# Ajax routing
GET     /ws               controllers.FooController.slothWebSocket
              
# and/or...

# Websocket routing
POST    /ajax/*path       controllers.FooController.slothAjax(path: String)
```

## Add wire handle(s)

Then you need to create one or more handles in the client for the protocol(s) you want to use to wire your API:

```scala
object wire extends WebClient {
  val fooAjax = clientAjax.wire[FooApi]
  val fooWs   = clientWs.wire[FooApi]
}
```

That's it.

## Call api from Client

Using the wire handles to call methods of the FooApi:

```scala
import client.wire._

fooAjax.incr(1).foreach { n =>
  println("got an incremented number from the server via Ajax: " + n)
}

// Or via WebSocket using a WS wire handle...

fooWs.incr(1).foreach { n =>
  println("got an incremented number from the server via WebSocket: " + n)
}
```

## Start application

    sbt
    run

Open [http://localhost:9000/](http://localhost:9000/) in the browser and enjoy the beautiful page and try its interactive calls via both Ajax and WebSocket.

Once a websocket connection has been established, repeated calls over the socket are almost 10x faster than Ajax calls that have to establish a connection on each call. Over the network the latency will likely minimize the difference (see [this blog](https://www.peterbe.com/plog/websockets-vs-xhr-2019)).
