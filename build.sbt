name := "Make ocremix easier to use"

version := "0.1"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype Repo" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "repo.codahale.com" at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.8" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration" % "test",
  "com.codahale" % "jerkson_2.9.1" % "0.5.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.4",
  "com.typesafe" % "config" % "1.0.0",
  "com.typesafe.akka" % "akka-actor" % "2.0.4",
  "com.typesafe.akka" % "akka-agent" % "2.0.4"
)

//jerkson is broken with 2.10...
//scalaVersion := "2.10.0-RC5"
//
//libraryDependencies ++= Seq(
//  "org.scalatest" % "scalatest_2.10.0-RC5" % "1.8-B1",
//  "com.codahale" % "jerkson_2.9.1" % "0.5.0",
//  "net.databinder.dispatch" %% "dispatch-core" % "0.9.4",
//  "com.typesafe" % "config" % "1.0.0",
//  "com.typesafe.akka" %% "akka-actor" % "2.1.0-RC6" cross CrossVersion.full,
//  "com.typesafe.akka" %% "akka-agent" % "2.1.0-RC6" cross CrossVersion.full
//)
