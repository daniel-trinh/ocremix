package com.sixnothings.twitter.api

import dispatch._
import dispatch.oauth._
import com.codahale.jerkson.Json._
import java.net.URLEncoder
import com.sixnothings.config._
import com.sixnothings.twitter.json._

class ApiClient(someOauth: Auth) {
  val twitterOauth = someOauth

  def sendError(error: String) = "yep"

  def asyncSendError = "yep"

  def api = url(TwitterSettings.twitterUrls("api"))

  def directMessage(user: TwitterHandle, message: String): Promise[Either[String,String]] = {
    Http(
      api / "direct_messages" / "new.json"
      << Map(
        "user_id"     -> user.id,
        "screen_name" -> user.screenName,
        "text"        -> message
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).either.left.map { error =>
      """
      |Error sending direct message to %s.
      |Failed message contents: %s
      |Error message: %s
      """.format(user.screenName, message, error.getMessage).stripMargin
    }
  }

  def statusesUpdate(tweet: Tweet): Promise[Either[String,String]] = {
    Http(
      api / "statuses" / "update.json"
      << Map (
        "status"    -> tweet.content,
        "trim_user" -> "t"
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).either.left.map { error =>
      """
      |Error posting tweet.
      |Failed tweet message: %s
      |Error message: %s
      """.format(tweet, error.getMessage).stripMargin
    }
  }

  // this is pretty much just used to test that talking to twitter's API works
  def helpConfiguration: Promise[Either[String, TwitterConfiguration]] = {
    Http(
      api / "help" / "configuration.json"
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).either.left.map { error =>
      """
      |Error retrieving configuration.
      |Error message: %s
      """.format(error.getMessage).stripMargin
    }.right.map { jsString => parse[TwitterConfiguration](jsString) }
  }
}