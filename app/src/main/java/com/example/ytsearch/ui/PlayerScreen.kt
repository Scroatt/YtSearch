package com.example.ytsearch.ui

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ytsearch.SearchViewModel
import kotlinx.coroutines.delay

private fun embedHtml(id: String) =
  "<html><body style=\"margin:0;background:#000\">" +
    "<iframe width=\"100%\" height=\"100%\" " +
    "src=\"https://www.youtube.com/embed/$id?autoplay=1&playsinline=1&rel=0\" " +
    "frameborder=\"0\" allow=\"autoplay; encrypted-media; picture-in-picture\" " +
    "allowfullscreen></iframe></body></html>"

@Composable
private fun YTPlayer(videoId: String) {
  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.mediaPlaybackRequiresUserGesture = false
        webChromeClient = WebChromeClient()
        loadDataWithBaseURL("https://www.youtube.com", embedHtml(videoId), "text/html", "utf-8", null)
      }
    },
    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp))
  )
}

@Composable
fun PlayerScreen(vm: SearchViewModel, id: String, onOpen: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  val video = (state.feed + state.results).firstOrNull { it.id == id } ?: vm.repo.byId(id)
  val real = state.online && id.length == 11
  val subscribed = video.channel in state.subscriptions
  val liked = video.id in state.likes
  var playing by rememberSaveable { mutableStateOf(true) }
  var progress by rememberSaveable { mutableFloatStateOf(0.05f) }
  var expanded by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(id) { vm.ensureDetails(id, video.duration) }
  LaunchedEffect(playing) {
    while (playing && progress < 1f) {
      delay(500)
      progress = (progress + 0.01f).coerceAtMost(1f)
    }
  }

  LazyColumn(Modifier.fillMaxSize()) {
    item {
      Column(Modifier.padding(12.dp)) {
        if (real) {
          YTPlayer(id)
        } else {
          ThumbArt(video.seed, if (state.playerDuration != "--:--") state.playerDuration else video.duration, Modifier.fillMaxWidth(), video.thumb)
          Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { playing = !playing }) {
              Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, null,
                Modifier.size(36.dp))
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f))
          }
        }
        Spacer(Modifier.height(8.dp))
        Text(video.title, style = MaterialTheme.typography.titleMedium)
        Text("${video.views} • ${if (state.playerDuration != "--:--") state.playerDuration else video.duration} • ${video.category}",
          style = MaterialTheme.typography.bodySmall)
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
          if (expanded) video.description.ifBlank { "Sem descrição." } else "Descrição",
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.clickable { expanded = !expanded }.padding(vertical = 4.dp)
        )
        TextButton(onClick = { expanded = !expanded }) {
          Text(if (expanded) "Mostrar menos" else "Mostrar mais")
        }
        if (state.playerComments.isNotEmpty()) {
          Text("Comentários (${state.playerComments.size})", style = MaterialTheme.typography.titleSmall)
          Spacer(Modifier.height(4.dp))
        }
      }
    }
    items(state.playerComments, key = { it.author + it.text.take(20) }) { c ->
      Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text("@${c.author}", style = MaterialTheme.typography.labelLarge)
        Text(c.text, style = MaterialTheme.typography.bodySmall)
        Text("👍 ${c.likes}", style = MaterialTheme.typography.labelSmall)
      }
    }
    item {
      Column(Modifier.padding(12.dp)) {
        Text("A seguir", style = MaterialTheme.typography.titleSmall)
      }
    }
    items(vm.repo.related(video), key = { it.id }) { v -> FeedCard(v, onOpen) }
  }
}
