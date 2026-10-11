package com.example.ytsearch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ytsearch.SearchViewModel

@Composable
fun HomeScreen(vm: SearchViewModel, onOpen: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  Column(Modifier.fillMaxSize()) {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(Icons.Filled.PlayCircle, null, tint = MaterialTheme.colorScheme.primary)
      Text("  Lume Play", style = MaterialTheme.typography.titleLarge)
    }
    LazyRow(
      contentPadding = PaddingValues(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(vm.repo.categories) { c ->
        FilterChip(
          selected = state.category == c,
          onClick = { vm.onCategory(c) },
          label = { Text(c) }
        )
      }
    }
    if (state.feedLoading) {
      CircularProgressIndicator(Modifier.padding(24.dp))
    }
    LazyColumn(Modifier.fillMaxSize()) {
      items(state.feed, key = { it.id }) { v ->
        FeedCard(v, onOpen)
      }
    }
  }
}
