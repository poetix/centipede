package com.codepoetics.centipede

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import java.net.URI

class HttpPageFetcher() : PageFetcher {

    override fun invoke(uri: URI): String {
        val client = HttpClient()

        val method = GetMethod(uri.toString())
        try {
            val statusCode = client.executeMethod(method)

            if (statusCode != 200) {
                throw Throwable("Method failed: " + method.getStatusLine())
            }

            val responseBody = method.getResponseBody()
            return String(responseBody)
        } finally {
            method.releaseConnection()
        }
    }

}

