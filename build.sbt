import com.typesafe.sbt.SbtStartScript

name := "Make ocremix easier to use"

version := "0.3"

scalaVersion := "2.11.2"

resolvers ++= Seq(
  "Sonatype Repo" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "repo.codahale.com" at "http://repo.codahale.com"
)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.7.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
  "com.typesafe" % "config" % "1.0.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-agent" % "2.3.4"
)

seq(SbtStartScript.startScriptForClassesSettings: _*)