package com.example.myapplication.presentation.locationlist

import com.example.myapplication.domain.location.LocationId
import com.example.myapplication.domain.location.LocationSummary

data class LocationListState(
    val isLoading: Boolean = false,
    val items: List<LocationSummary> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface LocationListIntent {
    data object Load : LocationListIntent
    data object Retry : LocationListIntent
    data class ClickLocation(val id: LocationId) : LocationListIntent
}

sealed interface LocationListEffect {
    data class OpenLocation(val id: LocationId) : LocationListEffect
}

