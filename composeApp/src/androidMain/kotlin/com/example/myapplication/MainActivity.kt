package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.myapplication.di.createAppDependencies
import com.example.myapplication.presentation.ui.AppPlatform

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val dependencies = remember(applicationContext) { applicationContext.createAppDependencies() }
            App(
                dependencies = dependencies,
                platform = AppPlatform.Android,
            )
        }
    }
}
