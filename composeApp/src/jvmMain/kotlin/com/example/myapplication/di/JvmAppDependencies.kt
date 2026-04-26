package com.example.myapplication.di

import com.example.myapplication.cross.PlatformContext
import com.example.myapplication.cross.SoundManager
import com.example.myapplication.data.location.DefaultLocationRepository
import com.example.myapplication.data.location.local.LocationCache
import com.example.myapplication.data.location.remote.LocationApi
import com.example.myapplication.data.network.AppJson
import com.example.myapplication.data.network.createHttpClient
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

fun createAppDependencies(): AppDependencies {
    val httpClient = createHttpClient()
    val preferences = Preferences.userRoot().node("rm_locations_cache")
    val settings = PreferencesSettings(preferences)
    val cache = LocationCache(settings = settings, json = AppJson)
    val api = LocationApi(httpClient)

    return DefaultAppDependencies(
        locationRepository = DefaultLocationRepository(remote = api, cache = cache),
        soundManager = SoundManager(PlatformContext(Unit)),
    )
}
