package com.example.ytsearch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ytsearch.data.SearchRepository
import com.example.ytsearch.data.local.AppDatabase
import com.example.ytsearch.model.VideoResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
  val query: String = "",
  val suggestions: List<String> = emptyList(),
  val results: List<VideoResult> = emptyList(),
  val history: List<String> = emptyList(),
  val loading: Boolean = false
)

class SearchViewModel(app: Application) : AndroidViewModel(app) {
  private val dao = AppDatabase.get(app).historyDao()
  private val repo = SearchRepository(dao)
  private val queryFlow = MutableStateFlow("")

  private val _ui = MutableStateFlow(SearchUiState())
  val ui: StateFlow<SearchUiState> = _ui.asStateFlow()

  init {
    viewModelScope.launch {
      repo.history().collectLatest { rows ->
        _ui.update { it.copy(history = rows.map { e -> e.query }) }
      }
    }
    @OptIn(FlowPreview::class)
    viewModelScope.launch {
      queryFlow.debounce(300).collectLatest { q ->
        if (q.isBlank()) {
          _ui.update { it.copy(suggestions = emptyList()) }
        } else {
          _ui.update { it.copy(suggestions = repo.suggest(q)) }
        }
      }
    }
  }

  fun onQueryChange(q: String) {
    _ui.update { it.copy(query = q) }
    queryFlow.value = q
  }

  fun submit(q: String = _ui.value.query) {
    val query = q.trim()
    if (query.isEmpty()) return
    viewModelScope.launch {
      _ui.update { it.copy(loading = true) }
      repo.saveQuery(query)
      val results = repo.search(query)
      _ui.update { it.copy(results = results, loading = false, suggestions = emptyList()) }
    }
  }

  fun deleteHistory(q: String) = viewModelScope.launch { repo.deleteQuery(q) }
  fun clearHistory() = viewModelScope.launch { repo.clearHistory() }
}
