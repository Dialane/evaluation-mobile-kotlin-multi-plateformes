package com.example.myapplication.di

import com.example.myapplication.cross.SoundManager
import com.example.myapplication.domain.location.LocationRepository

interface AppDependencies {
    val locationRepository: LocationRepository
    val soundManager: SoundManager
}

