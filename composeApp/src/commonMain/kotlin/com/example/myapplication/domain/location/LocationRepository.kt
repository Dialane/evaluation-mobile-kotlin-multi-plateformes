package com.example.myapplication.domain.location

interface LocationRepository {
    suspend fun getLocations(forceRefresh: Boolean = false): List<LocationSummary>

    suspend fun getLocation(id: LocationId, forceRefresh: Boolean = false): LocationDetail
}

