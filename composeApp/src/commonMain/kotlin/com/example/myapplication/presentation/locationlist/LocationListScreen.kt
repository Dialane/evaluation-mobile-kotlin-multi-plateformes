package com.example.myapplication.presentation.locationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.presentation.ui.components.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    store: LocationListStore,
    title: String,
    modifier: Modifier = Modifier,
) {
    val state by store.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        when {
            state.isLoading && state.items.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null && state.items.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Impossible de charger les locations",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = state.errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    FilledTonalButton(
                        onClick = { store.dispatch(LocationListIntent.Retry) },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text("Réessayer")
                    }
                }
            }

            state.items.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Aucune location à afficher",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    FilledTonalButton(
                        onClick = { store.dispatch(LocationListIntent.Load) },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text("Charger")
                    }
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.isLoading) {
                    item(key = "loading_indicator") {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
                items(state.items, key = { it.id.value }) { item ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { store.dispatch(LocationListIntent.ClickLocation(item.id)) },
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (item.type.isNotBlank()) Tag(text = item.type)
                                if (item.dimension.isNotBlank()) Tag(text = item.dimension)
                                Tag(text = "${item.residentCount} résidents")
                            }
                        }
                    }
                }
            }
        }
    }
}
