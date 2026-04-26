package com.example.myapplication.cross

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

actual class SoundManager actual constructor(
    platformContext: PlatformContext,
) {
    private val appContext: Context = platformContext.raw as Context

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private var isLoaded: Boolean = false
    private val soundId: Int = soundPool.load(appContext, resolveRawResId("sus_meme_click"), 1)

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (sampleId == soundId && status == 0) {
                isLoaded = true
            }
        }
    }

    actual fun playUiClick() {
        if (!isLoaded) return
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    private fun resolveRawResId(entryName: String): Int {
        val id = appContext.resources.getIdentifier(entryName, "raw", appContext.packageName)
        require(id != 0) {
            "Raw resource not found: res/raw/$entryName.(mp3|wav|ogg)"
        }
        return id
    }
}

