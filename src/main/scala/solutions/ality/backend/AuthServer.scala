package solutions.ality.backend

import java.util.concurrent.Executors

import cats.effect._
import com.typesafe.scalalogging.LazyLogging
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import solutions.ality.backend.service.AuthService

import scala.concurrent.ExecutionContext

object AuthServer extends IOApp with LazyLogging {

  implicit val serverECtx: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors))

  val httpApp = Router("/api" -> AuthService.api).orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](serverECtx)
    .bindHttp(8080, "0.0.0.0")
    .withHttpApp(httpApp)
    .serve
    .compile
    .drain
    .as(ExitCode.Success)

  logger.info(s"========================================")
  logger.info(s"Test server has been started")
  logger.info(s"Use following credential to access: ")
  logger.info(s"login:  [POST] api/login?username=${AuthService.testUsername}&password=${AuthService.testPassword}")
  logger.info(s"logout: [GET]  api/logout")
  logger.info(s"get user's details: [GET]  api/user")
  logger.info(s"========================================")
}
