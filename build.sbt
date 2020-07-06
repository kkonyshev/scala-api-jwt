name := "scala-api-jwt"

version := "0.1"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-Ypartial-unification")

val sttpVersion = "1.7.2"
val circleVersion = "0.12.0"
val http4sVersion = "0.21.4"
val slf4jVersion = "1.7.30"
val logbackVersion = "1.2.3"
val tsecV = "0.2.0"

val logging = Seq(
  "org.slf4j"       % "slf4j-api"         % slf4jVersion,
  "org.slf4j"       % "slf4j-simple"      % slf4jVersion,
  "org.slf4j"       % "log4j-over-slf4j"  % slf4jVersion,
  "ch.qos.logback"  % "logback-core"      % logbackVersion,
  "ch.qos.logback"  %  "logback-classic"  % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )

val http4sDependencies = Seq(
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion
  )

val tSecDependencies = Seq(
  "io.github.jmcardon" %% "tsec-common"         % tsecV,
  "io.github.jmcardon" %% "tsec-password"       % tsecV,
  "io.github.jmcardon" %% "tsec-cipher-jca"     % tsecV,
  "io.github.jmcardon" %% "tsec-cipher-bouncy"  % tsecV,
  "io.github.jmcardon" %% "tsec-mac"            % tsecV,
  "io.github.jmcardon" %% "tsec-signatures"     % tsecV,
  "io.github.jmcardon" %% "tsec-hash-jca"       % tsecV,
  "io.github.jmcardon" %% "tsec-hash-bouncy"    % tsecV,
  "io.github.jmcardon" %% "tsec-libsodium"      % "0.0.1-M11",
  "io.github.jmcardon" %% "tsec-jwt-mac"        % tsecV,
  "io.github.jmcardon" %% "tsec-jwt-sig"        % tsecV,
  "io.github.jmcardon" %% "tsec-http4s"         % tsecV
  )

val testDependency = Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
  )

libraryDependencies ++= logging
libraryDependencies ++= http4sDependencies
libraryDependencies ++= tSecDependencies
libraryDependencies ++= testDependency


enablePlugins(JavaAppPackaging)
// Disable javadoc packaging
mappings in (Compile, packageDoc) := Seq()

// docker packaging configuration
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)
mainClass in Compile := Some("solutions.ality.backend.AuthServer")

dockerBaseImage      := "openjdk:11-jre-slim"

// workaround for https://github.com/sbt/sbt-native-packager/issues/1202
daemonUserUid in Docker := None
daemonUser in Docker    := "daemon"
