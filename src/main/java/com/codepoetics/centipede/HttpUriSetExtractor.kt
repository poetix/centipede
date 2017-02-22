package com.codepoetics.centipede

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import java.net.URI
import java.util.regex.Pattern

class HttpUriSetExtractor() : UriSetExtractor {

    val uriPattern = Pattern.compile("href=\"([^\"]*)\"")
    val client = HttpClient()

    override fun invoke(uri: URI): Set<URI> {
        println("Fetching ${uri}")
        var result: UriSet = emptySet()

        try {
            val method = GetMethod(uri.toString())
            try {
                val statusCode = client.executeMethod(method)

                if (statusCode != 200) {
                    System.err.println("Method failed: " + method.getStatusLine())
                    return result
                }

                val responseBody = method.getResponseBody()

                val matcher = uriPattern.matcher(String(responseBody))

                while (matcher.find()) {
                    val fullUri = URI.create(matcher.group(1))
                    val normalisedUri = URI(fullUri.scheme, fullUri.host, fullUri.path, null)
                    if (normalisedUri.host != null) result += normalisedUri
                }
                return result
            } finally {
                method.releaseConnection()
            }
        } catch (e: Throwable) {
            // just ignore failures of any and every kind for now
        }

        return result
    }
}