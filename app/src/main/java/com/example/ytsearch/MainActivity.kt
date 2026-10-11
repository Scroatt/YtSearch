package com.example.ytsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.ytsearch.ui.SearchScreen

class MainActivity : ComponentActivity() {
  private val vm: SearchViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface { SearchScreen(vm) }
      }
    }
  }
}
