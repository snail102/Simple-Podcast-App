package ru.anydevprojects.simplepodcastapp.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.sha1
import java.util.Formatter
import kotlinx.serialization.json.Json
import ru.anydevprojects.simplepodcastapp.BuildConfig
import ru.anydevprojects.simplepodcastapp.core.CredentialsProvider

private const val BASE_URL = "https://api.podcastindex.org/api/1.0/"

internal fun getNetworkClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
    defaultRequest {
        url(BASE_URL)
        contentType(ContentType.Application.Json)
        headers {
            val epoch = (System.currentTimeMillis() / 1000)
            append("User-Agent", "SimplePodcastApp/${BuildConfig.VERSION_NAME}")
            append("X-Auth-Date", epoch.toString())
            append("X-Auth-Key", CredentialsProvider.apiKey())
            append("Authorization", authHeader(epoch))
        }
    }
}

private fun authHeader(epoch: Long): String {
    val apiKey = CredentialsProvider.apiKey()
    val apiSecret = CredentialsProvider.apiSecret()
    val authHash = sha1("$apiKey$apiSecret$epoch".toByteArray())
    return byteToHex(authHash)
}

private fun byteToHex(binary: ByteArray): String {
    Formatter().use { formatter ->
        for (b in binary) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }
}
