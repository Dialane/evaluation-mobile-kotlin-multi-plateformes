package com.example.myapplication.di

import com.example.myapplication.cross.SoundManager
import com.example.myapplication.domain.location.LocationRepository

class DefaultAppDependencies(
    override val locationRepository: LocationRepository,
    override val soundManager: SoundManager,
) : AppDependencies

