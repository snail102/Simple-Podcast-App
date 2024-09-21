package ru.anydevprojects.simplepodcastapp.app

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import ru.anydevprojects.simplepodcastapp.BuildConfig
import ru.anydevprojects.simplepodcastapp.core.CredentialsProvider
import ru.anydevprojects.simplepodcastapp.di.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        CredentialsProvider.setAPICredentials(
            key = BuildConfig.API_KEY,
            secret = BuildConfig.SECRET_KEY
        )

        CredentialsProvider.setGoogleId(BuildConfig.WEB_CLIENT_ID)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
