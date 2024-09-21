package ru.anydevprojects.simplepodcastapp.core

object CredentialsProvider {
    private lateinit var key: String
    private lateinit var secret: String
    private lateinit var webClientId: String

    fun setAPICredentials(key: String, secret: String) {
        this.key = key
        this.secret = secret
    }

    fun setGoogleId(webClientId: String) {
        this.webClientId = webClientId
    }

    fun getWebClientId(): String {
        if (!::webClientId.isInitialized) {
            throw IllegalStateException(
                "Web client ID isn't provided! make sure to set it first before trying to access its value"
            )
        }
        return webClientId
    }

    fun getApiKey(): String {
        if (!::key.isInitialized) {
            throw IllegalStateException(
                "API Key isn't provided! make sure to set it first before trying to access its value"
            )
        }
        return key
    }

    fun getApiSecret(): String {
        if (!::secret.isInitialized) {
            throw IllegalStateException(
                "API Secret isn't provided! make sure to set it first before trying to access its value"
            )
        }
        return secret
    }
}
