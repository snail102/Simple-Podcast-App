package ru.anydevprojects.simplepodcastapp.core.pushToken.data

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.anydevprojects.simplepodcastapp.core.pushToken.data.models.PushTokenRequest
import ru.anydevprojects.simplepodcastapp.core.pushToken.domain.PushTokenRepository

class PushTokenRepositoryImpl(
    private val httpClient: HttpClient
) : PushTokenRepository {
    override suspend fun sendToken(token: String): Result<Unit> {
        return kotlin.runCatching {
            httpClient.post("update_push_token") {
                setBody(PushTokenRequest(fcmToken = token))
            }
        }
    }
}
