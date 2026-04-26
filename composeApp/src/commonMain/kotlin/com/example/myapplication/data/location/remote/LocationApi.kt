package com.example.myapplication.data.location.remote

import com.example.myapplication.data.location.remote.dto.LocationDto
import com.example.myapplication.data.location.remote.dto.LocationsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class LocationApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://rickandmortyapi.com/api",
) {
    suspend fun getLocations(page: Int = 1): List<LocationDto> =
        httpClient.get("$baseUrl/location") {
            parameter("page", page)
        }.body<LocationsResponseDto>().results

    suspend fun getLocation(id: Int): LocationDto =
        httpClient.get("$baseUrl/location/$id").body()
}

