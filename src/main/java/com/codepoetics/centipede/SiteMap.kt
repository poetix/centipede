package com.codepoetics.centipede

import com.codepoetics.centipede.Link.*
import java.net.URI
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.function.Supplier

typealias SiteMap = Map<URI, LinkSet>

val defaultThreadPool = Executors.newFixedThreadPool(10)

fun SiteMap.closure(pageVisitor: PageVisitor, maxDepth: Int = 5, threadPool: ExecutorService = defaultThreadPool): SiteMap {
    if (maxDepth == 0) return this

    val visitedUris = this.keys
    val linked = this.values.flatMap { it }.toSet()
    val followableUris: Set<URI> = linked.filter { it.shouldFollow }.map { it.uri }.toSet()
    val toVisit = followableUris - visitedUris

    if (toVisit.isEmpty()) {
        return this
    }

    val newPages = toVisit.map { CompletableFuture.supplyAsync(Supplier<Pair<URI, LinkSet>> { it to pageVisitor(it) }, threadPool) }
        .map { future -> future.get() }

    return (this + newPages).closure(pageVisitor, maxDepth - 1, threadPool)
}

fun SiteMap.prettyPrint(startUri: URI, visited: UriSet = emptySet(), indentLevel: Int = 0): UriSet {
    val indent = (1..indentLevel).map { '\t' }.joinToString("")

    println("${indent}${startUri.path}")

    val links = this[startUri]
    if (links == null) return visited
    var newVisited = visited + startUri

    links.forEach { link ->
        when (link) {
            is ExternalLink -> println("\t${indent}\\-> ${link.uri}")
            is StaticResource -> println("\t${indent}${link.uri.path} (static)")
            is SiteLink ->
                if (newVisited.contains(link.uri)) println("\t${indent}(${link.uri.path})")
                else newVisited += this.prettyPrint(link.uri, newVisited + link.uri, indentLevel + 1)
        }
        newVisited += link.uri
    }

    return newVisited
}