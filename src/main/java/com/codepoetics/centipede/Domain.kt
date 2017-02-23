package com.codepoetics.centipede

import java.net.URI

sealed class Link(val uri: URI, val shouldFollow: Boolean) {
    data class SiteLink(val siteUri: URI) : Link(siteUri, true)
    data class StaticResource(val resourceUri: URI): Link(resourceUri, false)
    data class ExternalLink(val externalUri: URI) : Link(externalUri, false)
}

typealias UriSet = Set<URI>
typealias LinkSet = Set<Link>
typealias Page = String
typealias PageFetcher = (URI) -> Page
typealias UriSetExtractor = (URI, Page) -> UriSet
typealias UriSetFetcher = (URI) -> UriSet

fun PageFetcher.extractingWith(extractor: UriSetExtractor): UriSetFetcher = { uri ->
    try {
        extractor(uri, this(uri))
    } catch (e : Throwable) {
        // Ignore errors loadng pages for now
        emptySet()
    }
}

typealias PageVisitor = (URI) -> LinkSet
typealias Domain = String
typealias LinkClassifier = (Domain, URI) -> Link

fun UriSetFetcher.classifyingWith(linkClassifier: LinkClassifier): PageVisitor = { uri ->
    this(uri).map { linkClassifier(uri.host, it) }.toSet()
}

