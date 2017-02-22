package com.codepoetics.centipede

import java.net.URI

class Centipede(uriSetExtractor: UriSetExtractor, linkClassifier: LinkClassifier = SuffixAwareLinkClassifier()) {
    val pageVisitor = uriSetExtractor.classifyingWith(linkClassifier)

    operator fun invoke(uri: URI): SiteMap = mapOf(uri to pageVisitor(uri)).closure(pageVisitor)
}

fun main(args: Array<String>) {
    val startUri = URI.create(args[0])
    val centipede = Centipede(HttpUriSetExtractor())
    centipede(startUri).prettyPrint(startUri)
}
