package com.codepoetics.centipede

import java.net.URI

sealed class Link(val uri: URI, val shouldFollow: Boolean) {
    data class SiteLink(val siteUri: URI) : Link(siteUri, true)
    data class StaticResource(val resourceUri: URI): Link(resourceUri, false)
    data class ExternalLink(val externalUri: URI) : Link(externalUri, false)
}

typealias LinkSet = Set<Link>
typealias SiteMap = Map<URI, LinkSet>
typealias PageVisitor = (URI) -> LinkSet
typealias Domain = String
typealias LinkClassifier = (Domain, URI) -> Link

fun SiteMap.closure(pageVisitor: PageVisitor): SiteMap {
    val visitedUris = this.keys
    val linked = this.values.flatMap { it }.toSet()
    val followableUris: Set<URI> = linked.filter { it.shouldFollow }.map { it.uri }.toSet()
    val toVisit = followableUris - visitedUris

    if (toVisit.isEmpty()) {
        return this
    }

    val newPages = toVisit.map { it to pageVisitor(it) }
    return (this + newPages).closure(pageVisitor)
}

class Centipede(val pageVisitor: PageVisitor) {
    operator fun invoke(uri: URI): SiteMap = mapOf(uri to pageVisitor(uri)).closure(pageVisitor)
}
