import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers
import com.sixnothings.config._

class ConfigSpec extends FunSpec with BeforeAndAfter with ShouldMatchers {
  describe("load some stuff") {
    it("test char limit") {
      s"asdf"
      OCConfig.charLimit should be === 140
    }
    describe("handles") {
      it("ocremix handle is not nothing") {
        OCConfig.ocremixHandle.screenName should (not be null and not be "")
        OCConfig.ocremixHandle.id should (not be null and not be "")
      }
      it("panic handle is not nothing") {
        OCConfig.panicHandle.screenName should (not be null and not be "")
        OCConfig.panicHandle.id should (not be null and not be "")
      }
    }
    describe("keys") {
      it("consumer key is not nothing") {
        OCConfig.consumerKey.getKey() should (not be null and not be "")
        OCConfig.consumerKey.getSecret() should (not be null and not be "")
      }
      it("access key is not nothing") {
        OCConfig.accessKey.getKey() should (not be null and not be "")
        OCConfig.accessKey.getSecret() should (not be null and not be "")
      }
    }   
    it("urls should not be empty strings") {
      val requiredUrls = List("requestToken", "accessToken", "authorize", "api")
      requiredUrls.foreach { elem =>
        OCConfig.twitterUrls(elem) should (not be null and not be "")
      }
     requiredUrls match { case a @ Nil => 1; case _ => 2}
    }
  }
}
