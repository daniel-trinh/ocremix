import com.sixnothings.ocremix.RemixEntry
import com.sixnothings.ocremix.Remixer
import org.scalatest.{PrivateMethodTester, FunSpec, BeforeAndAfter, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers
import com.sixnothings.ocremix._
import scala._
import scala.xml.XML

class OcremixSpec extends FunSpec with BeforeAndAfter with ShouldMatchers with PrivateMethodTester {
  val sample =
    """<?xml version="1.0" encoding="utf-8"?>
      |<rss version="2.0">
      |  <channel>
      |    <title>10 Latest OverClocked ReMixes</title>
      |    <link>http://www.ocremix.org</link>
      |    <description/>
      |    <language>en-us</language>
      |    <item>
      |      <title>Chrono Cross 'A Dream Between Worlds'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/17/"&gt;Chrono Cross&lt;/a&gt; (PS1) by &lt;a href="http://www.ocremix.org/artist/11510/avitron"&gt;Avitron&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/10671/chris-amaterasu"&gt;Chris ~ Amaterasu&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/4/yasunori-mitsuda"&gt;Yasunori Mitsuda&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02572/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02572/</guid>
      |      <pubDate>Thu, 27 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Ristar 'Stars on Ice'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/131/"&gt;Ristar&lt;/a&gt; (GEN) by &lt;a href="http://www.ocremix.org/artist/11774/dusk"&gt;DusK&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/4655/rexy"&gt;Rexy&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/300/masafumi-ogata"&gt;Masafumi Ogata&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/161/naofumi-hataya"&gt;Naofumi Hataya&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/160/tomoko-sasaki"&gt;Tomoko Sasaki&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02571/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02571/</guid>
      |      <pubDate>Mon, 24 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Scott Pilgrim vs. The World: The Game '1-UP'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/820/"&gt;Scott Pilgrim vs. The World: The Game&lt;/a&gt; (PS3) by &lt;a href="http://www.ocremix.org/artist/5409/brandon-strader"&gt;Brandon Strader&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/9263/anamanaguchi"&gt;Anamanaguchi&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12548/ary-warnaar"&gt;Ary Warnaar&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12550/luke-silas"&gt;Luke Silas&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12551/peter-berkman"&gt;Peter Berkman&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02570/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02570/</guid>
      |      <pubDate>Mon, 24 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Donkey Kong Country 3: Dixie Kong's Double Trouble! 'mojo gogo'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/310/"&gt;Donkey Kong Country 3: Dixie Kong's Double Trouble!&lt;/a&gt; (SNES) by &lt;a href="http://www.ocremix.org/artist/4695/prophetik"&gt;prophetik&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/100/david-wise"&gt;David Wise&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/101/eveline-novakovic"&gt;Eveline Novakovic&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02569/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02569/</guid>
      |      <pubDate>Mon, 24 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>The Legend of Zelda: Majora's Mask 'Dawn of a New Day'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/490/"&gt;The Legend of Zelda: Majora's Mask&lt;/a&gt; (N64) by &lt;a href="http://www.ocremix.org/artist/12546/docjazz4"&gt;Docjazz4&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12547/funkyentropy"&gt;FunkyEntropy&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/4605/theophany"&gt;Theophany&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12064/xprtnovice"&gt;XPRTNovice&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/2/koji-kondo"&gt;Koji Kondo&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/542/toru-minegishi"&gt;Toru Minegishi&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02568/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02568/</guid>
      |      <pubDate>Sat, 22 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Metroid Prime 'Relics of an Ancient Race'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/425/"&gt;Metroid Prime&lt;/a&gt; (GCN) by &lt;a href="http://www.ocremix.org/artist/12541/argle"&gt;Argle&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/82/kenji-yamamoto-i"&gt;Kenji Yamamoto (I)&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/223/koichi-kyuma"&gt;Koichi Kyuma&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02567/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02567/</guid>
      |      <pubDate>Fri, 21 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Final Fantasy 'Requiem for a Dying World'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/8/"&gt;Final Fantasy&lt;/a&gt; (NES) by &lt;a href="http://www.ocremix.org/artist/5409/brandon-strader"&gt;Brandon Strader&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/12540/chernabogue"&gt;Chernabogue&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/3/nobuo-uematsu"&gt;Nobuo Uematsu&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02566/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02566/</guid>
      |      <pubDate>Fri, 21 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Cardinal Quest 'The World After Asterion'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/817/"&gt;Cardinal Quest&lt;/a&gt; (WIN) by &lt;a href="http://www.ocremix.org/artist/10683/some1namedjeff"&gt;some1namedjeff&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/12365/whitaker-trebella"&gt;Whitaker Trebella&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02565/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02565/</guid>
      |      <pubDate>Thu, 20 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Wild Arms 'A Morning at the Abbey'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/230/"&gt;Wild Arms&lt;/a&gt; (PS1) by &lt;a href="http://www.ocremix.org/artist/10136/archangel"&gt;Archangel&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/39/michiko-naruke"&gt;Michiko Naruke&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02564/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02564/</guid>
      |      <pubDate>Thu, 20 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |    <item>
      |      <title>Sonic &amp; Knuckles 'Airborne'</title>
      |      <description>ReMix of &lt;a href="http://www.ocremix.org/game/147/"&gt;Sonic &amp; Knuckles&lt;/a&gt; (GEN) by &lt;a href="http://www.ocremix.org/artist/11943/devastus"&gt;Devastus&lt;/a&gt;.&lt;br/&gt; Original soundtrack by &lt;a href="http://www.ocremix.org/artist/233/howard-drossin"&gt;Howard Drossin&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/41/jun-senoue"&gt;Jun Senoue&lt;/a&gt;, &lt;a href="http://www.ocremix.org/artist/910/tomonori-sawada"&gt;Tomonori Sawada&lt;/a&gt;.</description>
      |      <link>http://www.ocremix.org/remix/OCR02563/</link>
      |      <guid isPermaLink="true">http://www.ocremix.org/remix/OCR02563/</guid>
      |      <pubDate>Wed, 19 Dec 2012 00:00:00 +0000</pubDate>
      |    </item>
      |  </channel>
      |</rss>
    """.stripMargin

   describe("RSS") {
     describe("extractRemixes") {
       RSS.extractRemixes(XML.loadString(sample)) should be ===
         List(
           RemixEntry(
             List(
               Remixer("Avitron", "http://www.ocremix.org/artist/11510/avitron"),
               Remixer("Chris ~ Amaterasu" ,"http://www.ocremix.org/artist/10671/chris-amaterasu")
             ),
             "Chrono Cross 'A Dream Between Worlds'","http://www.youtube.com/watch?&v=g9sp3De7ocA","http://www.ocremix.org/remix/OCR02572/",2572
           ),

           RemixEntry(
             List(
               Remixer("DusK", "http://www.ocremix.org/artist/11774/dusk"),
               Remixer("Rexy", "http://www.ocremix.org/artist/4655/rexy")
             ),
             "Ristar 'Stars on Ice'","http://www.youtube.com/watch?&v=9RABA5jyU-w","http://www.ocremix.org/remix/OCR02571/",2571
           ),

           RemixEntry(
             List(
               Remixer("Brandon Strader","http://www.ocremix.org/artist/5409/brandon-strader")
             ),
             "Scott Pilgrim vs. The World: The Game '1-UP'","http://www.youtube.com/watch?&v=5Co4mtubixw","http://www.ocremix.org/remix/OCR02570/",2570
           ),

           RemixEntry(
             List(
               Remixer("prophetik", "http://www.ocremix.org/artist/4695/prophetik")
             ),
             "Donkey Kong Country 3: Dixie Kong's Double Trouble! 'mojo gogo'","http://www.youtube.com/watch?&v=kzPNyzN_g1c","http://www.ocremix.org/remix/OCR02569/",2569
           ),

           RemixEntry(
             List(
               Remixer("Docjazz4", "http://www.ocremix.org/artist/12546/docjazz4"),
               Remixer("FunkyEntropy", "http://www.ocremix.org/artist/12547/funkyentropy"),
               Remixer("Theophany", "http://www.ocremix.org/artist/4605/theophany"),
               Remixer("XPRTNovice", "http://www.ocremix.org/artist/12064/xprtnovice")
             ),
             "The Legend of Zelda: Majora's Mask 'Dawn of a New Day'","http://www.youtube.com/watch?&v=1NryFD9_hR0","http://www.ocremix.org/remix/OCR02568/",2568
           ),

           RemixEntry(
             List(
               Remixer("Argle", "http://www.ocremix.org/artist/12541/argle"),
               Remixer("Koichi Kyuma","http://www.ocremix.org/artist/223/koichi-kyuma")
             ),
             "Metroid Prime 'Relics of an Ancient Race'","http://www.youtube.com/watch?&v=x21k14XcHow","http://www.ocremix.org/remix/OCR02567/",2567
           ),

           RemixEntry(
             List(
               Remixer("Brandon Strader","http://www.ocremix.org/artist/5409/brandon-strader"),
               Remixer("Chernabogue", "http://www.ocremix.org/artist/12540/chernabogue")
             ),
             "Final Fantasy 'Requiem for a Dying World'","http://www.youtube.com/watch?&v=5PRo-3jOi3A","http://www.ocremix.org/remix/OCR02566/",2566
           ),

           RemixEntry(
             List(
               Remixer("some1namedjeff", "http://www.ocremix.org/artist/10683/some1namedjeff")
             ),
             "Cardinal Quest 'The World After Asterion'","http://www.youtube.com/watch?&v=IIUVmHUHuzQ","http://www.ocremix.org/remix/OCR02565/",2565
           ),

           RemixEntry(
             List(
               Remixer("Archangel", "http://www.ocremix.org/artist/10136/archangel")
             ),
             "Wild Arms 'A Morning at the Abbey'","http://www.youtube.com/watch?&v=i1HCxmMMfEE","http://www.ocremix.org/remix/OCR02564/",2564
           ),

           RemixEntry(
             List(
               Remixer("Devastus", "http://www.ocremix.org/artist/11943/devastus")
             ),
             "Sonic & Knuckles 'Airborne'","http://www.youtube.com/watch?&v=jcExvvbtFpA","http://www.ocremix.org/remix/OCR02563/",2563
           )
         )
     }
     describe("extractRemixEntry") {
     }
     describe("extractYoutubeLink") {
       val extractYoutubeLink = PrivateMethod[String]('extractYoutubeLink)
       RSS invokePrivate extractYoutubeLink("http://www.ocremix.org/remix/OCR02566/")
     }
     describe("extractRemixers") {
       val extractRemixers = PrivateMethod[List[Remixer]]('extractRemixers)
       RSS invokePrivate extractRemixers((XML.loadString(sample) \\ "item" \\ "description" head) text) should be ===
       List(
         Remixer("Avitron", "http://www.ocremix.org/artist/11510/avitron"),
         Remixer("Chris ~ Amaterasu" ,"http://www.ocremix.org/artist/10671/chris-amaterasu")
        )
     }
     describe("followRedirects") {
     }
   }

  // describe("apply methods") {
  //   it("class -- Bar") {
  //     val bar = new Bar()
  //     bar() should be === 0
  //   }
  //   it("object -- BarMaker") {
  //     val bar = BarMaker()
  //     bar() should be === 0
  //   }
  //}
  
}
