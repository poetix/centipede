package com.codepoetics.centipede

import com.codepoetics.centipede.Builders.aSiteUri
import com.codepoetics.centipede.Builders.anExternalUri
import com.codepoetics.centipede.CentipedeMatchers.haveLinksFrom
import com.codepoetics.centipede.Link.ExternalLink
import com.codepoetics.centipede.Link.SiteLink
import com.codepoetics.centipede.TestUtils.mock
import io.kotlintest.matchers.Matchers
import io.kotlintest.mock.`when`
import io.kotlintest.specs.WordSpec
import org.mockito.Mockito
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

class CentipedeTest : WordSpec() {

    val pageVisitor: PageVisitor = mock()
    val linkClassifier: LinkClassifier = { domain, uri: URI ->
        if (uri.host.equals(domain)) {
            SiteLink(uri)
        } else {
            ExternalLink(uri)
        }
    }

    val crawler = Centipede(pageVisitor)

    init {
        "The crawler" should {
            "accept a URI and return a SiteMap with outbound links from that URI" {
                val startUri = aSiteUri()

                startUri.hasNoLinks()

                crawler(startUri) should haveLinksFrom(startUri to emptySet())
            }

            "return all the outbound links found on the page at the initial URI" {
                val startUri = aSiteUri()
                val link1 = aSiteUri()
                val link2 = aSiteUri()

                startUri.linksTo(link1, link2)
                link1.hasNoLinks()
                link2.hasNoLinks()

                crawler(startUri) should haveLinksFrom(
                        startUri to setOf(SiteLink(link1), SiteLink(link2)),
                        link1 to emptySet(),
                        link2 to emptySet())
            }

            "follow outbound links" {
                val homepage = aSiteUri()
                val bio = aSiteUri()
                val blogposts = aSiteUri()
                val linkedBlog = aSiteUri()

                homepage.linksTo(bio, blogposts)
                bio.linksTo(homepage)
                blogposts.linksTo(homepage, linkedBlog)
                linkedBlog.hasNoLinks()

                crawler(homepage) should haveLinksFrom(
                        homepage to setOf(SiteLink(bio), SiteLink(blogposts)),
                        bio to setOf(SiteLink(homepage)),
                        blogposts to setOf(SiteLink(homepage), SiteLink(linkedBlog)),
                        linkedBlog to emptySet()
                )
            }

            "not follow links to resources on other domains" {
                val home = aSiteUri()
                val away = anExternalUri()

                home.linksTo(away)
                away.hasNoLinks()

                crawler(home) should haveLinksFrom(home to setOf(ExternalLink(away)))

            }
        }
    }

    fun URI.linksTo(vararg uris: URI): Unit {
        `when`(pageVisitor(this)).thenReturn(uris.map { linkClassifier("test.com", it) }.toSet())
    }

    fun URI.hasNoLinks() { this.linksTo() }
}

object Builders {
    val nextId = AtomicLong()

    fun aSiteUri(): URI = URI.create("http://test.com/${nextId.incrementAndGet()}")
    fun anExternalUri(): URI = URI.create("http://remote.com/${nextId.incrementAndGet()}")
}

object TestUtils {
    fun <I, O, T : Function1<I, O>> mock() = Mockito.mock(Function1::class.java) as T
}

object CentipedeMatchers : Matchers {
    fun haveLinksFrom(vararg pairs: Pair<URI, LinkSet>): (SiteMap) -> Unit = { siteMap ->
        siteMap.keys should haveSize(pairs.size)
        pairs.forEach { pair -> siteMap should contain(pair.first, pair.second) }
    }
}
