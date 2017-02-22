package com.codepoetics.centipede

import java.net.URI

typealias Link = URI
typealias LinkSet = Set<Link>
typealias SiteMap = Map<URI, LinkSet>
typealias PageVisitor = (URI) -> LinkSet

class Centipede(val pageVisitor: PageVisitor) {
    operator fun invoke(uri: URI): SiteMap = mapOf(uri to pageVisitor(uri))
}
