package com.example.ytsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ytsearch.SearchViewModel

@Composable
fun LibraryScreen(vm: SearchViewModel, onOpen: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  LazyColumn(Modifier.fillMaxSize()) {
    item {
      Column(Modifier.padding(16.dp)) {
        Text("Biblioteca", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
      }
    }
    item {
      ListItem(
        headlineContent = { Text("Histórico (${state.history.size})") },
        leadingContent = { Icon(Icons.Filled.History, null) }
      )
    }
    item {
      ListItem(
        headlineContent = { Text("Inscrições (${state.subscriptions.size})") },
        leadingContent = { Icon(Icons.Filled.Subscriptions, null) },
        supportingContent = {
          Text(if (state.subscriptions.isEmpty()) "Nenhuma ainda" else state.subscriptions.joinToString())
        }
      )
    }
    item {
      ListItem(
        headlineContent = { Text("Curtidos (${state.likes.size})") },
        leadingContent = { Icon(Icons.Filled.ThumbUp, null) }
      )
    }
    item {
      ListItem(
        headlineContent = { Text("Downloads") },
        leadingContent = { Icon(Icons.Filled.Download, null) },
        supportingContent = { Text("Em breve neste MVP") }
      )
    }
    if (state.history.isNotEmpty()) {
      item { Text("Assistidos recentemente", Modifier.padding(16.dp), style = MaterialTheme.typography.titleSmall) }
      state.history.forEach { h ->
        item {
          Row(
            Modifier.fillMaxWidth().clickable { vm.onQueryChange(h); vm.submit(h) }.padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Icon(Icons.Filled.History, null)
            Text("  $h", style = MaterialTheme.typography.bodyMedium)
          }
        }
      }
    }
  }
}
