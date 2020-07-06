package solutions.ality.backend.auth

import solutions.ality.backend.auth.AuthHelpers.User

object UserRepository {

  final val testUsername = "john"
  final val testPassword = "secret"

  def fetchUser(username: String, password: String): Option[User] = {
    (username, password) match {
      case (inputUser, inputPass) if inputUser == testUsername && inputPass == testPassword => Some(StatefulAuth.john)
      case _ => None
    }
  }
}
