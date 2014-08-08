import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers
import com.sixnothings.config._

class ConfigSpec extends FunSpec with BeforeAndAfter with ShouldMatchers {
  describe("load some stuff") {
    it("test char limit") {
      TwitterSettings.charLimit should be === 140
    }
    describe("handles") {
      it("ocremix handle is not nothing") {
        TwitterSettings.ocremixHandle.screenName should (not be null and not be "")
        TwitterSettings.ocremixHandle.id should (not be null and not be "")
      }
      it("panic handle is not nothing") {
        TwitterSettings.panicHandle.screenName should (not be null and not be "")
        TwitterSettings.panicHandle.id should (not be null and not be "")
      }
    }
    describe("keys") {
      it("consumer key is not nothing") {
        TwitterSettings.consumerKey.getKey should (not be null and not be "")
        TwitterSettings.consumerKey.getSecret should (not be null and not be "")
      }
      it("access key is not nothing") {
        TwitterSettings.accessKey.getKey should (not be null and not be "")
        TwitterSettings.accessKey.getSecret should (not be null and not be "")
      }
    }   
    it("urls should not be empty strings") {
      val requiredUrls = List("requestToken", "accessToken", "authorize", "api")
      requiredUrls.foreach { elem =>
        TwitterSettings.twitterUrls(elem) should (not be null and not be "")
      }
    }
  }
}
