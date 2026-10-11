package com.example.ytsearch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytsearch.data.SearchRepository
import com.example.ytsearch.data.local.AppDatabase
import com.example.ytsearch.model.VideoResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
  val query: String = "",
  val category: String = "Tudo",
  val suggestions: List<String> = emptyList(),
  val results: List<VideoResult> = emptyList(),
  val history: List<String> = emptyList(),
  val subscriptions: Set<String> = emptySet(),
  val likes: Set<String> = emptySet(),
  val loading: Boolean = false,
  val voiceError: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(app: Application) : AndroidViewModel(app) {
  private val dao = AppDatabase.get(app).historyDao()
  val repo = SearchRepository(dao)
  private val queryFlow = MutableStateFlow("")

  private val _ui = MutableStateFlow(SearchUiState())
  val ui = _ui.asStateFlow()

  init {
    viewModelScope.launch {
      repo.history().collectLatest { rows ->
        _ui.update { it.copy(history = rows.map { e -> e.query }) }
      }
    }
    viewModelScope.launch {
      queryFlow.debounce(300).collectLatest { q ->
        _ui.update { it.copy(suggestions = if (q.isBlank()) emptyList() else repo.suggest(q)) }
      }
    }
  }

  fun onQueryChange(q: String) {
    _ui.update { it.copy(query = q, voiceError = null) }
    queryFlow.value = q
  }

  fun onCategory(c: String) {
    _ui.update { it.copy(category = c) }
  }

  fun submit(q: String = _ui.value.query) {
    val query = q.trim()
    if (query.isEmpty()) return
    viewModelScope.launch {
      _ui.update { it.copy(loading = true) }
      repo.saveQuery(query)
      _ui.update { it.copy(results = repo.search(query), loading = false, suggestions = emptyList()) }
    }
  }

  fun voiceResult(text: String?) {
    if (text.isNullOrBlank()) {
      _ui.update { it.copy(voiceError = "Não entendi. Toque no microfone e tente de novo.") }
    } else {
      onQueryChange(text)
      submit(text)
    }
  }

  fun toggleSub(channel: String) {
    _ui.update { it.copy(subscriptions = if (channel in it.subscriptions) it.subscriptions - channel else it.subscriptions + channel) }
  }

  fun toggleLike(id: String) {
    _ui.update { it.copy(likes = if (id in it.likes) it.likes - id else it.likes + id) }
  }

  fun deleteHistory(q: String) = viewModelScope.launch { repo.deleteQuery(q) }
  fun clearHistory() = viewModelScope.launch { repo.clearHistory() }
}
