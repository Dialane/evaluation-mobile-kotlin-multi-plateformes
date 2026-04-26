package com.example.myapplication.data.location

import com.example.myapplication.data.location.local.LocationCache
import com.example.myapplication.data.location.local.CachedLocationDetail
import com.example.myapplication.data.location.local.CachedLocationSummary
import com.example.myapplication.data.location.remote.LocationApi
import com.example.myapplication.data.location.remote.dto.LocationDto
import com.example.myapplication.domain.location.LocationDetail
import com.example.myapplication.domain.location.LocationId
import com.example.myapplication.domain.location.LocationRepository
import com.example.myapplication.domain.location.LocationSummary

class DefaultLocationRepository(
    private val remote: LocationApi,
    private val cache: LocationCache,
) : LocationRepository {
    override suspend fun getLocations(forceRefresh: Boolean): List<LocationSummary> {
        if (!forceRefresh) {
            cache.readLocationListPage1()?.let { cached ->
                return cached.map { it.toDomain() }
            }
        }

        // Fetch strategy (2 sources) :
        // - si le remote échoue, on tente de retomber sur le cache local (si présent)
        // - sinon on remonte l'erreur pour que la Presentation puisse afficher un retry
        return runCatching {
            val remoteLocations = remote.getLocations(page = 1)
            val summaries = remoteLocations.map { it.toSummary() }
            cache.writeLocationListPage1(summaries.map { it.toCache() })
            summaries
        }.getOrElse { error ->
            cache.readLocationListPage1()?.map { it.toDomain() } ?: throw error
        }
    }

    override suspend fun getLocation(id: LocationId, forceRefresh: Boolean): LocationDetail {
        if (!forceRefresh) {
            cache.readLocationDetail(id.value)?.let { cached ->
                return cached.toDomain()
            }
        }

        return runCatching {
            val remoteLocation = remote.getLocation(id.value)
            val detail = remoteLocation.toDetail()
            cache.writeLocationDetail(detail.toCache())
            detail
        }.getOrElse { error ->
            cache.readLocationDetail(id.value)?.toDomain() ?: throw error
        }
    }
}

private fun LocationDto.toSummary(): LocationSummary =
    LocationSummary(
        id = LocationId(id),
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residents.size,
    )

private fun LocationDto.toDetail(): LocationDetail {
    val residentIds = residents.mapNotNull(::residentIdFromUrl)
    return LocationDetail(
        id = LocationId(id),
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residents.size,
        residentIds = residentIds,
    )
}

private fun residentIdFromUrl(url: String): Int? =
    url.substringAfterLast('/', missingDelimiterValue = "").toIntOrNull()

private fun CachedLocationSummary.toDomain(): LocationSummary =
    LocationSummary(
        id = LocationId(id),
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residentCount,
    )

private fun LocationSummary.toCache(): CachedLocationSummary =
    CachedLocationSummary(
        id = id.value,
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residentCount,
    )

private fun CachedLocationDetail.toDomain(): LocationDetail =
    LocationDetail(
        id = LocationId(id),
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residentCount,
        residentIds = residentIds,
    )

private fun LocationDetail.toCache(): CachedLocationDetail =
    CachedLocationDetail(
        id = id.value,
        name = name,
        type = type,
        dimension = dimension,
        residentCount = residentCount,
        residentIds = residentIds.take(30),
    )
