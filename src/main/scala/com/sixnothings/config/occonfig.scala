package com.sixnothings.config

import com.typesafe.config._
import com.ning.http.client.oauth._

trait ProjectConfig {
  val base = ConfigFactory.load()
}

case object TwitterConfig extends ProjectConfig {

  val twitterConfig = base.getConfig("twitter")
  val handlesConfig = twitterConfig.getConfig("handles")
  val twitterUrlsConfig = twitterConfig.getConfig("urls")

  val charLimit = twitterConfig.getInt("charLimit")

  val ocremixHandle = TwitterHandle(
    "ocremix",
    handlesConfig.getConfig("ocremix").getString("id")
  )

  val panicHandle = TwitterHandle(
    "panic",
    handlesConfig.getConfig("panic").getString("id")
  )

  val consumerKey       = new ConsumerKey(
    twitterConfig.getString("consumerKey"),
    twitterConfig.getString("consumerSecret")
  )

  val accessKey = new RequestToken(
    twitterConfig.getString("accessKey"),
    twitterConfig.getString("accessSecret")
  )


  val urlKeys = List("requestToken", "accessToken", "authorize", "api")

  val twitterUrls = urlKeys.foldLeft(Map[String, String]()) {(
    (urls, key) => urls + (key -> twitterUrlsConfig.getString(key)))
  }

}

case object OCConfig {
  // TODO: figure out less verbose way of loading values from conf..
  // probably use a .json file with jerkson instead.

  val base = ConfigFactory.load()
  val rssUrl = base.getConfig("ocremix").getConfig("urls").getString("ocremixRss")

}

case class TwitterHandle(screenName: String, id: String)
