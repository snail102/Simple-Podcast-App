package ru.anydevprojects.simplepodcastapp.core.dataStore

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.anydevprojects.simplepodcastapp.core.dataStore.models.UserPrefsSerializer
import ru.anydevprojects.simplepodcastapp.core.user.domain.models.User

class DataStoreImpl(
    private val context: Context
) : DataStore {

    private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

    private val Context.userPrefsDataStore by dataStore(
        fileName = "user_prefs.pb",
        serializer = UserPrefsSerializer
    )

    private val accessTokenKey = stringPreferencesKey(ACCESS_TOKEN_KEY)
    private val refreshTokenKey = stringPreferencesKey(REFRESH_TOKEN_KEY)

    private var _accessToken: String = ""
    private var _refreshToken: String = ""

    init {
        context.dataStore.data.map { preferences ->
            _accessToken = preferences[accessTokenKey].orEmpty()
            _refreshToken = preferences[refreshTokenKey].orEmpty()
        }
    }

    override val userFlow: Flow<User?>
        get() = context.userPrefsDataStore.data
            .map { userPrefs ->
                User(
                    id = userPrefs.id,
                    email = userPrefs.email,
                    name = userPrefs.name,
                    familyName = userPrefs.familyName,
                    pictureUrl = userPrefs.pictureUrl
                )
            }

    override val accessToken: String
        get() = _accessToken
    override val refreshToken: String
        get() = _refreshToken

    override suspend fun getUser(): User? {
        val userPrefs = context.userPrefsDataStore.data.first()

        if (userPrefs.id.isEmpty()) {
            return null
        }

        return User(
            id = userPrefs.id,
            email = userPrefs.email,
            name = userPrefs.name,
            familyName = userPrefs.familyName,
            pictureUrl = userPrefs.pictureUrl
        )
    }

    override suspend fun updateTokens(accessToken: String, refreshToken: String) {
        _accessToken = accessToken
        _refreshToken = refreshToken

        context.dataStore.edit { settings ->
            settings[accessTokenKey] = accessToken
            settings[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun removeTokens() {
        _accessToken = ""
        _refreshToken = ""

        context.dataStore.edit { settings ->
            settings[accessTokenKey] = ""
            settings[refreshTokenKey] = ""
        }
    }

    override suspend fun removeUser() {
        context.userPrefsDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder().run {
                clearId()
                clearName()
                clearFamilyName()
                clearEmail()
                clearPictureUrl()
                build()
            }
        }
    }

    private companion object {
        private const val PREFERENCES_NAME = "preference"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }
}
