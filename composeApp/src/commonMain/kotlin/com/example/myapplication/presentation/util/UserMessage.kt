package com.example.myapplication.presentation.util

internal fun Throwable.toUserMessage(): String {
    val raw = message?.trim().orEmpty()
    if (raw.isBlank()) return "Une erreur est survenue"
    if (raw.startsWith("Value too long", ignoreCase = true)) {
        return "Erreur de cache local (valeur trop longue)"
    }
    return raw.take(220)
}

