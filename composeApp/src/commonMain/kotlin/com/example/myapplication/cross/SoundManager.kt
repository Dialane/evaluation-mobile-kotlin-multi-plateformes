package com.example.myapplication.cross

data class PlatformContext(val raw: Any)

/**
 * Contrat cross-platform (expect/actual) : une action audio simple, utilisée par la couche Presentation
 * lors d'une interaction significative (sélection d'une location).
 */
expect class SoundManager {
    constructor(platformContext: PlatformContext)
    fun playUiClick()
}
