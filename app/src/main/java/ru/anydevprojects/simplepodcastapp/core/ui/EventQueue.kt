package ru.anydevprojects.simplepodcastapp.core.ui

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import java.util.LinkedList
import java.util.Queue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EventQueue<E : ViewEvent> {

    private val sharedFlow = MutableSharedFlow<Queue<E>>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    fun offerEvent(coroutineScope: CoroutineScope, viewEvent: E) {
        val queue = sharedFlow.replayCache.firstOrNull() ?: LinkedList()
        queue.add(viewEvent)

        coroutineScope.launch { sharedFlow.emit(queue) }
    }

    fun collect(
        lifecycleOwner: LifecycleOwner,
        coroutineScope: LifecycleCoroutineScope,
        onEvent: (E) -> Unit
    ) {
        sharedFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { consumeEvents(it, onEvent) }
            .launchIn(coroutineScope)
    }

    fun getFlow() = sharedFlow.map { it.remove() }

    @RestrictTo(RestrictTo.Scope.TESTS)
    fun testCollect(coroutineScope: CoroutineScope, onEvent: (E) -> Unit) {
        sharedFlow.onEach { consumeEvents(it, onEvent) }
            .launchIn(coroutineScope)
    }

    private inline fun consumeEvents(viewEvents: Queue<E>, consumeEvent: (E) -> Unit) {
        while (viewEvents.isNotEmpty()) consumeEvent(viewEvents.remove())
    }
}
