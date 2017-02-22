package com.codepoetics.centipede

import com.codepoetics.centipede.CentipedeMatchers.haveLinksFrom
import com.codepoetics.centipede.TestUtils.mock
import io.kotlintest.matchers.Matchers
import io.kotlintest.mock.`when`
import io.kotlintest.specs.WordSpec
import org.mockito.Mockito
import java.net.URI

class CentipedeTest : WordSpec() {

    val pageVisitor: PageVisitor = mock()
    val crawler = Centipede(pageVisitor)

    init {
        "The crawler" should {
            "accept a URI and return a SiteMap with outbound links from that URI" {
                val startUri = URI.create("http://test.com/1")
                crawler(startUri) should haveLinksFrom(startUri)
            }

            "return all the outbound links found on the page at the initial URI" {
                val startUri = URI.create("http://test.com/1")
                val link1 = URI.create("http://test.com/2")
                val link2 = URI.create("http://test.com/3")

                `when`(pageVisitor(startUri)).thenReturn(setOf(
                        link1,
                        link2
                ))

                crawler(startUri) should haveLinksFrom(startUri to setOf(link1, link2))
            }
        }
    }
}

object TestUtils {
    fun <I, O, T : Function1<I, O>> mock() = Mockito.mock(Function1::class.java) as T
}

object CentipedeMatchers : Matchers {
    fun haveLinksFrom(uri: URI): (SiteMap) -> Unit = {
        it should haveKey(uri)
    }

    fun haveLinksFrom(pair: Pair<URI, LinkSet>): (SiteMap) -> Unit = {
        it should contain(pair.first, pair.second)
    }
}
