package ru.anydevprojects.simplepodcastapp.authorization.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.anydevprojects.simplepodcastapp.authorization.data.AuthorizationRepositoryImpl
import ru.anydevprojects.simplepodcastapp.authorization.domain.AuthorizationRepository
import ru.anydevprojects.simplepodcastapp.authorization.presentaion.AuthorizationViewModel
import ru.anydevprojects.simplepodcastapp.core.network.networkClientLogicServerQualifier

val authorizationModule = module {
    viewModel {
        AuthorizationViewModel(get(), get())
    }
    factory<AuthorizationRepository> {
        AuthorizationRepositoryImpl(
            httpClient = get(
                qualifier = networkClientLogicServerQualifier
            ),
            get()
        )
    }
}
