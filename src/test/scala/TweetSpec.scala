import com.sixnothings.twitter.api.Tweet
import java.lang.IllegalArgumentException
import org.scalatest.{PrivateMethodTester, FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers

class TweetSpec extends FunSpec with BeforeAndAfter with ShouldMatchers with PrivateMethodTester {

  val shortMessage = "hello 123"
  val longMessage  = "0123456789" * 14 + "1"

  val shortUrl = "www.10.com"
  val shortHttpsUrl = "https://short.com"
  val shortHttpUrl = "http://short.com"

  val thirtyCharUrl = "www.30length.com/yep/123456789"
  val longHttpsUrl = "https://www.this-url-is-definitely-longer-than-21.com/"
  val longHttpUrl = "http://www.this-url-is-definitely-longer-than-20.com/"


  val tenChars = "0123456789"

  val shortMessageWithUrl         = "this link is pretty cool: http://www.twitter.com"
  val longMessageWithUrl          = tenChars * 12 + "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithUrlHttps     = tenChars * 11 + "123456789" + "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithMultipleUrls = thirtyCharUrl * 7

  describe("trimMessage") {
    it("should return itself if length less than 140") {
    }
    it("should trim a message with one http url") {
    }
    it("should trim a message with one https url") {
    }
    it("should trim an ocremix rss style message") {
    }
  }
  describe("tweetable") {
    describe("valid cases") {
      it("should be true for a short message") {
        Tweet.tweetable(shortMessage) should be === true
      }
      it("should be true for a short message with a Http url") {
        Tweet.tweetable(shortMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Http URL") {
        Tweet.tweetable(longMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Https URL") {
        Tweet.tweetable(longMessageWithUrlHttps) should be === true
      }
      it("should be true for a long message (over limit) with multiple shortenable URLs") {
        Tweet.tweetable(longMessageWithMultipleUrls) should be === true
      }
    }
    describe("invalid cases") {
      it("should be false for a message over 140 characters") {
        Tweet.tweetable(longMessage) should be === false
      }

      it("should raise an exception for a 0 length string") {
        intercept[IllegalArgumentException](Tweet tweetable "")
      }
      it("should raise an exception when maxLimit is <= 0") {
        intercept[IllegalArgumentException](Tweet.tweetable("nope", 0))
        intercept[IllegalArgumentException](Tweet.tweetable("nope", -1))
      }
    }
  }
  describe("tcoShortenedUrlLength") {
    val tcoShortenedUrlLength = PrivateMethod[Int]('tcoShortenedUrlLength)

    // TODO: this code looks like it can be shortened
    describe("urls longer than tco shortened url limit") {
      it("no protocol url") {
        Tweet invokePrivate tcoShortenedUrlLength(thirtyCharUrl) should be === 20
      }
      it("https url") {
        Tweet invokePrivate tcoShortenedUrlLength(longHttpsUrl) should be === 21
      }
      it("http url") {
        Tweet invokePrivate tcoShortenedUrlLength(longHttpUrl) should be === 20
      }
    }

    describe("urls shorter than tco shortened url limit") {
      it("no protocol url") {
        Tweet invokePrivate tcoShortenedUrlLength(shortUrl) should be === shortUrl.length
      }
      it("https url") {
        Tweet invokePrivate tcoShortenedUrlLength(shortHttpsUrl) should be === shortHttpsUrl.length
      }
      it("http url") {
        Tweet invokePrivate tcoShortenedUrlLength(shortHttpUrl) should be === shortHttpUrl.length
      }
    }
  }

  describe("savedChars") {
    val savedChars = PrivateMethod[Int]('savedChars)
    it("saved chars of a long URL as a message should be url.length - shortUrlLength") {
      Tweet invokePrivate savedChars(longHttpUrl) should be === (longHttpUrl.length - Tweet.shortUrlLength)
    }
  }
}