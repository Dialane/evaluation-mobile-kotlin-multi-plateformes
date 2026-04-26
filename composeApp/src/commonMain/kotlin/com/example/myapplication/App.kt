package com.example.myapplication

import androidx.compose.runtime.Composable
import com.example.myapplication.di.AppDependencies
import com.example.myapplication.presentation.ui.AppPlatform
import com.example.myapplication.presentation.ui.AppRoot

@Composable
fun App(
    dependencies: AppDependencies,
    platform: AppPlatform,
) {
    AppRoot(
        dependencies = dependencies,
        platform = platform,
    )
}
