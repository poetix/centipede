package com.codepoetics.centipede

import java.net.URI

typealias SiteMap = Map<URI, LinkSet>

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

fun SiteMap.prettyPrint(startUri: URI, visited: UriSet = emptySet(), indentLevel: Int = 0) {
    val indent = (1..indentLevel).map { '\t' }.joinToString("")
    val newVisited = visited + startUri
    println("${indent}${startUri.path}")
    this[startUri]!!.forEach { link ->
        when (link) {
            is Link.ExternalLink -> println("\t->${indent}${link.uri}")
            is Link.StaticResource -> println("\t${indent}${link.uri.path} (static)")
            is Link.SiteLink ->
                if (newVisited.contains(link.uri)) println("\t${indent}(${link.uri.path})")
                else this.prettyPrint(link.uri, newVisited + link.uri, indentLevel + 1)
        }
    }
}