package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.di.AppDependencies
import com.example.myapplication.domain.location.LocationId
import com.example.myapplication.domain.location.usecase.GetLocationDetail
import com.example.myapplication.domain.location.usecase.GetLocations
import com.example.myapplication.presentation.locationdetail.LocationDetailIntent
import com.example.myapplication.presentation.locationdetail.LocationDetailScreen
import com.example.myapplication.presentation.locationdetail.LocationDetailStore
import com.example.myapplication.presentation.locationlist.LocationListEffect
import com.example.myapplication.presentation.locationlist.LocationListIntent
import com.example.myapplication.presentation.locationlist.LocationListScreen
import com.example.myapplication.presentation.locationlist.LocationListStore
import com.example.myapplication.presentation.platform.PlatformBackHandler
import com.example.myapplication.presentation.theme.AppTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun AppRoot(
    dependencies: AppDependencies,
    platform: AppPlatform,
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val listStore = remember {
                LocationListStore(
                    getLocations = GetLocations(dependencies.locationRepository),
                    soundManager = dependencies.soundManager,
                    dispatcher = Dispatchers.Default,
                )
            }
            DisposableEffect(Unit) {
                onDispose { listStore.close() }
            }
            LaunchedEffect(Unit) {
                listStore.dispatch(LocationListIntent.Load)
            }

            when (platform) {
                AppPlatform.Android -> MobileRoot(
                    listStore = listStore,
                    dependencies = dependencies,
                )
                AppPlatform.Desktop -> DesktopRoot(
                    listStore = listStore,
                    dependencies = dependencies,
                )
            }
        }
    }
}

@Composable
private fun MobileRoot(
    listStore: LocationListStore,
    dependencies: AppDependencies,
) {
    var route: MobileRoute by remember { mutableStateOf(MobileRoute.List) }

    LaunchedEffect(listStore) {
        listStore.effects.collect { effect ->
            when (effect) {
                is LocationListEffect.OpenLocation -> route = MobileRoute.Detail(effect.id)
            }
        }
    }

    PlatformBackHandler(enabled = route is MobileRoute.Detail) {
        route = MobileRoute.List
    }

    when (val current = route) {
        MobileRoute.List -> LocationListScreen(
            store = listStore,
            title = "Locations",
            modifier = Modifier.fillMaxSize(),
        )
        is MobileRoute.Detail -> {
            val detailStore = remember(current.id) {
                LocationDetailStore(
                    getLocationDetail = GetLocationDetail(dependencies.locationRepository),
                    dispatcher = Dispatchers.Default,
                )
            }
            DisposableEffect(detailStore) {
                onDispose { detailStore.close() }
            }
            LaunchedEffect(current.id) {
                detailStore.dispatch(LocationDetailIntent.Load(current.id))
            }
            LocationDetailScreen(
                store = detailStore,
                title = "Détail",
                onBack = { route = MobileRoute.List },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DesktopRoot(
    listStore: LocationListStore,
    dependencies: AppDependencies,
) {
    var selectedId: LocationId? by remember { mutableStateOf(null) }

    LaunchedEffect(listStore) {
        listStore.effects.collect { effect ->
            when (effect) {
                is LocationListEffect.OpenLocation -> selectedId = effect.id
            }
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        LocationListScreen(
            store = listStore,
            title = "Locations",
            modifier = Modifier.fillMaxHeight().widthIn(min = 280.dp, max = 420.dp),
        )
        LocationDetailPanel(
            locationId = selectedId,
            dependencies = dependencies,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LocationDetailPanel(
    locationId: LocationId?,
    dependencies: AppDependencies,
    modifier: Modifier,
) {
    if (locationId == null) {
        EmptyDetail(modifier = modifier)
        return
    }

    val store = remember(locationId) {
        LocationDetailStore(
            getLocationDetail = GetLocationDetail(dependencies.locationRepository),
            dispatcher = Dispatchers.Default,
        )
    }
    DisposableEffect(store) {
        onDispose { store.close() }
    }
    LaunchedEffect(locationId) {
        store.dispatch(LocationDetailIntent.Load(locationId))
    }

    LocationDetailScreen(
        store = store,
        title = "Détail",
        onBack = null,
        modifier = modifier,
    )
}
