package com.codepoetics.centipede

import com.codepoetics.centipede.Builders.aUri
import com.codepoetics.centipede.CentipedeMatchers.haveLinksFrom
import com.codepoetics.centipede.TestUtils.mock
import io.kotlintest.matchers.Matchers
import io.kotlintest.mock.`when`
import io.kotlintest.specs.WordSpec
import org.mockito.Mockito
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

class CentipedeTest : WordSpec() {

    val pageVisitor: PageVisitor = mock()
    val crawler = Centipede(pageVisitor)

    init {
        "The crawler" should {
            "accept a URI and return a SiteMap with outbound links from that URI" {
                val startUri = aUri()

                startUri.linksTo()

                crawler(startUri) should haveLinksFrom(startUri)
            }

            "return all the outbound links found on the page at the initial URI" {
                val startUri = aUri()
                val link1 = aUri()
                val link2 = aUri()

                startUri.linksTo(link1, link2)
                link1.linksTo()
                link2.linksTo()

                crawler(startUri) should haveLinksFrom(startUri to setOf(link1, link2))
            }

            "follow outbound links" {
                val homepage = aUri()
                val bio = aUri()
                val blogposts = aUri()
                val linkedBlog = aUri()

                homepage.linksTo(bio, blogposts)
                bio.linksTo(homepage)
                blogposts.linksTo(homepage, linkedBlog)
                linkedBlog.linksTo()

                crawler(homepage) should haveLinksFrom(
                        homepage to setOf(bio, blogposts),
                        bio to setOf(homepage),
                        blogposts to setOf(homepage, linkedBlog)
                )
            }
        }
    }

    fun URI.linksTo(vararg uris: URI): Unit {
        `when`(pageVisitor(this)).thenReturn(setOf(*uris))
    }
}

object Builders {
    val nextId = AtomicLong()
    fun aUri(): URI = URI.create("http://test.com/${nextId.incrementAndGet()}")
}

object TestUtils {
    fun <I, O, T : Function1<I, O>> mock() = Mockito.mock(Function1::class.java) as T
}

object CentipedeMatchers : Matchers {
    fun haveLinksFrom(uri: URI): (SiteMap) -> Unit = {
        it should haveKey(uri)
    }

    fun haveLinksFrom(vararg pairs: Pair<URI, LinkSet>): (SiteMap) -> Unit = { siteMap ->
        pairs.forEach { pair -> siteMap should contain(pair.first, pair.second) }
    }
}
