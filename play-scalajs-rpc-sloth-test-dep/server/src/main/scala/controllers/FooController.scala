package controllers

import java.nio.ByteBuffer
import boopickle.Default._
import cats.implicits._
import play.api.mvc.{Action, AnyContent}
import playing.sloth.SlothController
import server.FooApiImpl
import server.page.ServerPage
import shared.FooApi
import sloth._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FooController extends SlothController {

  // Route instance with Api and Api implementation
  val router = Router[ByteBuffer, Future].route[FooApi](FooApiImpl)

  // Explicit actions
  def index(): Action[AnyContent] = Action(Ok(ServerPage()))
}