package ru.anydevprojects.simplepodcastapp.ui.components.bottomsheet

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SheetExpanded(currentFraction: Float, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceDim)
            .fillMaxSize()
            .navigationBarsPadding()
            .graphicsLayer(alpha = getAlpha(currentFraction))
    ) {
        content()
    }
}

private fun getAlpha(currentFraction: Float): Float {
    val result = when {
        currentFraction <= 0.3f -> 0f
        currentFraction >= 0.7f -> 1f
        else -> 1f - (currentFraction + 0.3f) / 0.4f
    }
    Log.d("getAlpha11", result.toString())
    return result
}
