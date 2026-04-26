package com.example.myapplication.presentation.platform

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Desktop: pas de bouton back système à gérer ici.
}

