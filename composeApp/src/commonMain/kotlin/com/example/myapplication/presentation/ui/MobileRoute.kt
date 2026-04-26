package com.example.myapplication.presentation.ui

import com.example.myapplication.domain.location.LocationId

sealed interface MobileRoute {
    data object List : MobileRoute
    data class Detail(val id: LocationId) : MobileRoute
}

