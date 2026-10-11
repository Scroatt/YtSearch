package com.example.ytsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ytsearch.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(vm: SearchViewModel, id: String, onOpen: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  val video = vm.repo.byId(id)
  val subscribed = video.channel in state.subscriptions
  val liked = video.id in state.likes
  var playing by rememberSaveable { mutableStateOf(true) }
  var progress by rememberSaveable { mutableFloatStateOf(0.05f) }
  var expanded by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(playing) {
    while (playing && progress < 1f) {
      delay(500)
      progress = (progress + 0.01f).coerceAtMost(1f)
    }
  }

  LazyColumn(Modifier.fillMaxSize()) {
    item {
      Column(Modifier.padding(12.dp)) {
        ThumbArt(video.seed, video.duration, Modifier.fillMaxWidth())
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { playing = !playing }) {
            Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, null,
              Modifier.size(36.dp))
          }
          LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text(video.title, style = MaterialTheme.typography.titleMedium)
        Text("${video.views} • ${video.category}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.AccountCircle, null, Modifier.size(36.dp),
            tint = MaterialTheme.colorScheme.secondary)
          Text("  ${video.channel}\n  ${video.subs}", style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f))
          if (subscribed) {
            OutlinedButton(onClick = { vm.toggleSub(video.channel) }) { Text("Inscrito") }
          } else {
            Button(onClick = { vm.toggleSub(video.channel) }) { Text("Inscrever-se") }
          }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(onClick = { vm.toggleLike(video.id) }) {
            Icon(if (liked) Icons.Filled.ThumbUp else Icons.Filled.ThumbDown, null)
          }
          Text(video.likes, style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterVertically))
          Spacer(Modifier.weight(1f))
          IconButton(onClick = {}) { Icon(Icons.Filled.Share, null) }
          IconButton(onClick = {}) { Icon(Icons.Filled.Download, null) }
        }
        Text(
          if (expanded) video.description + " Categoria: ${video.category}." else "Descrição",
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.clickable { expanded = !expanded }.padding(vertical = 4.dp)
        )
        TextButton(onClick = { expanded = !expanded }) {
          Text(if (expanded) "Mostrar menos" else "Mostrar mais")
        }
        Text("A seguir", style = MaterialTheme.typography.titleSmall)
      }
    }
    items(vm.repo.related(video), key = { it.id }) { v -> FeedCard(v, onOpen) }
  }
}
