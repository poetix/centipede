package com.codepoetics.centipede

import java.net.URI

class SuffixAwareLinkClassifier() : LinkClassifier {

    companion object {
        val staticSuffixes = setOf("js", "css", "gif", "jpg", "png", "ico")
    }

    override fun invoke(domain: Domain, uri: URI): Link =
            if (domainsMatch(domain, uri)) {
                if (isStaticResource(uri)) {
                    Link.StaticResource(uri)
                } else {
                    Link.SiteLink(uri)
                }
            } else {
                Link.ExternalLink(uri)
            }

    private fun domainsMatch(domain: Domain, uri: URI): Boolean = uri.host.equals(domain)
    private fun isStaticResource(uri: URI): Boolean = staticSuffixes.stream().anyMatch { uri.path.endsWith(".${it}") }

}
