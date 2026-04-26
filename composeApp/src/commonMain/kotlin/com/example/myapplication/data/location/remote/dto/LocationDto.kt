package com.example.myapplication.data.location.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String> = emptyList(),
    val url: String,
    val created: String,
)

@Serializable
data class LocationsResponseDto(
    val info: LocationsInfoDto,
    val results: List<LocationDto>,
)

@Serializable
data class LocationsInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null,
)
