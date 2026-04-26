package com.example.myapplication.presentation.locationlist

import com.example.myapplication.cross.SoundManager
import com.example.myapplication.domain.location.usecase.GetLocations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myapplication.presentation.util.toUserMessage

class LocationListStore(
    private val getLocations: GetLocations,
    private val soundManager: SoundManager,
    dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _state = MutableStateFlow(LocationListState())
    val state: StateFlow<LocationListState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LocationListEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<LocationListEffect> = _effects.asSharedFlow()

    fun dispatch(intent: LocationListIntent) {
        when (intent) {
            LocationListIntent.Load -> load(forceRefresh = false)
            LocationListIntent.Retry -> load(forceRefresh = true)
            is LocationListIntent.ClickLocation -> {
                soundManager.playUiClick()
                _effects.tryEmit(LocationListEffect.OpenLocation(intent.id))
            }
        }
    }

    private fun load(forceRefresh: Boolean) {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            runCatching { getLocations(forceRefresh = forceRefresh) }
                .onSuccess { items ->
                    _state.value = _state.value.copy(isLoading = false, items = items, errorMessage = null)
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
