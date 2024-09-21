package ru.anydevprojects.simplepodcastapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : ViewState, ContentState : ContentViewState, I : ViewIntent, E : ViewEvent>(
    initialStateAndDefaultContentState: () -> Pair<State, ContentState>
) : ViewModel() {

    private val _event = Channel<E>(capacity = Channel.CONFLATED)
    val event = _event.receiveAsFlow()

    private val _viewStateFlow: MutableStateFlow<State> =
        MutableStateFlow(initialStateAndDefaultContentState().first)

    val stateFlow: StateFlow<State> = _viewStateFlow.onStart {
        onStart()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initialStateAndDefaultContentState().first
    )

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

    open fun onStart() {}

    protected fun emitEvent(event: E) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
