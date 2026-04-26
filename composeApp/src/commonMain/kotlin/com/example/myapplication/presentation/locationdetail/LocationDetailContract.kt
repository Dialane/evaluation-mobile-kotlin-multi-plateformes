package com.example.myapplication.presentation.locationdetail

import com.example.myapplication.domain.location.LocationDetail
import com.example.myapplication.domain.location.LocationId

data class LocationDetailState(
    val isLoading: Boolean = false,
    val locationId: LocationId? = null,
    val location: LocationDetail? = null,
    val errorMessage: String? = null,
)

sealed interface LocationDetailIntent {
    data class Load(val id: LocationId) : LocationDetailIntent
    data object Retry : LocationDetailIntent
}

