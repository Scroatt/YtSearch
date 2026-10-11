package com.example.ytsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ytsearch.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm: SearchViewModel, onOpen: (String) -> Unit, onVoice: () -> Unit) {
  val state by vm.ui.collectAsState()
  var active by rememberSaveable { mutableStateOf(false) }

  Column(Modifier.fillMaxSize()) {
    SearchBar(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      query = state.query,
      onQueryChange = vm::onQueryChange,
      onSearch = { vm.submit(it); active = false },
      active = active,
      onActiveChange = { active = it },
      placeholder = { Text("Pesquisar") },
      leadingIcon = { Icon(Icons.Filled.Search, null) },
      trailingIcon = {
        Row {
          if (state.query.isNotEmpty()) {
            IconButton(onClick = { vm.onQueryChange("") }) { Icon(Icons.Filled.Clear, null) }
          }
          IconButton(onClick = onVoice) { Icon(Icons.Filled.Mic, null) }
        }
      }
    ) {
      state.suggestions.forEach { s ->
        ListItem(
          headlineContent = { Text(s) },
          leadingContent = { Icon(Icons.Filled.Search, null) },
          modifier = Modifier.clickable { vm.onQueryChange(s); vm.submit(s); active = false }
        )
      }
      if (state.history.isNotEmpty()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Recentes")
          TextButton(onClick = vm::clearHistory) { Text("Limpar") }
        }
        state.history.forEach { h ->
          ListItem(
            headlineContent = { Text(h) },
            leadingContent = { Icon(Icons.Filled.History, null) },
            trailingContent = {
              IconButton(onClick = { vm.deleteHistory(h) }) { Icon(Icons.Filled.Close, null) }
            },
            modifier = Modifier.clickable { vm.onQueryChange(h); vm.submit(h); active = false }
          )
        }
      }
    }

    state.voiceError?.let { Text(it, modifier = Modifier.padding(horizontal = 16.dp)) }

    if (!active && state.history.isNotEmpty() && state.results.isEmpty()) {
      Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        state.history.take(5).forEach { h ->
          AssistChip(onClick = { vm.onQueryChange(h); vm.submit(h) }, label = { Text(h) })
        }
      }
    }

    if (state.loading) {
      CircularProgressIndicator(Modifier.padding(24.dp))
    } else {
      LazyColumn {
        items(state.results, key = { it.id }) { v -> FeedCard(v, onOpen) }
      }
    }
  }
}
