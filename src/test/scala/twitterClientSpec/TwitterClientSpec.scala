import com.sixnothings.twitter.api.{Auth, TwitterClient}
import com.sun.javaws.exceptions.InvalidArgumentException
import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers

class TwitterClientSpec extends FunSpec with BeforeAndAfter with ShouldMatchers {

  val client       = new TwitterClient(new Auth(""))
  val shortMessage = "hello 123"
  val longMessage  = "0123456789" * 14 + "1"

  val thirtyCharUrl = "www.30length.com/yep/123456789"
  val tenChars = "0123456789"

  val shortMessageWithUrl         = "this link is pretty cool: http://www.twitter.com"
  val longMessageWithUrl          = tenChars * 12 + "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithUrlHttps     = tenChars * 11 + "123456789" + "http://www.twitter.com/api1.1/help/configuration.json"
  val longMessageWithMultipleUrls = thirtyCharUrl * 7

  describe("trimMessage") {
    val client = new TwitterClient(new Auth(""))
    val shortMessage = """this message is less than 140"""

    it("should return itself if length less than 140") {
      client.trimMessage(shortMessage) should be === shortMessage
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
        client.tweetable(shortMessage) should be === true
      }
      it("should be true for a short message with a Http url") {
        client.tweetable(shortMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Http URL") {
        client.tweetable(longMessageWithUrl) should be === true
      }
      it("should be true for a long message (over limit) with a shortenable Https URL") {
        client.tweetable(longMessageWithUrlHttps) should be === true
      }
      it("should be true for a long message (over limit) with multiple shortenable URLs") {
        client.tweetable(longMessageWithMultipleUrls) should be === true
      }
    }
    describe("invalid cases") {
      it("should be false for a message over 140 characters") {
        client.tweetable(longMessage) should be === false
      }

      it("should raise an exception for a 0 length string") {
        intercept[InvalidArgumentException](client tweetable "")
      }
      it("should raise an exception when maxLimit is <= 0") {
        intercept[InvalidArgumentException](client.tweetable("nope", 0))
        intercept[InvalidArgumentException](client.tweetable("nope", -1))
      }
    }
  }
}