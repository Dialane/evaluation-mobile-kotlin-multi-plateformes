package com.example.myapplication.domain.location.usecase

import com.example.myapplication.domain.location.LocationRepository
import com.example.myapplication.domain.location.LocationSummary

class GetLocations(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): List<LocationSummary> =
        repository.getLocations(forceRefresh = forceRefresh)
}

