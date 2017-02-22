package com.codepoetics.centipede

import io.kotlintest.matchers.Matchers
import org.mockito.Mockito
import java.net.URI
import java.util.concurrent.atomic.AtomicLong

object Builders {
    val localDomain = "test.com"
    val remoteDomain = "remote.com"
    val nextId = AtomicLong()

    fun aSiteUri(): URI = URI.create("http://${localDomain}/${nextId.incrementAndGet()}")
    fun aSiteUri(path: String): URI = URI.create("http://${localDomain}/${path}")
    fun anExternalUri(): URI = URI.create("http://${remoteDomain}/${nextId.incrementAndGet()}")
}

object TestUtils {
    fun <I, O, T : Function1<I, O>> mock() = Mockito.mock(Function1::class.java) as T
}

object CentipedeMatchers : Matchers {
    fun haveLinksFrom(vararg pairs: Pair<URI, LinkSet>): (SiteMap) -> Unit = { siteMap ->
        siteMap.keys should haveSize(pairs.size)
        pairs.forEach { pair -> siteMap should contain(pair.first, pair.second) }
    }
}
