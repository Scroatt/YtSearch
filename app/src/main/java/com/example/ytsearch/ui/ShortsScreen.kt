package com.example.ytsearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ytsearch.SearchViewModel

@Composable
fun ShortsScreen(vm: SearchViewModel, onOpen: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  val clips = (if (state.feed.isNotEmpty()) state.feed else vm.repo.home("Tudo")).take(6)
  val pager = rememberPagerState(pageCount = { clips.size })
  VerticalPager(state = pager, modifier = Modifier.fillMaxSize()) { i ->
    val v = clips[i]
    val liked = v.id in state.likes
    Box(
      modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(Color(0xFF241B3A), Color(0xFF0E0A18)))
      )
    ) {
      Column(Modifier.align(Alignment.Center)) {
        Icon(Icons.Filled.PlayCircle, null, tint = Color.White.copy(alpha = 0.9f),
          modifier = Modifier.padding(bottom = 12.dp))
        Text(v.title, color = Color.White, style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(horizontal = 32.dp))
      }
      Row(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
        Column(Modifier.weight(1f)) {
          Text("@${v.channel}", color = Color.White, style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.height(4.dp))
          Text(v.title, color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall, maxLines = 2)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          IconButton(onClick = { vm.toggleLike(v.id) }) {
            Icon(if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null,
              tint = if (liked) Color(0xFFFF4D6D) else Color.White)
          }
          Text(v.likes, color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}
