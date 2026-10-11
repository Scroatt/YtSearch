package com.example.ytsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ytsearch.ui.HomeScreen
import com.example.ytsearch.ui.LibraryScreen
import com.example.ytsearch.ui.LumePlayTheme
import com.example.ytsearch.ui.PlayerScreen
import com.example.ytsearch.ui.SearchScreen
import com.example.ytsearch.ui.ShortsScreen
import com.example.ytsearch.ui.VoiceSearchScreen

class MainActivity : ComponentActivity() {
  private val vm: SearchViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LumePlayTheme {
        Surface { LumeNav(vm) }
      }
    }
  }
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
  Tab("home", "Início", Icons.Filled.Home),
  Tab("shorts", "Shorts", Icons.AutoMirrored.Filled.PlaylistPlay),
  Tab("search", "Busca", Icons.Filled.Search),
  Tab("library", "Você", Icons.Filled.VideoLibrary)
)

@Composable
private fun LumeNav(vm: SearchViewModel) {
  val nav = rememberNavController()
  val entry by nav.currentBackStackEntryAsState()
  val route = entry?.destination?.route ?: "home"
  val showBar = route in tabs.map { it.route }

  Scaffold(
    bottomBar = {
      if (showBar) {
        NavigationBar {
          tabs.forEach { t ->
            NavigationBarItem(
              selected = route == t.route,
              onClick = { nav.navigate(t.route) { launchSingleTop = true } },
              icon = { Icon(t.icon, null) },
              label = { Text(t.label) }
            )
          }
        }
      }
    }
  ) { pad ->
    NavHost(nav, startDestination = "home", Modifier.padding(pad)) {
      composable("home") { HomeScreen(vm, onOpen = { nav.navigate("player/$it") }) }
      composable("shorts") { ShortsScreen(vm, onOpen = { nav.navigate("player/$it") }) }
      composable("search") {
        SearchScreen(vm, onOpen = { nav.navigate("player/$it") }, onVoice = { nav.navigate("voice") })
      }
      composable("voice") {
        VoiceSearchScreen(vm, onDone = { nav.navigate("search") { launchSingleTop = true } })
      }
      composable("library") { LibraryScreen(vm, onOpen = { nav.navigate("player/$it") }) }
      composable(
        "player/{id}",
        arguments = listOf(navArgument("id") { type = NavType.StringType })
      ) { back ->
        PlayerScreen(vm, back.arguments?.getString("id") ?: "1",
          onOpen = { nav.navigate("player/$it") })
      }
    }
  }
}
