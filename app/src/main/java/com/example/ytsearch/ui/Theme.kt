package com.example.ytsearch.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LumePurple = Color(0xFF5B2EE5)
private val LumeTeal = Color(0xFF00C2A8)
private val LumeDark = Color(0xFF14101F)

private val Light = lightColorScheme(
  primary = LumePurple,
  secondary = LumeTeal,
  tertiary = LumeTeal
)

private val Dark = darkColorScheme(
  primary = Color(0xFFB79CFF),
  secondary = LumeTeal,
  tertiary = LumeTeal,
  background = LumeDark,
  surface = LumeDark
)

@Composable
fun LumePlayTheme(dark: Boolean = false, content: @Composable () -> Unit) {
  MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
