package com.example.ytsearch.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ytsearch.SearchViewModel
import java.util.Locale

@Composable
fun VoiceSearchScreen(vm: SearchViewModel, onDone: (String) -> Unit) {
  val state by vm.ui.collectAsState()
  val context = LocalContext.current

  val speech = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
    if (res.resultCode == Activity.RESULT_OK) {
      val text = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      vm.voiceResult(text)
      if (!text.isNullOrBlank()) onDone(text)
    } else {
      vm.voiceResult(null)
    }
  }

  val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
    if (granted) startVoice(speech) else
      vm.voiceResult(null)
  }

  Column(
    Modifier.fillMaxSize().padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(Icons.Filled.Mic, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(16.dp))
    Text("Busca por voz", style = MaterialTheme.typography.headlineSmall)
    Spacer(Modifier.height(8.dp))
    Text("Toque abaixo e fale o que procura", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(24.dp))
    Button(onClick = {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED
      ) {
        startVoice(speech)
      } else {
        permission.launch(Manifest.permission.RECORD_AUDIO)
      }
    }) {
      Icon(Icons.Filled.Mic, null)
      Text("  Ouvir")
    }
    state.voiceError?.let { Spacer(Modifier.height(12.dp)); Text(it) }
  }
}

private fun startVoice(
  launcher: androidx.activity.result.ActivityResultLauncher<Intent>
) {
  val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale sua busca")
  }
  try {
    launcher.launch(intent)
  } catch (ignored: Exception) {
  }
}
