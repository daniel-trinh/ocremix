package com.sixnothings.ocremix

import com.codahale.jerkson.Json._
import dispatch._
import java.util.concurrent.TimeUnit._
import scala.xml
import com.sixnothings.config.{TwitterSettings, OCRemixSettings}
import com.sixnothings.twitter.api.Tweetable
import com.ning.http.client.Response
import xml.XML

case object OCRemix extends Enumeration {
  val rssUrl = url(OCRemixSettings.rssUrl)
}

/**
 * Holds information of a particular OCRemix entry.
 *
 * @param title Remix name (and usually game name as well).
 * @param youtubeUrl Link to listen to remix on youtube.
 * @param writeupUrl Link to direct download remix or look at reviews.
 * @param songId Unique ID for remix, defined by OCR. Every remix has an ID.
 */
case class RemixEntry(
  remixers: List[Remixer],
  composers: List[Composer],
  game: Game,
  title: String,
  youtubeUrl: String,
  writeupUrl: String,
  songId: Int) {

  // TODO: refactor this method to be less boilerplatey
  def toTweetable: Tweetable = {

    lazy val v0 = "%d: %s by %s Original by %s Y: %s W: %s".
      format(songId, title, remixItemsToString(remixers), remixItemsToString(composers), youtubeUrl, writeupUrl)

    lazy val v1 = "%d: %s by %s Y: %s W: %s".
      format(songId, title, remixItemsToString(remixers), youtubeUrl, writeupUrl)

    lazy val v2 = "%d: %s. Y: %s W: %s".
      format(songId, title, youtubeUrl, writeupUrl)

    lazy val v3 = "%d: %s. Y: %s".
      format(songId, title, youtubeUrl)

    lazy val v4 = "%d. Y: %s".
      format(songId, youtubeUrl)

    if (Tweetable.isTweetable(v0))
      Tweetable(v0)
    else if (Tweetable.isTweetable(v1))
      Tweetable(v1)
    else if (Tweetable.isTweetable(v2))
      Tweetable(v2)
    else if (Tweetable.isTweetable(v3))
      Tweetable(v3)
    else
      Tweetable(v4)
  }

  private def remixItemsToString(remixers: List[RemixItem]): String = {
    remixers match {
      case head :: tail => {
        head.name + tail.foldLeft("") {
          (accu, remixer) => accu + ", " + remixer.name
        } + "."
      }
      case Nil => ""
    }
  }
}

trait RemixItem {
  val url: String
  val name: String
}

case class Remixer(url: String, name: String) extends RemixItem

case class Composer(url: String, name: String) extends RemixItem

case class Game(url: String, name: String) extends RemixItem


case object RSS {

  val descriptionSplitterRegex = """(?s).*ReMix of(.+) by (.+)Original soundtrack by(.+)""".r

  // used to filter out the game and artist info from the description node of the
  // ocremix rss 2.0 response.
//  val itemDescriptionRegex = """(Original soundtrack by|by|ReMix of)? <a href="(http[s]?://w{3}\.?ocremix.org/(game|artist)/\d+/[\w\s\-%\.\(\)]*)">([\w~\.\s]+)</a>""".r
  val itemDescriptionRegex = """(?s)<a href="(http[s]?://w{3}\.?ocremix.org/(game|artist)/\d+/[^>]*)/?">([^>]+)</a>""".r

  val noDTDRegex = """(?s)<!DOCTYPE [^>]+>(.+)""".r

  // matches an embedded youtube link, such as those found on a ocremix song page.
  // the capture group will match the unique video ID in the youtube link.
  val embeddedYoutubeRegex = """http[s]?://w{3}\.?youtube.com/v/([\w+\-]+)[.&=\d\w\s]*""".r

  val songIdRegex = """.*remix/OCR(\d+)/.*""".r

  def fetch: Promise[Either[String, xml.Elem]] = {
    val response = Http(OCRemix.rssUrl OK as.xml.Elem).either
    val retriedResponse = retry.Backoff(5, Duration(1000, MILLISECONDS), 2)(response)
    retriedResponse.left.map { error =>
      """
        |Error fetching OCRemix RSS.
        |Error message: %s
      """.format(error.getMessage).stripMargin
    }
  }

  def extractRemixes(xml: scala.xml.Elem): List[RemixEntry] = {
    val xmlItems = xml \\ "channel" \ "item"
    (for { item <- xmlItems } yield extractRemixEntry(item)).toList
  }

  private def extractRemixEntry(xmlItem: scala.xml.Node): RemixEntry = {
    val remixLink = xmlItem \ "guid" text
    val songIdRegex(songId) = remixLink

    val descriptionSplitterRegex(gameGroup, remixersGroup, artistsGroup) = xmlItem \ "description" text

    RemixEntry(
      remixers = extractRemixers(remixersGroup),
      composers = extractComposers(artistsGroup),
      game = extractGame(gameGroup),
      title = xmlItem \ "title" text,
      youtubeUrl = extractYoutubeLink(remixLink),
      writeupUrl = remixLink,
      songId = songId.toInt
    )
  }

  /**
   * Follows redirected links until a non 3xx status code is given or a limit is reached
   */
  private def followRedirects(link: String, limit: Int = 10): Response = {
    assert(
      limit > 0,
      """Follow redirect limit has been reached, maybe in a redirect loop?
        |Link: %s""".stripMargin.format(link)
    )
   val response = Http(url(link))()

    response.getStatusCode match {
      case code if code / 100 == 2 => response
      case code if code / 100 == 3 => followRedirects(response.getHeader("Location"), limit - 1)
    }
  }

  /**
   * The Youtube URL from crawling the OCRemix link isn't suitable for tweeting, so modify
   * it so it is shareable.
   * @param remixLink a valid OCRemix song URL
   * @throws MatchError when no youtube URL is found
   * @return A shareable youtube link of the OCRemix song.
   */
  private def extractYoutubeLink(remixLink: String): String = {

    // parse out DTD since there is no way to cache the DTD download, and each download takes minutes
    val noDTDRegex(noDocTypeBody) = followRedirects(remixLink).getResponseBody

    // hack to remove chars that require a proper DTD
    val responseBody = XML.loadString(noDocTypeBody.replace("&nbsp;", "&#160;").replace("&raquo;","&#187;"))

    val embeddedYoutubeUrl = (responseBody \\ "_").filter {
      _.attribute("id").filter(_.text=="ytplayer").isDefined
    } \ "@data" text

    val embeddedYoutubeRegex(videoId) = embeddedYoutubeUrl

    "http://www.youtube.com/watch?&v=" + videoId
  }

  private def extractRemixers(remixerGroup: String): List[Remixer] = {
    itemDescriptionRegex.findAllIn(remixerGroup).foldLeft(List[Remixer]()) { (accu, item) =>
      val itemDescriptionRegex(url, _, name) = item
      Remixer(url, name) :: accu
    }
  }

  private def extractComposers(composerGroup: String): List[Composer]  = {
    itemDescriptionRegex.findAllIn(composerGroup).foldLeft(List[Composer]()) { (accu, item) =>
      val itemDescriptionRegex(url, _, name) = item
      Composer(url, name) :: accu
    }
  }

  private def extractGame(gameGroup: String): Game = {
    val itemDescriptionRegex(url, _, name) = itemDescriptionRegex.findFirstIn(gameGroup).get
    Game(url, name)
  }

  def htmlDecode(str: String): String = {
    // Kind of ugly and inefficient, but it works
    str.replace("&amp;","&").
      replace("&lt;", "<").
      replace("&gt;", ">").
      replace("&quot;", "\"").
      replace("&apos;", "\'").
      replace("&nbsp;", " ")
  }
}
