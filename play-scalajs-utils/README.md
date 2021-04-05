# Play ScalaJS utility boilerplate library
    
Various small utils to aid in Play/ScalaJS projects. Import this to use in your project: 

    "com.marcgrue" %%% "playing-utils" % "0.1.0"

### HtmlTag

Allow Play Actions to receive html pages as a [Scalatag][Scalatags] by letting your Play controller classes extend the `HtmlTag` class:

    class FooController extends InjectedController with HtmlTag {
      def index(): Action[AnyContent] = Action(Ok(ServerPage()))
    }

instead of

    class FooController extends InjectedController {
      def index(): Action[AnyContent] = Action(Ok(ServerPage().render).as("text/html"))
    }

Let your controller extend the `HtmlTag` class. This also encodes the page as a "<!DOCTYPE html>" so that you don't have to add that to pages manually


### RxBindings

Various implicits to smooth using [scala.rx][scala.rx] with [Scalatags][Scalatags]. Like using variables() in logic or as tag result type: 
      
    // Logic in curEntity() Var determines css class
    td(
      cls := Rx(if (eid == curEntity()) "star" else "normal"),
      "Ben"
    )

    // createDbStatus() Var determines the content of the `div`
    div(
      Rx(
        createDbStatus() match {
          case None                             => ok("")
          case Some(msg) if msg.startsWith("_") => div("Important!: ", msg.tail)
          case Some(msg)                        => ok(msg)
        }
      )
    )

[scala.rx]: https://github.com/lihaoyi/scala.rx
[Scalatags]: https://github.com/com-lihaoyi/scalatags