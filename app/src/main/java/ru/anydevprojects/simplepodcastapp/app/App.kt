package ru.anydevprojects.simplepodcastapp.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import ru.anydevprojects.simplepodcastapp.BuildConfig
import ru.anydevprojects.simplepodcastapp.core.CredentialsProvider
import ru.anydevprojects.simplepodcastapp.di.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CredentialsProvider.setAPICredentials(
            key = BuildConfig.API_KEY,
            secret = BuildConfig.SECRET_KEY
        )

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
