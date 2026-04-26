package com.example.myapplication.domain.location

@JvmInline
value class LocationId(val value: Int)

data class LocationSummary(
    val id: LocationId,
    val name: String,
    val type: String,
    val dimension: String,
    val residentCount: Int,
)

data class LocationDetail(
    val id: LocationId,
    val name: String,
    val type: String,
    val dimension: String,
    val residentCount: Int,
    val residentIds: List<Int>,
)

