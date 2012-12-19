import com.sixnothings.twitter.api.Tweet
import java.lang.IllegalArgumentException
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers

class TweetSpec extends FunSpec with BeforeAndAfter with ShouldMatchers {

  val shortMessage = "hello 123"
  val longMessage  = "0123456789" * 14 + "1"

  val thirtyCharUrl = "www.30length.com/yep/123456789"
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
}