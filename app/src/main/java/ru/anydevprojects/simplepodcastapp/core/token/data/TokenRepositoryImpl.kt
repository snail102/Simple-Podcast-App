package ru.anydevprojects.simplepodcastapp.core.token.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.anydevprojects.simplepodcastapp.core.dataStore.DataStore
import ru.anydevprojects.simplepodcastapp.core.token.domain.TokenRepository
import ru.anydevprojects.simplepodcastapp.core.token.domain.models.Token

class TokenRepositoryImpl(
    private val dataStore: DataStore
) : TokenRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var _accessToken: String = ""
    private var _refreshToken: String = ""

    init {
        dataStore.tokenFlow.onEach { token ->
            _accessToken = token?.access.orEmpty()
            _refreshToken = token?.refresh.orEmpty()
        }.launchIn(coroutineScope)
    }

    override val tokenFlow: Flow<Token?>
        get() = dataStore.tokenFlow

    override val accessToken: String
        get() = _accessToken
    override val refreshToken: String
        get() = _refreshToken

    override suspend fun hasToken(): Boolean {
        return dataStore.getToken() != null
    }

    override suspend fun updateTokens(token: Token) {
        dataStore.updateTokens(
            accessToken = token.access,
            refreshToken = token.refresh
        )
    }

    override suspend fun removeTokens() {
        dataStore.removeTokens()
    }
}
