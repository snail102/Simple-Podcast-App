package ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.di

import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.data.MediaPlayerControlImpl
import ru.anydevprojects.simplepodcastapp.core.mediaPlayerControl.domain.MediaPlayerControl

val mediaPlayerControlModule = module {
    single<MediaPlayerControl> { MediaPlayerControlImpl(get()) }
}
