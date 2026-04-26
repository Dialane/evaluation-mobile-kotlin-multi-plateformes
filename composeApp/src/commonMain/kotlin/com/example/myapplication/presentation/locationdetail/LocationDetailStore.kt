package com.example.myapplication.presentation.locationdetail

import com.example.myapplication.domain.location.LocationId
import com.example.myapplication.domain.location.usecase.GetLocationDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.presentation.util.toUserMessage

class LocationDetailStore(
    private val getLocationDetail: GetLocationDetail,
    dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _state = MutableStateFlow(LocationDetailState())
    val state: StateFlow<LocationDetailState> = _state.asStateFlow()

    fun dispatch(intent: LocationDetailIntent) {
        when (intent) {
            is LocationDetailIntent.Load -> load(intent.id, forceRefresh = false)
            LocationDetailIntent.Retry -> {
                val id = _state.value.locationId ?: return
                load(id, forceRefresh = true)
            }
        }
    }

    private fun load(id: LocationId, forceRefresh: Boolean) {
        scope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                locationId = id,
                errorMessage = null,
            )
            runCatching { getLocationDetail(id = id, forceRefresh = forceRefresh) }
                .onSuccess { location ->
                    _state.value = _state.value.copy(isLoading = false, location = location, errorMessage = null)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(isLoading = false, errorMessage = error.toUserMessage())
                }
        }
    }

    fun close() {
        scope.cancel()
    }
}
