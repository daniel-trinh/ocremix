package com.sixnothings.ocremix

import com.codahale.jerkson.Json._
import dispatch._
import java.util.concurrent.TimeUnit._
import scala.xml
import com.sixnothings.config.{ OCRemixSettings }

case object OCRemix extends Enumeration {
  val rssUrl = url(OCRemixSettings.rssUrl)
}

case class RemixEntry(
  remixer: String,
  title: String,
  game: String,
  mp3Url: String,
  youtubeUrl: String,
  writeupUrl: String,
  songId: Int) {

  val remixerTwit    = "!" + remixer
  val titleTwit      = "$" + title
  val gameTwit       = "%" + game
  val mp3UrlTwit     = "&" + mp3Url
  val youtubeUrlTwit = "*" +  youtubeUrl
  val writeupUrlTwit = "~" + writeupUrl
}

// Creates a tweetable string (less than 140 chars, w/ URLs counting as 20 each)
case class RemixTweet(entry: RemixEntry) {
  val remixer = "!" + "SongEntry.remixer"

  //def countChars
}

case object RSS {

  def fetch: Promise[Either[String, xml.Elem]] = {
    val response = Http(OCRemix.rssUrl OK as.xml.Elem).either
    val retriedResponse = retry.Backoff(10, Duration(1000, MILLISECONDS), 2)(response)
    for (exception <- retriedResponse.left)
     yield "Request to rss feed failed:" + exception.getMessage()
  }

  // def extractRemixEntry(xmlItem: scala.xml.Node): Either[String, RemixEntry] = {
  //   val description = xmlItem \ "description"
  //   val
  // }

  // def extractRemixes(xml: scala.xml.Elem): Either[String, List[RemixEntry]] = {
  //   val xmlItems = xml \ "rss" \ "channel" \ "item"
  //   for {
  //     item <- xmlItems
  //   } yield extractRemixEntry(item).right
  // }

  // def extractXml(promisedXml: Promise[Either[String, xml.Elem]]): scala.xml.Elem = {
  //   val x = for(x <- promisedXml)
  //     yield x.right
  // }

  // def extractRemixes(xml: scala.xml.Elem): List[RemixEntry] = {
  //   val remixes = xml \ "rss" \ "channel" \ "item" \

  // }

  // def tenLatestRemixes = {
  //    for (xmlEither <- fetchRSS)
  //      yield for {
  //        xml <- xmlEither.right
  //        xmlItem <- xml \ "rss" \ "channel" \ "item"
  //        remixes <- extractRemixEntry(xmlItem).right
  //      } yield remixes
  //  }

  //def newSongs: Stream[SongEntry]

  def diff = {
  }
}
