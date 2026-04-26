package com.example.myapplication.cross

import javazoom.jl.player.Player
import java.awt.Toolkit

actual class SoundManager actual constructor(
    platformContext: PlatformContext,
) {
    actual fun playUiClick() {
        val stream = javaClass.getResourceAsStream("/sounds/sus_meme_click.mp3")
        if (stream == null) {
            Toolkit.getDefaultToolkit().beep()
            return
        }

        Thread(
            {
                runCatching {
                    stream.use { Player(it).play() }
                }
            },
            "ui-click-sound",
        ).apply { isDaemon = true }.start()
    }
}

