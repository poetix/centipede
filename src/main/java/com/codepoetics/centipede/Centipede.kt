package com.codepoetics.centipede

import java.net.URI

typealias Link = URI
typealias LinkSet = Set<Link>
typealias SiteMap = Map<URI, LinkSet>
typealias PageVisitor = (URI) -> LinkSet

fun SiteMap.closure(pageVisitor: PageVisitor): SiteMap {
    val visited = this.keys
    val linked = this.values.flatMap { it }.toSet()
    val toVisit = linked - visited

    if (toVisit.isEmpty()) {
        return this
    }

    val newPages = toVisit.map { it to pageVisitor(it) }
    return (this + newPages).closure(pageVisitor)
}

class Centipede(val pageVisitor: PageVisitor) {
    operator fun invoke(uri: URI): SiteMap = mapOf(uri to pageVisitor(uri)).closure(pageVisitor)
}
