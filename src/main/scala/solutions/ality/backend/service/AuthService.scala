package solutions.ality.backend.service

import cats.SemigroupK
import cats.effect._
import com.typesafe.scalalogging.LazyLogging
import org.http4s._
import org.http4s.dsl.io._
import solutions.ality.backend.auth.AuthHelpers.User
import solutions.ality.backend.auth.{StatefulAuth, UserRepository}
import tsec.authentication.{TSecAuthService, asAuthed}

object AuthService extends LazyLogging {

  object UsernameQueryParam extends QueryParamDecoderMatcher[String]("username")
  object PasswordQueryParam extends QueryParamDecoderMatcher[String]("password")

  val login = HttpRoutes.of[IO] {
    case _ @ POST -> Root / "login" :? UsernameQueryParam(username) :? PasswordQueryParam(password) => {
      val response = for {
        user <- IO.fromOption(UserRepository.fetchUser(username, password))(new RuntimeException("username or password does not match"))
        resp <- Ok()
        auth <- StatefulAuth.jwtStatefulAuth.create(user.id)
        _    <- IO.delay(logger.info(s"$user logged in successfully."))
      } yield StatefulAuth.jwtStatefulAuth.embed(resp, auth)

      response.handleErrorWith(t => IO.delay(new Response(status = Unauthorized.withReason(t.getMessage))))
    }
  }

  val securedServices = StatefulAuth.Auth.liftService(TSecAuthService {
    case request @ POST -> Root / "logout" asAuthed user => {
      for {
        _    <- StatefulAuth.jwtStatefulAuth.discard(request.authenticator)
        resp <- Ok()
      } yield {
        logger.info(s"$user logged out successfully.")
        resp
      }
    }
    case request @ POST -> Root / "refresh" asAuthed user => {
      for {
        auth <- StatefulAuth.jwtStatefulAuth.renew(request.authenticator)
        resp <- Ok()
      } yield {
        logger.info(s"$user toked has been refreshed, new expire date is: ${auth.expiry}.")
        StatefulAuth.jwtStatefulAuth.embed(resp, auth)
      }
    }
    case request @ GET -> Root / "user" asAuthed user => {
      import UserEncoder.userEncoder
      logger.info(s"$user has accessed protected area. Token expired at: ${request.authenticator.expiry}")
      Ok(request.identity)
    }
  })

  import SemigroupK.ops._
  val api = login <+> securedServices
}

object UserEncoder {
  import cats.effect._
  import io.circe.generic.auto._
  import org.http4s._
  import org.http4s.circe._

  implicit def userEncoder: EntityEncoder[IO, User] = jsonEncoderOf[IO, User]
}
