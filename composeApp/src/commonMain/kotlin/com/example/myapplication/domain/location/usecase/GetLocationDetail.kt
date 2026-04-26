package com.example.myapplication.domain.location.usecase

import com.example.myapplication.domain.location.LocationDetail
import com.example.myapplication.domain.location.LocationId
import com.example.myapplication.domain.location.LocationRepository

class GetLocationDetail(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(id: LocationId, forceRefresh: Boolean = false): LocationDetail =
        repository.getLocation(id = id, forceRefresh = forceRefresh)
}

