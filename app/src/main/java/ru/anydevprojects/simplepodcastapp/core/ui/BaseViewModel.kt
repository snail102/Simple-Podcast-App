package ru.anydevprojects.simplepodcastapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<State : ViewState, ContentState : ContentViewState, I : ViewIntent, E : ViewEvent>(
    initialStateAndDefaultContentState: () -> Pair<State, ContentState>
) : ViewModel() {

    val events = EventQueue<E>()

    private val _viewStateFlow: MutableStateFlow<State> =
        MutableStateFlow(initialStateAndDefaultContentState().first)

    val stateFlow: StateFlow<State> = _viewStateFlow

    var lastContentState: ContentState = initialStateAndDefaultContentState().second
        private set

    protected fun updateState(newState: State) {
        @Suppress("UNCHECKED_CAST")
        if (newState is ContentViewState) {
            lastContentState = newState as ContentState
        }

        _viewStateFlow.value = newState
    }

    abstract fun onIntent(intent: I)

    protected fun emitEvent(event: E) {
        events.offerEvent(viewModelScope, event)
    }
}
