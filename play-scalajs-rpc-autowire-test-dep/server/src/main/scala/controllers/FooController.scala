package controllers

import boopickle.Default._
import javax.inject._
import play.api.mvc.{Action, AnyContent}
import playing.autowire._
import server.FooApiImpl
import server.page.FooPage
import shared.FooApi
import scala.concurrent.ExecutionContext

// Inject API server implementation

@Singleton
class FooController @Inject()(api: FooApiImpl)(implicit ec: ExecutionContext) extends AutowireController {

  // Route api instance with shared Api type
  val autowireRouter = AutowireServer.route[FooApi](api)

  // Explicit actions
  def index(): Action[AnyContent] = Action(Ok(FooPage()))
}