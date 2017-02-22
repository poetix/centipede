package com.codepoetics.centipede

import java.net.URI

typealias Link = URI
typealias SiteMap = Map<URI, Set<Link>>

class Centipede {
    operator fun invoke(uri: URI): SiteMap = mapOf(uri to emptySet())
}
