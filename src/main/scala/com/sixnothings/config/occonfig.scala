package com.sixnothings.config
import scala.io.Source
import com.typesafe.config._
import com.ning.http.client.oauth._
import com.codahale.jerkson.Json._
import com.sixnothings.twitter.json._
import akka.agent.Agent
import com.sixnothings.maestro.MySystem


trait ProjectSettings {
  val base = ConfigFactory.load()
}

case object TwitterSettings extends ProjectSettings {
  val defaultConfiguration = Source.fromURL(getClass.getResource("/defaultTwitterConfiguration.json"))
  val configuration = Agent(parse[TwitterConfiguration](defaultConfiguration))(MySystem())

  // TODO: figure out less verbose way of loading values from conf..
  // probably use a .json file and parse it into a case class using a magic json parser --
  // that way I won't need to write a silly spec to make sure these config values are non nil.
  val twitterConf = base.getConfig("twitter")
  val handlesConf = twitterConf.getConfig("handles")
  val twitterUrlsConf = twitterConf.getConfig("urls")

  val charLimit = twitterConf.getInt("charLimit")

  val ocremixHandle = TwitterHandle(
    handlesConf.getConfig("ocremix").getString("name"),
    handlesConf.getConfig("ocremix").getString("id")
  )

  val panicHandle = TwitterHandle(
    handlesConf.getConfig("panic").getString("name"),
    handlesConf.getConfig("panic").getString("id")
  )

  val consumerKey = new ConsumerKey(
    twitterConf.getString("consumerKey"),
    twitterConf.getString("consumerSecret")
  )

  val accessKey = new RequestToken(
    twitterConf.getString("accessKey"),
    twitterConf.getString("accessSecret")
  )

  val urlKeys = List("requestToken", "accessToken", "authorize", "api")

  val twitterUrls = urlKeys.foldLeft(Map[String, String]()) {(
    (urls, key) => urls + (key -> twitterUrlsConf.getString(key)))
  }

}

case object OCRemixSettings extends ProjectSettings {
  val rssUrl = base.getConfig("ocremix").getConfig("urls").getString("ocremixRss")
}

case class TwitterHandle(screenName: String, id: String)
