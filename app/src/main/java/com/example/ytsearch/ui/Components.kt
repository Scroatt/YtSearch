package com.example.ytsearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ytsearch.model.VideoResult

private val thumbs = listOf(
  0xFF5B2EE5 to 0xFF00C2A8,
  0xFFE5484D to 0xFFFF8A00,
  0xFF0066FF to 0xFF00E5FF,
  0xFF7C4DFF to 0xFFFF4D94,
  0xFF00B36B to 0xFFB8E600
)

@Composable
fun ThumbArt(seed: Int, duration: String, modifier: Modifier = Modifier) {
  val pair = thumbs[kotlin.math.abs(seed) % thumbs.size]
  Box(
    modifier = modifier
      .aspectRatio(16f / 9f)
      .clip(RoundedCornerShape(12.dp))
      .background(Brush.linearGradient(listOf(Color(pair.first), Color(pair.second))))
  ) {
    Text(
      "LUME PLAY",
      modifier = Modifier.align(Alignment.Center),
      style = MaterialTheme.typography.titleLarge,
      color = Color.White.copy(alpha = 0.9f)
    )
    Text(
      duration,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(6.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.Black.copy(alpha = 0.75f))
        .padding(horizontal = 6.dp, vertical = 2.dp),
      style = MaterialTheme.typography.labelSmall,
      color = Color.White
    )
  }
}

@Composable
fun FeedCard(item: VideoResult, onOpen: (String) -> Unit, onMenu: (String) -> Unit = {}) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpen(item.id) }
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    ThumbArt(item.seed, item.duration, Modifier.fillMaxWidth())
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Top) {
      Icon(Icons.Filled.AccountCircle, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.secondary)
      Spacer(Modifier.width(10.dp))
      Column(Modifier.weight(1f)) {
        Text(item.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("${item.channel} • ${item.views}", style = MaterialTheme.typography.bodySmall)
      }
      IconButton(onClick = { onMenu(item.id) }) { Icon(Icons.Filled.MoreVert, null) }
    }
  }
}
