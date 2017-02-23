package com.codepoetics.centipede

import java.net.URI
import java.util.regex.Pattern

class RegexUriSetExtractor() : UriSetExtractor {

    val uriPattern = Pattern.compile("href=\"([^\"]*)\"")

    override fun invoke(baseUri: URI, page: Page): Set<URI> {
        val matcher = uriPattern.matcher(page)
        var result: UriSet = emptySet()

        while (matcher.find()) {
            try {
                val fullUri = baseUri.resolve(URI.create(matcher.group(1)))
                val normalisedUri = URI(fullUri.scheme, fullUri.host, fullUri.path, null)

                result += normalisedUri
            } catch (e: Throwable) {
                e.printStackTrace()
                // just ignore all parsing failures for now
            }
        }
        return result
    }

}
