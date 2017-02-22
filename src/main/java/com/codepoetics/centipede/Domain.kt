package com.codepoetics.centipede

import java.net.URI

sealed class Link(val uri: URI, val shouldFollow: Boolean) {
    data class SiteLink(val siteUri: URI) : Link(siteUri, true)
    data class StaticResource(val resourceUri: URI): Link(resourceUri, false)
    data class ExternalLink(val externalUri: URI) : Link(externalUri, false)
}

typealias UriSet = Set<URI>
typealias LinkSet = Set<Link>
typealias UriSetExtractor = (URI) -> UriSet
typealias PageVisitor = (URI) -> LinkSet
typealias Domain = String
typealias LinkClassifier = (Domain, URI) -> Link

fun UriSetExtractor.classifyingWith(linkClassifier: LinkClassifier): PageVisitor = { uri ->
    this(uri).map { linkClassifier(uri.host, it) }.toSet()
}

