import com.sixnothings.twitter.api.Tweetable
import java.lang.IllegalArgumentException
import org.scalatest.{PrivateMethodTester, FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers

class TweetableSpec extends FunSpec with BeforeAndAfter with ShouldMatchers with PrivateMethodTester {

  val shortMessage = "hello 123"
  val longMessage  = "0123456789" * 14 + "1"

  val shortUrl      = "www.10.com"
  val shortHttpsUrl = "https://short.com"
  val shortHttpUrl  = "http://short.com"

  val twentyNineCharUrl = "www.29length.com/yep/12345678"
  val longHttpsUrl      = "https://www.this-url-is-definitely-longer-than-21.com/"
  val longHttpUrl       = "http://www.this-url-is-definitely-longer-than-20.com/"

  val tenChars = "0123456789"

  val shortMessageWithUrl         = "this link is pretty cool: http://www.twitter.com"
  val longMessageWithUrl          = tenChars * 12 +
    "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithUrlHttps     = tenChars * 11 + "123456789" +
    "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithMultipleUrls = (twentyNineCharUrl + " ") * 6

  describe("trim") {
    it("should return itself if length less than 140") {
      Tweetable.trim(shortUrl) should be === shortUrl
    }
    it("should trim a long message") {
      Tweetable.trim(longMessageWithMultipleUrls) should be === longMessageWithMultipleUrls.
        substring(0, Tweetable.charLimit-2) + ".."
    }
  }
  describe("tweetable") {
    describe("valid cases") {
      it("should be true for a short message") {
        Tweetable.isTweetable(shortMessage) should be === true
      }
      it("should be true for a short message with a Http url") {
        Tweetable.isTweetable(shortMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Http URL") {
        Tweetable.isTweetable(longMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Https URL") {
        Tweetable.isTweetable(longMessageWithUrlHttps) should be === true
      }
      it("should be true for a long message (over limit) with multiple shortenable URLs") {
        Tweetable.isTweetable(longMessageWithMultipleUrls) should be === true
      }
    }
    describe("invalid cases") {
      it("should be false for a message over 140 characters") {
        Tweetable.isTweetable(longMessage) should be === false
      }
      it("should raise an exception for a 0 length string") {
        intercept[IllegalArgumentException](Tweetable isTweetable "")
      }
      it("should raise an exception when maxLimit is <= 0") {
        intercept[IllegalArgumentException](Tweetable.isTweetable("nope", 0))
        intercept[IllegalArgumentException](Tweetable.isTweetable("nope", -1))
      }
    }
  }
  describe("tcoShortenedUrlLength") {
    val tcoShortenedUrlLength = PrivateMethod[Int]('tcoShortenedUrlLength)

    // TODO: this code looks like it can be shortened
    describe("urls longer than tco shortened url limit") {
      it("no protocol url") {
        Tweetable invokePrivate tcoShortenedUrlLength(twentyNineCharUrl) should be === 20
      }
      it("https url") {
        Tweetable invokePrivate tcoShortenedUrlLength(longHttpsUrl) should be === 21
      }
      it("http url") {
        Tweetable invokePrivate tcoShortenedUrlLength(longHttpUrl) should be === 20
      }
    }

    describe("urls shorter than tco shortened url limit") {
      it("no protocol url") {
        Tweetable invokePrivate tcoShortenedUrlLength(shortUrl) should be === shortUrl.length
      }
      it("https url") {
        Tweetable invokePrivate tcoShortenedUrlLength(shortHttpsUrl) should be === shortHttpsUrl.length
      }
      it("http url") {
        Tweetable invokePrivate tcoShortenedUrlLength(shortHttpUrl) should be === shortHttpUrl.length
      }
    }
  }

  describe("savedChars") {
    val savedChars = PrivateMethod[Int]('savedChars)
    it("saved chars of a long URL as a message should be url.length - shortUrlLength") {
      Tweetable invokePrivate savedChars(longHttpUrl) should be === (longHttpUrl.length - Tweetable.shortUrlLength)
    }
    it("saved chars of a multi url message, urls separated by spaces") {
      Tweetable invokePrivate savedChars(longMessageWithMultipleUrls) should be === 29 * 6 - 20 * 6
    }
  }
}