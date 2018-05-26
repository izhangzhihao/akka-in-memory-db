name := "akka-in-memory-db"
organization in ThisBuild := "com.github.izhangzhihao"
scalaVersion in ThisBuild := "2.12.5"
version := "0.1.0-SNAPSHOT"

lazy val akkaVersion = "2.5.12"

lazy val akkaTypedVersion = "2.5.8"

lazy val commonDependencies = Seq(
  //"com.typesafe.akka" %% "akka-typed" % akkaTypedVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  //"com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  //"com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion % Test
)

lazy val commons = project
  .settings(
    name := "commons",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val client = project
  .settings(
    name := "client",
    settings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(commons)

lazy val server = project
  .settings(
    name := "server",
    settings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(commons)

lazy val run = project // just for play
  .settings(
    name := "run",
    settings,
  ).dependsOn(server, client)

lazy val settings = Seq(
  scalacOptions ++= Seq(
    "-unchecked",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-deprecation",
    "-encoding",
    "utf8"
  ),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)