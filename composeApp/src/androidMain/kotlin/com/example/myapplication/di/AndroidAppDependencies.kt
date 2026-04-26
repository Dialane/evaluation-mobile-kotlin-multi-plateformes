package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.cross.SoundManager
import com.example.myapplication.cross.PlatformContext
import com.example.myapplication.data.location.DefaultLocationRepository
import com.example.myapplication.data.location.local.LocationCache
import com.example.myapplication.data.location.remote.LocationApi
import com.example.myapplication.data.network.AppJson
import com.example.myapplication.data.network.createHttpClient
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

/**
 * Extension de Context (Android) : point d'entrée de l'injection.
 * Le Context reste cantonné au wiring (pas de fuite vers Domain / Presentation).
 */
fun Context.createAppDependencies(): AppDependencies {
    val httpClient = createHttpClient()
    val settings = appSettings()
    val cache = LocationCache(settings = settings, json = AppJson)
    val api = LocationApi(httpClient)

    return DefaultAppDependencies(
        locationRepository = DefaultLocationRepository(remote = api, cache = cache),
        soundManager = SoundManager(PlatformContext(applicationContext)),
    )
}

private fun Context.appSettings(): Settings =
    SharedPreferencesSettings(
        getSharedPreferences("rm_locations_cache", Context.MODE_PRIVATE),
    )
