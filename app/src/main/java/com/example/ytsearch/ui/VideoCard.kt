package com.example.ytsearch.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ytsearch.model.VideoResult

@Composable
fun VideoCard(item: VideoResult) {
  Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(40.dp))
      Spacer(Modifier.width(12.dp))
      Column {
        Text(item.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
        Text("${item.channel} • ${item.views} • ${item.duration}",
          style = MaterialTheme.typography.bodySmall)
      }
    }
  }
}
