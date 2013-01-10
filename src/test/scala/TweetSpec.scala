import com.codahale.jerkson.AST.JValue
import com.sixnothings.twitter.api.{Auth, ApiClient}
import com.sixnothings.twitter.json.Tweet
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers
import com.codahale.jerkson.Json._

class TweetSpec extends FunSpec with BeforeAndAfter with ShouldMatchers {

  val ocremixTweet =
    """{
      |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
      |    "id": 289264432092180480,
      |    "id_str": "289264432092180480",
      |    "text": "2576: Super Dodge Ball 'Almost Frozen' by Rexy, Monobrow. Original by Kazuo Sawa. Y: http:\/\/t.co\/GLWc4ofR W: http:\/\/t.co\/dFqxOs0X",
      |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
      |    "truncated": false,
      |    "in_reply_to_status_id": null,
      |    "in_reply_to_status_id_str": null,
      |    "in_reply_to_user_id": null,
      |    "in_reply_to_user_id_str": null,
      |    "in_reply_to_screen_name": null,
      |    "user": {
      |        "id": 123158055,
      |        "id_str": "123158055"
      |    },
      |    "geo": null,
      |    "coordinates": null,
      |    "place": null,
      |    "contributors": null,
      |    "retweet_count": 0,
      |    "entities": {
      |        "hashtags": [],
      |        "urls": [{
      |            "url": "http:\/\/t.co\/GLWc4ofR",
      |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=blR_HlD9N2A",
      |            "display_url": "youtube.com\/watch?&v=blR_H\u2026",
      |            "indices": [85, 105]
      |        }, {
      |            "url": "http:\/\/t.co\/dFqxOs0X",
      |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02576\/",
      |            "display_url": "ocremix.org\/remix\/OCR02576\/",
      |            "indices": [109, 129]
      |        }],
      |        "user_mentions": []
      |    },
      |    "favorited": false,
      |    "retweeted": false,
      |    "possibly_sensitive": false
      |}
    """.stripMargin

  val ocremixTweets = """
    |[{
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264432092180480,
    |    "id_str": "289264432092180480",
    |    "text": "2576: Super Dodge Ball 'Almost Frozen' by Rexy, Monobrow. Original by Kazuo Sawa. Y: http:\/\/t.co\/GLWc4ofR W: http:\/\/t.co\/dFqxOs0X",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/GLWc4ofR",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=blR_HlD9N2A",
    |            "display_url": "youtube.com\/watch?&v=blR_H\u2026",
    |            "indices": [85, 105]
    |        }, {
    |            "url": "http:\/\/t.co\/dFqxOs0X",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02576\/",
    |            "display_url": "ocremix.org\/remix\/OCR02576\/",
    |            "indices": [109, 129]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431853080576,
    |    "id_str": "289264431853080576",
    |    "text": "2577: The Legend of Zelda: Ocarina of Time 'Solace' by The Joker, 2P. Y: http:\/\/t.co\/iNKexaV5 W: http:\/\/t.co\/6Xq8oJ4y",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/iNKexaV5",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=cihZX0e2EBY",
    |            "display_url": "youtube.com\/watch?&v=cihZX\u2026",
    |            "indices": [73, 93]
    |        }, {
    |            "url": "http:\/\/t.co\/6Xq8oJ4y",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02577\/",
    |            "display_url": "ocremix.org\/remix\/OCR02577\/",
    |            "indices": [97, 117]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431840501762,
    |    "id_str": "289264431840501762",
    |    "text": "2572: Chrono Cross 'A Dream Between Worlds' by Chris ~ Amaterasu, Avitron. Y: http:\/\/t.co\/bChhiw93 W: http:\/\/t.co\/z5VGiIdd",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/bChhiw93",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=g9sp3De7ocA",
    |            "display_url": "youtube.com\/watch?&v=g9sp3\u2026",
    |            "indices": [78, 98]
    |        }, {
    |            "url": "http:\/\/t.co\/z5VGiIdd",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02572\/",
    |            "display_url": "ocremix.org\/remix\/OCR02572\/",
    |            "indices": [102, 122]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431819542529,
    |    "id_str": "289264431819542529",
    |    "text": "2574: Ys IV: The Dawn of Ys 'Hero of Celceta' by Scaredsim, OA. Y: http:\/\/t.co\/nURI5g36 W: http:\/\/t.co\/x5vCS8sT",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/nURI5g36",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=j1YlN4tCnWE",
    |            "display_url": "youtube.com\/watch?&v=j1YlN\u2026",
    |            "indices": [67, 87]
    |        }, {
    |            "url": "http:\/\/t.co\/x5vCS8sT",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02574\/",
    |            "display_url": "ocremix.org\/remix\/OCR02574\/",
    |            "indices": [91, 111]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431811158016,
    |    "id_str": "289264431811158016",
    |    "text": "2575: Chrono Trigger 'Trigger, Please' by Shnabubula. Y: http:\/\/t.co\/welGhgS6 W: http:\/\/t.co\/7XJtPc4z",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/welGhgS6",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=jxOUM8Rxx-M",
    |            "display_url": "youtube.com\/watch?&v=jxOUM\u2026",
    |            "indices": [57, 77]
    |        }, {
    |            "url": "http:\/\/t.co\/7XJtPc4z",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02575\/",
    |            "display_url": "ocremix.org\/remix\/OCR02575\/",
    |            "indices": [81, 101]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431798566913,
    |    "id_str": "289264431798566913",
    |    "text": "2570: Scott Pilgrim vs. The World: The Game '1-UP' by Brandon Strader. Y: http:\/\/t.co\/7aX0P2nA W: http:\/\/t.co\/3qnHIDEJ",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/7aX0P2nA",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=5Co4mtubixw",
    |            "display_url": "youtube.com\/watch?&v=5Co4m\u2026",
    |            "indices": [74, 94]
    |        }, {
    |            "url": "http:\/\/t.co\/3qnHIDEJ",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02570\/",
    |            "display_url": "ocremix.org\/remix\/OCR02570\/",
    |            "indices": [98, 118]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431785967617,
    |    "id_str": "289264431785967617",
    |    "text": "2571: Ristar 'Stars on Ice' by Rexy, DusK. Y: http:\/\/t.co\/JdVLEI55 W: http:\/\/t.co\/ZtBQjNHj",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/JdVLEI55",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=9RABA5jyU-w",
    |            "display_url": "youtube.com\/watch?&v=9RABA\u2026",
    |            "indices": [46, 66]
    |        }, {
    |            "url": "http:\/\/t.co\/ZtBQjNHj",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02571\/",
    |            "display_url": "ocremix.org\/remix\/OCR02571\/",
    |            "indices": [70, 90]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431777599488,
    |    "id_str": "289264431777599488",
    |    "text": "2573: Metal Gear Solid 3: Snake Eater 'Innocent Deception' by Dj Mystix, Claire Yaxley. Y: http:\/\/t.co\/kK6AhqBz W: http:\/\/t.co\/BsUAOdUL",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/kK6AhqBz",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=0tkIij0Bn-o",
    |            "display_url": "youtube.com\/watch?&v=0tkIi\u2026",
    |            "indices": [91, 111]
    |        }, {
    |            "url": "http:\/\/t.co\/BsUAOdUL",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02573\/",
    |            "display_url": "ocremix.org\/remix\/OCR02573\/",
    |            "indices": [115, 135]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264431442038785,
    |    "id_str": "289264431442038785",
    |    "text": "2569: Donkey Kong Country 3: Dixie Kong's Double Trouble! 'mojo gogo' by prophetik. Y: http:\/\/t.co\/qHQj0806 W: http:\/\/t.co\/L1MIN9by",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/qHQj0806",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=kzPNyzN_g1c",
    |            "display_url": "youtube.com\/watch?&v=kzPNy\u2026",
    |            "indices": [87, 107]
    |        }, {
    |            "url": "http:\/\/t.co\/L1MIN9by",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02569\/",
    |            "display_url": "ocremix.org\/remix\/OCR02569\/",
    |            "indices": [111, 131]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}, {
    |    "created_at": "Thu Jan 10 06:56:29 +0000 2013",
    |    "id": 289264430699655168,
    |    "id_str": "289264430699655168",
    |    "text": "2568: The Legend of Zelda: Majora's Mask 'Dawn of a New Day'. Y: http:\/\/t.co\/lULSGgBG W: http:\/\/t.co\/1iFYp4g1",
    |    "source": "\u003ca href=\"http:\/\/ocremix.org\" rel=\"nofollow\"\u003eOCRemix RSS to Tweets\u003c\/a\u003e",
    |    "truncated": false,
    |    "in_reply_to_status_id": null,
    |    "in_reply_to_status_id_str": null,
    |    "in_reply_to_user_id": null,
    |    "in_reply_to_user_id_str": null,
    |    "in_reply_to_screen_name": null,
    |    "user": {
    |        "id": 123158055,
    |        "id_str": "123158055"
    |    },
    |    "geo": null,
    |    "coordinates": null,
    |    "place": null,
    |    "contributors": null,
    |    "retweet_count": 0,
    |    "entities": {
    |        "hashtags": [],
    |        "urls": [{
    |            "url": "http:\/\/t.co\/lULSGgBG",
    |            "expanded_url": "http:\/\/www.youtube.com\/watch?&v=1NryFD9_hR0",
    |            "display_url": "youtube.com\/watch?&v=1NryF\u2026",
    |            "indices": [65, 85]
    |        }, {
    |            "url": "http:\/\/t.co\/1iFYp4g1",
    |            "expanded_url": "http:\/\/www.ocremix.org\/remix\/OCR02568\/",
    |            "display_url": "ocremix.org\/remix\/OCR02568\/",
    |            "indices": [89, 109]
    |        }],
    |        "user_mentions": []
    |    },
    |    "favorited": false,
    |    "retweeted": false,
    |    "possibly_sensitive": false
    |}]
  """.stripMargin

  val firstTweet =
    "2576: Super Dodge Ball 'Almost Frozen' by Rexy, Monobrow. Original by Kazuo Sawa. Y: http://t.co/GLWc4ofR W: http://t.co/dFqxOs0X"

  describe("Tweet") {
    it("should serialize an OCRemix JSON tweet into a Map[String,Any] object") {
      val parsedJson = parse[Map[String, Any]](ocremixTweet)
      parsedJson("text") should be === firstTweet
    }

    it("should serialize an array of tweets into a List[Map[String,Any]] object") {
      val parsedJson = parse[List[Map[String,Any]]](ocremixTweets)
      parsedJson.head("text") should be === firstTweet
    }

  }


}