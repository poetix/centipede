package com.codepoetics.centipede

import com.codepoetics.centipede.Builders.aSiteUri
import com.codepoetics.centipede.Builders.anExternalUri
import com.codepoetics.centipede.CentipedeMatchers.haveLinksFrom
import com.codepoetics.centipede.Link.*
import com.codepoetics.centipede.TestUtils.mock
import io.kotlintest.matchers.Matchers
import io.kotlintest.mock.`when`
import io.kotlintest.specs.WordSpec
import org.mockito.Mockito
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

class CentipedeTest : WordSpec() {

    val uriSetExtractor: UriSetExtractor = mock()

    val crawler = Centipede(uriSetExtractor)

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

            "not follow links to static resources" {
                val home = aSiteUri()
                val favicon = aSiteUri("favicon.ico")

                home.linksTo(favicon)
                favicon.hasNoLinks()

                crawler(home) should haveLinksFrom(home to setOf(StaticResource(favicon)))
            }
        }
    }

    fun URI.linksTo(vararg uris: URI): Unit {
        `when`(uriSetExtractor(this)).thenReturn(uris.toSet())
    }

    fun URI.hasNoLinks() { this.linksTo() }
}
