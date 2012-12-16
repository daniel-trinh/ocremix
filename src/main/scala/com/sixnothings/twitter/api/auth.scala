package com.sixnothings.twitter.api

import dispatch.oauth._
import com.sixnothings.config.{ OCConfig }

class Auth(callback: String) extends Exchange
with DummyCallback
with SomeHttp
with TwitterConsumer
with TwitterEndpoints {
  val http = dispatch.Http
  val accessKey = OCConfig.accessKey
}

trait TwitterEndpoints extends SomeEndpoints {
  val requestToken = OCConfig.twitterUrls("requestToken")
  val accessToken  = OCConfig.twitterUrls("accessToken")
  val authorize    = OCConfig.twitterUrls("authorize")
}

trait DummyCallback extends SomeCallback {
  val callback = "http://ocremix.org"
}

trait TwitterConsumer extends SomeConsumer {
  val consumer = OCConfig.consumerKey
}
