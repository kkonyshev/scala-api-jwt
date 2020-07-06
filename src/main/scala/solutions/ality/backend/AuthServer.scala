package solutions.ality.backend

import java.util.concurrent.Executors

import cats.effect._
import com.typesafe.scalalogging.LazyLogging
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import solutions.ality.backend.auth.UserRepository
import solutions.ality.backend.service.AuthService

import scala.concurrent.ExecutionContext

object AuthServer extends IOApp with LazyLogging with AppBanner {

  implicit val serverECtx: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors))

  val httpApp = Router("/api" -> AuthService.api).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](serverECtx)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .withBanner(banner)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}

trait AppBanner {
  val banner = List(
    s"!========================================",
    s"!  Test server has been started",
    s"!  Use following credential to access: ",
    s"!  login:  [POST] api/login?username=${UserRepository.testUsername}&password=${UserRepository.testPassword}",
    s"!  logout: [GET]  api/logout",
    s"!  get user's details: [GET]  api/user",
    s"========================================")
}
