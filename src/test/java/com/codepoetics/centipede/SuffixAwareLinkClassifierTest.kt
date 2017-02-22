package com.codepoetics.centipede

import com.codepoetics.centipede.Link.*
import io.kotlintest.specs.WordSpec

class SuffixAwareLinkClassifierTest : WordSpec() {

    val unit = SuffixAwareLinkClassifier()

    init {
        "The link classifier" should {
            "classify links in the same domain as site links" {
                val uri = Builders.aSiteUri()
                unit(Builders.localDomain, uri) shouldEqual SiteLink(uri)
            }

            "classify links in a remote domain as external links" {
                val uri = Builders.anExternalUri()
                unit(Builders.localDomain, uri) shouldEqual ExternalLink(uri)
            }

            "classify static resources" {
                setOf("js", "css", "gif", "jpg", "png", "ico").forEach { suffix ->
                    var uri = Builders.aSiteUri("resource.${suffix}")
                    unit(Builders.localDomain, uri) shouldEqual StaticResource(uri)
                }
            }
        }
    }
}
