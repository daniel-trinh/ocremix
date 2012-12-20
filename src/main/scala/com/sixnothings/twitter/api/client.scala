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
      |Error sending direct message to %s.screenName.
      |Failed message contents: %s
      |Error message: %s.getMessage
      """.format(user, message, error).stripMargin
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
      |Error message: %s.getMessage
      """.format(tweet, error).stripMargin
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
      |Error message: %s.getMessage
      """.format(error).stripMargin
    }.right.map { jsString => parse[TwitterConfiguration](jsString) }
  }
}


case class Tweet(message: String) {
  require(Tweet.tweetable(message) == true,
    """
      |Message is too long to tweet (t.co shortening has been accounted for).
      |Message: %s
    """.format(message).stripMargin)
  val content = message
}

case object Tweet {
  val charLimit = TwitterSettings.charLimit

  // this should match any valid http / ftp url, with or without the actual <protocol>:// string
  val urlRegex = """(((http|ftp|https|ftps|sftp)://)|(www\.))+(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(/[a-zA-Z0-9&amp;%_\./-~-]*)?""".r

  /**
   * This variable is intended to be periodically set every day, by loading
   * directly from Twitter's help/configuration.json API request.
   *
   * @return The contents of the twitterConfiguration Agent.
   */
  def twitterConfig: TwitterConfiguration = TwitterSettings.configuration()

  /**
   * The underscore casing is due to how Jerkson requires constructor attributes
   * to match key names
   */
  def shortUrlLength: Int      = twitterConfig.short_url_length
  def shortUrlLengthHttps: Int = twitterConfig.short_url_length_https

  /**
   * Will trim a message if it will be longer than the Tweet character limit.
   *
   * Will simply trim characters from the end of the tweet, plus 2 extra for appending "..",
   * until it is equal to or less than the character limit.
   *
   * WARNING: This method will trim all text from the end of the message param, regardless
   * of whether or not the trimmed text is part of a URL.
   *
   * @throws IllegalArgumentException when maxLimit is nonsensical (0 or less)
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
    require(maxLimit >= 1, "maxLimit must be non negative and not empty: %s".format(message))

    val messageLength = message.length()

    if (messageLength <= maxLimit)
      true
    else {
      if (messageLength - savedChars(message) <= 140) true else false
    }

    message.length() match {
      case length if length > maxLimit => {
        false
      }
      case length if length < 140 => true
    }
  }

  /**
   * Returns the number of characters a particular URL will count for within a tweet
   *
   * The max number of chars a URL can be is based off of the shortUrlLength and shortUrlLengthHttps
   * parameters. These characters are retrieved from twitter's help/configuration.json API call.
   *
   * @param url The url to check for how many characters it counts for. Does not check if it is
   *            a valid URL, that is intended to be done by the user of this method / with
   *            <Regex>.findAllIn.
   * @return The length of the URL after any possible t.co url shortening has occurred.
   */
  private def tcoShortenedUrlLength(url: String): Int = {
    val maxUrlLength = if (url startsWith "https://") shortUrlLengthHttps else shortUrlLength
    val urlLength = url.length

    if (urlLength <= maxUrlLength) urlLength else maxUrlLength
  }

  /**
   * Calculates the number of characters that will be saved when the URLs in the
   * specified message are shortened by Twitter's automatic t.co URL shortening service.
   *
   * @param message The message to analyze for URLs that can benefit from t.co shortening
   * @return The number of characters saved from t.co shortening, if any (default 0)
   */
  private def savedChars(message: String): Int = {

    val beforeAndAfterUrlLengths = urlRegex.findAllIn(message).foldLeft((0, 0)) {
      case ((origUrlLengths, shortenedUrlLengths), url) => {
        (origUrlLengths + url.length, shortUrlLength + tcoShortenedUrlLength(url))
      }
    }
    beforeAndAfterUrlLengths._1 - beforeAndAfterUrlLengths._2
  }
}
