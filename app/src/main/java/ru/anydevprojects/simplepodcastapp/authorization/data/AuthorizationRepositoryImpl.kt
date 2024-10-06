package ru.anydevprojects.simplepodcastapp.authorization.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import ru.anydevprojects.simplepodcastapp.authorization.data.models.SignIn
import ru.anydevprojects.simplepodcastapp.authorization.data.models.SignInResponse
import ru.anydevprojects.simplepodcastapp.authorization.data.models.toDomain
import ru.anydevprojects.simplepodcastapp.authorization.domain.AuthorizationRepository
import ru.anydevprojects.simplepodcastapp.core.CredentialsProvider
import ru.anydevprojects.simplepodcastapp.core.token.data.toDomain
import ru.anydevprojects.simplepodcastapp.core.token.domain.TokenRepository
import ru.anydevprojects.simplepodcastapp.core.user.domain.UserRepository

class AuthorizationRepositoryImpl(
    private val httpClient: HttpClient,
    private val applicationContext: Context,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : AuthorizationRepository {
    override suspend fun signInByGoogle(): Result<Unit> {
        return runCatching {
            val credentialManager = CredentialManager.create(applicationContext)

            Log.d("auth", "id ${CredentialsProvider.getWebClientId()}")
            val signInWithGoogleOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(CredentialsProvider.getWebClientId())
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = applicationContext
            )
            handleSignIn(result)
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val googleToken = googleIdTokenCredential.idToken

                        authOnServerLogic(googleToken)

                        Log.d("auth", "token google id ${googleIdTokenCredential.idToken}")
                        Log.d("auth", "givenName ${googleIdTokenCredential.givenName}")
                        Log.d("auth", "familyName ${googleIdTokenCredential.familyName}")
                        Log.d("auth", "displayName ${googleIdTokenCredential.displayName}")
                        Log.d("auth", "phoneNumber ${googleIdTokenCredential.phoneNumber}")
                        Log.d("auth", "id ${googleIdTokenCredential.id}")
                        Log.d(
                            "auth",
                            "profilePictureUri ${googleIdTokenCredential.profilePictureUri}"
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("auth", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("auth", "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("auth", "Unexpected type of credential")
            }
        }
    }

    private suspend fun authOnServerLogic(googleToken: String) {
        val response = httpClient.post("sign_in") {
            setBody(
                SignIn(
                    token = googleToken
                )
            )
        }.body<SignInResponse>()

        tokenRepository.updateTokens(
            token = response.tokens.toDomain()
        )
        userRepository.updateUser(user = response.user.toDomain())
    }
}
