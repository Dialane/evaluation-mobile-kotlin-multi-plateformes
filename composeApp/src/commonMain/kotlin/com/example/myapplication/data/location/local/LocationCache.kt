package com.example.myapplication.data.location.local

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.russhwolf.settings.Settings

class LocationCache(
    private val settings: Settings,
    private val json: Json,
) {
    fun readLocationListPage1(): List<CachedLocationSummary>? =
        settings.getStringOrNullCompat(KEY_LOCATIONS_PAGE_1)?.let { stored ->
            runCatching { json.decodeFromString<List<CachedLocationSummary>>(stored) }.getOrNull()
        }

    fun writeLocationListPage1(locations: List<CachedLocationSummary>) {
        settings.putString(KEY_LOCATIONS_PAGE_1, json.encodeToString(locations))
    }

    fun readLocationDetail(id: Int): CachedLocationDetail? =
        settings.getStringOrNullCompat(detailKey(id))?.let { stored ->
            runCatching { json.decodeFromString<CachedLocationDetail>(stored) }.getOrNull()
        }

    fun writeLocationDetail(location: CachedLocationDetail) {
        settings.putString(detailKey(location.id), json.encodeToString(location))
    }

    private fun detailKey(id: Int): String = "location_detail_$id"

    private companion object {
        private const val KEY_LOCATIONS_PAGE_1 = "locations_page_1"
    }
}

private fun Settings.getStringOrNullCompat(key: String): String? =
    if (hasKey(key)) getString(key, "") else null

@Serializable
data class CachedLocationSummary(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentCount: Int,
)

@Serializable
data class CachedLocationDetail(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentCount: Int,
    val residentIds: List<Int>,
)
