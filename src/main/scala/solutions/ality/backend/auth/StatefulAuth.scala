package solutions.ality.backend.auth

import cats.Id
import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import tsec.authentication._
import tsec.common.SecureRandomId
import tsec.mac.jca.{HMACSHA256, MacSigningKey}

import scala.concurrent.duration._

object StatefulAuth  extends LazyLogging {

  import AuthHelpers._

  val jwtStore = dummyBackingStore[IO, SecureRandomId, AugmentedJWT[HMACSHA256, Int]](s => SecureRandomId.coerce(s.id))

  //We create a way to store our users. You can attach this to say, your doobie accessor
  val userStore: BackingStore[IO, Int, User] = dummyBackingStore[IO, Int, User](_.id)

  val john = User(1, 33, "John Dow")
  userStore.put(john)

  //Our signing key. Instantiate in a safe way using .generateKey[F]
  val signingKey: MacSigningKey[HMACSHA256] = HMACSHA256.generateKey[Id]

  val jwtStatefulAuth =
    JWTAuthenticator.backed.inBearerToken(
      expiryDuration = 10.minutes, //Absolute expiration time
      maxIdle        = None,
      tokenStore     = jwtStore,
      identityStore  = userStore,
      signingKey     = signingKey
      )


  val Auth = SecuredRequestHandler(jwtStatefulAuth)
}
