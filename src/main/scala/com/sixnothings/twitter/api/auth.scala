package com.sixnothings.twitter.api

import dispatch.oauth._
import com.sixnothings.config.{TwitterSettings, OCRemixSettings}

class Auth(callback: String) extends Exchange
with DummyCallback
with SomeHttp
with TwitterConsumer
with TwitterEndpoints {
  val http = dispatch.Http
  val accessKey = TwitterSettings.accessKey
}

trait TwitterEndpoints extends SomeEndpoints {
  val requestToken = TwitterSettings.twitterUrls("requestToken")
  val accessToken  = TwitterSettings.twitterUrls("accessToken")
  val authorize    = TwitterSettings.twitterUrls("authorize")
}

trait DummyCallback extends SomeCallback {
  val callback = "http://ocremix.org"
}

trait TwitterConsumer extends SomeConsumer {
  val consumer = TwitterSettings.consumerKey
}
