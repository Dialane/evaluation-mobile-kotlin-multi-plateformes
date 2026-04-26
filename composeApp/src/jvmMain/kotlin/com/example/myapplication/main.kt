package com.example.myapplication

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.myapplication.di.createAppDependencies
import com.example.myapplication.presentation.ui.AppPlatform

fun main() = application {
    val dependencies = createAppDependencies()
    Window(
        onCloseRequest = ::exitApplication,
        title = "MyApplication",
    ) {
        App(
            dependencies = dependencies,
            platform = AppPlatform.Desktop,
        )
    }
}
