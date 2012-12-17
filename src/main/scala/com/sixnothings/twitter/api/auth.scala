package com.sixnothings.twitter.api

import dispatch.oauth._
import com.sixnothings.config.{TwitterConfig, OCConfig}

class Auth(callback: String) extends Exchange
with DummyCallback
with SomeHttp
with TwitterConsumer
with TwitterEndpoints {
  val http = dispatch.Http
  val accessKey = TwitterConfig.accessKey
}

trait TwitterEndpoints extends SomeEndpoints {
  val requestToken = TwitterConfig.twitterUrls("requestToken")
  val accessToken  = TwitterConfig.twitterUrls("accessToken")
  val authorize    = TwitterConfig.twitterUrls("authorize")
}

trait DummyCallback extends SomeCallback {
  val callback = "http://ocremix.org"
}

trait TwitterConsumer extends SomeConsumer {
  val consumer = TwitterConfig.consumerKey
}
