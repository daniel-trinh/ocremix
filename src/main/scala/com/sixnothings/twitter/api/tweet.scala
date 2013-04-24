package com.sixnothings.twitter.api

import com.sixnothings.config.TwitterSettings
import com.sixnothings.twitter.json.TwitterConfiguration

case class Tweetable(message: String) {
  require(Tweetable.isTweetable(message) == true,
    """
      |Message is too long to tweet (t.co shortening has been accounted for).
      |Message: %s
    """.format(message).stripMargin)
  val content = message
}

case object Tweetable {
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
   * Returns the length of twitter's t.co service for http urls
   *
   * The underscore casing is due to how Jerkson requires constructor attributes
   * to match key names
   */
  def shortUrlLength: Int      = twitterConfig.short_url_length

  /**
   * Returns the length of twitter's t.co service for https urls
   *
   * The underscore casing is due to how Jerkson requires constructor attributes
   * to match key names
   */
  def shortUrlLengthHttps: Int = twitterConfig.short_url_length_https

  /**
   * Trims a message if it will be longer than the Tweetable character limit, otherwise does nothing.
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
  def trim(message: String, maxLimit: Int = charLimit): String = {
    require(maxLimit >= 0, "maxLimit must be non negative")
    if (message.length() > maxLimit)
      message.substring(0, maxLimit-2) + ".."
    else
      message
  }

  /**
   * Tells you if the message can be tweeted.
   *
   * Figures out if the message's final length (after t.co shortening) can be tweeted.
   *
   * WARNING: this does not necessarily reflect with 100% accuracy if the message is
   * tweetable or not. Since this method uses a regex on the entire method to find URL
   * matches, there are likely a few corner cases that Twitter's API might figure out
   * there are multiple URLs, but the regex does not (or vice versa) -- for example, the
   * regex used by this method will not detect multiple urls in this string:
   * "www.twitter.comwww.twitter.comwww.twitter.com".
   *
   * Format your messages in a sane manner (separate your URLs by spaces).
   *
   * @param message The message to check for tweetability.
   * @param maxLimit Loaded from TwitterSettings.charLimit by default -- pass this param
   *                 to override.
   * @return True if tweetable, false if not
   */
  def isTweetable(message: String, maxLimit: Int = charLimit): Boolean = {
    require(maxLimit >= 1, "maxLimit must be greater than one: %s".format(maxLimit))
    require(message.length() >= 1, "message length can't be an empty string.")

    val messageLength = message.length()

    if (messageLength <= maxLimit)
      true
    else {
      messageLength - savedChars(message) <= 140
    }
  }

  /**
   * Calculates the final expected number of characters of a message treated
   * as a potential Tweetable.
   * @param message
   * @return message length after any potential t.co url shortening
   */
  def expectedMessageLength(message: String): Int =
    message.length() - savedChars(message)

  def numCharsOverflow(message: String): Int =
    charLimit - expectedMessageLength(message)

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
        (origUrlLengths + url.length, shortenedUrlLengths + tcoShortenedUrlLength(url))
      }
    }
    beforeAndAfterUrlLengths._1 - beforeAndAfterUrlLengths._2
  }
}
