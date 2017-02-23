package com.codepoetics.centipede

import java.net.URI

class Centipede(uriSetFetcher: UriSetFetcher, linkClassifier: LinkClassifier = SuffixAwareLinkClassifier()) {
    val pageVisitor = uriSetFetcher.classifyingWith(linkClassifier)

    operator fun invoke(uri: URI): SiteMap = mapOf(uri to pageVisitor(uri)).closure(pageVisitor)
}

fun main(args: Array<String>) {
    if (args.size == 0) {
        println("Usage: java -jar target/centipede-1.0-SNAPSHOT.jar [start uri]")
        return
    }
    val startUri = URI.create(args[0])
    val pageFetcher = HttpPageFetcher()
    val uriSetExtractor = RegexUriSetExtractor()
    val centipede = Centipede(pageFetcher.extractingWith(uriSetExtractor))
    centipede(startUri).prettyPrint(startUri)
}
