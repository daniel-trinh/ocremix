package com.sixnothings.twitter.api

import dispatch._, Defaults._
import dispatch.oauth._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import com.sixnothings.config._
import com.sixnothings.twitter.json._

class ApiClient(someOauth: Auth) {
  implicit val formats = org.json4s.DefaultFormats
  val twitterOauth = someOauth

  def api = url(TwitterSettings.twitterUrls("api"))

  /**
   * Sends a Twitter direct message.
   *
   * @param user The user to send a direct message to
   * @param message The message to send to the user.
   * @return Returns an Either containing a Right(success) message, or a Left(error) message.
   */
  def directMessage(user: TwitterHandle, message: String): Future[String] = {
    Http(
      api / "direct_messages" / "new.json"
      << Map(
        "user_id"     -> user.id,
        "screen_name" -> user.screenName,
        "text"        -> message
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).transform (
      identity,
      { error =>
        new Exception("""
        |Error sending direct message to %s.
        |Failed message contents: %s
        |Error message: %s
        """.format(user.screenName, message, error.getMessage).stripMargin)
      }
    )
  }

  /**
   * Used to "tweet" a new status (the kind that has a 140 character limit).
   *
   * @param tweet A message to tweet.
   * @return Returns a Promised Either containing a Right(success) message, or a Left(error) message.
   */
  def statusesUpdate(tweet: Tweetable): Future[String] = {
    Http(
      api / "statuses" / "update.json"
      << Map (
        "status"    -> tweet.content,
        "trim_user" -> "t"
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).transform ( identity, error =>
      new Exception("""
      |Error posting tweet.
      |Failed tweet message: %s
      |Error message: %s
      """.format(tweet, error.getMessage).stripMargin)
    )
  }

  /**
   * Used to retrieve a user's tweet timeline.
   *
   * @param userId The numerical Twitter ID of the user to retrieve timeline info from.
   * @param screenName The name of the user to retrieve timeline info from.
   * @param count The number of tweets to retrieve from the user's timeline.
   * @return Returns an Either containing a Right(success) message, or a Left(error) message.
   */
  def userTimeline(
    userId: String,
    screenName: String,
    count: Int): Future[String] = {
    Http(
      api / "statuses" / "user_timeline.json"
      <<? Map (
        "user_id" -> userId,
        "screen_name" -> screenName,
        "trim_user" -> "t",
        "count" -> count.toString
      )
      sign (twitterOauth.consumer, twitterOauth.accessKey)
      OK as.String
    ).transform (
      { x => x },
      { error =>
        new Exception("""
          |Error retrieving timeline.
          |userId: %d
          |screenName: %s
          |count: %d
        """.stripMargin.format(userId, screenName, count))
      }
    )
  }

  /**
   * Used to retrieve Twitter's website configuration data. Contains info about t.co url length.
   *
   * @return Returns an Either containing a Right(success) message, or a Left(error) message.
   */
  def helpConfiguration: Future[TwitterConfiguration] = {
    Http(
      api / "help" / "configuration.json"
        sign(twitterOauth.consumer, twitterOauth.accessKey)
        OK as.String
    ).transform(
    { x => parse(x).extract[TwitterConfiguration]}, { error =>
      new Exception(
        """
          |Error retrieving configuration.
          |Error message: %s
        """
          .format(error.getMessage).stripMargin)
      }
    )
  }
}