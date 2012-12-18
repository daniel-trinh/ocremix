package com.sixnothings.twitter.api

import dispatch._
import dispatch.oauth._
import com.codahale.jerkson.Json._
import java.net.URLEncoder
import com.sixnothings.config._
import com.sixnothings.twitter.json._

trait Tweetable {
  val twitterOauth: Auth
  val charLimit = TwitterSettings.charLimit

  // this should match any valid http / ftp url, with or without the actual <protocol>:// string
  val urlRegex = """(((http|ftp|https|ftps|sftp)://)|(www\.))+(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(/[a-zA-Z0-9&amp;%_\./-~-]*)?""".r

  /**
   * This variable is intended to be periodically set every day, by loading
   * directly from Twitter's help/configuration.json API request.
   *
   * @return The contents of the twitterConfiguration Agent.
   */
  def twitterConfig: TwitterConfiguration = TwitterSettings.twitterConfiguration()

  /**
   * The underscore casing is due to how Jerkson requires constructor attributes
   * to match key names
   */
  def shortUrlLength: Int      = twitterConfig.short_url_length
  def shortUrlLengthHttps: Int = twitterConfig.short_url_length_https

  def api = url(TwitterSettings.twitterUrls("api"))

  /**
   * Will trim a message if it will be longer than the Tweet character limit.
   *
   * WARNING: This method will trim all text from the end of the message param, including
   * URLS.
   *
   * @throws IllegalArgumentException
   * Will simply trim characters from the end of the tweet, plus 2 for '.."
   *
   * @param message The message to check and trim if tweetable.
   * @param maxLimit Loaded from TwitterSettings.charLimit by default -- pass this param
   *                 to override.
   * @return Trimmed message.
   */

  def trimMessage(message: String, maxLimit: Int = charLimit): String = {
    require(maxLimit >= 0, "maxLimit must be non negative")

    message.length() match {
      case length if length > maxLimit => { }
      case length if length < 3 => { }
    }
    message.substring(0, message.length())
  }

  /**
   * Tells you if the message can be tweeted.
   *
   * Figures out if the message's final length (after t.co shortening) can be tweeted.
   *
   * @param message The message to check for tweetability.
   * @param maxLimit Loaded from TwitterSettings.charLimit by default -- pass this param
   *                 to override.
   * @return True if tweetable, false if not
   */

  def tweetable(message: String, maxLimit: Int = charLimit): Boolean = {
    require(maxLimit >= 1, s"maxLimit must be non negative and not empty: $message")
    urlRegex.findAllIn(message) foreach {
      url => {
        val maxUrlLength = if (url startsWith "https") shortUrlLength else shortUrlLengthHttps
        val urlLength = url.length

        if (urlLength <= maxUrlLength)
          true
        else if (urlLength >= maxUrlLength) {

        }
      }
    }

    message.length() match {
      case length if length > maxLimit => {
        false
      }
      case length if length < 140 => true
    }
  }
}

class TwitterClient(someOauth: Auth) extends Tweetable {
  val twitterOauth = someOauth

  def sendError(error: String) = ???

  def asyncSendError = ???

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
      s"""
      Error sending direct message to $user.screenName.
      Failed message contents: $error.getMessage
      Error message: %s
      """.stripMargin
    }
  }

  def statusesUpdate(message: String): Promise[Either[String,String]] = {
    Http(
      api / "statuses" / "update.json"
      << Map (
        "status"    -> trimMessage(message),
        "trim_user" -> "t"
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).either.left.map { error =>
      s"""
      Error posting tweet.
      Failed tweet message: $message
      Error message: $error.getMessage
      """.stripMargin
    }
  }

  // this is pretty much just used to test that talking to twitter's API works
  def helpConfiguration: Promise[Either[String, TwitterConfiguration]] = {
    Http(
      api / "help" / "configuration.json"
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).either.left.map { error =>
      s"""
      Error retrieving configuration.
      Error message: $error.getMessage
      """.stripMargin
    }.right.map { jsString => parse[TwitterConfiguration](jsString) }
  }

}
