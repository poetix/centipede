package com.codepoetics.centipede

import com.codepoetics.centipede.CentipedeMatchers.haveLinksFrom
import io.kotlintest.matchers.Matchers
import io.kotlintest.specs.WordSpec
import java.net.URI

class CentipedeTest : WordSpec() {

    val crawler = Centipede()

    init {
        "The crawler" should {
            "accept a URI and return a SiteMap with outbound links from that URI" {
                val startUri = URI.create("http://test.com/1")
                crawler(URI.create("http://test.com/1")) should haveLinksFrom(startUri)
            }
        }
    }
}

object CentipedeMatchers : Matchers {
    fun haveLinksFrom(uri: URI): (SiteMap) -> Unit = {
        it should haveKey(uri)
    }
}
