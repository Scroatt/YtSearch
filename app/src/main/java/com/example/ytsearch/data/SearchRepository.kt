package com.example.ytsearch.data

import com.example.ytsearch.data.local.HistoryDao
import com.example.ytsearch.data.local.HistoryEntity
import com.example.ytsearch.model.VideoResult
import kotlinx.coroutines.delay

class SearchRepository(private val historyDao: HistoryDao) {
  fun history() = historyDao.observe()

  suspend fun saveQuery(query: String) {
    if (query.isBlank()) return
    historyDao.insert(HistoryEntity(query.trim()))
  }

  suspend fun deleteQuery(query: String) = historyDao.delete(query)
  suspend fun clearHistory() = historyDao.clear()

  private val catalog = listOf(
    VideoResult("1", "Como funciona o algoritmo de busca", "Canal Tech", "12:34", "1,2 mi"),
    VideoResult("2", "Busca com debounce no Android", "Android Dev", "08:10", "340 mil"),
    VideoResult("3", "Jetpack Compose SearchBar na prática", "Compose Lab", "15:02", "98 mil"),
    VideoResult("4", "Room: histórico de pesquisa offline", "Mobile DB", "10:45", "210 mil"),
    VideoResult("5", "Retrofit + paginação de resultados", "API Guys", "18:20", "75 mil")
  )

  suspend fun suggest(query: String): List<String> {
    if (query.isBlank()) return emptyList()
    delay(150)
    val q = query.lowercase()
    return (catalog.map { it.title }.filter { it.lowercase().contains(q) } +
      listOf("$query tutorial", "$query ao vivo", "$query 2026")).distinct().take(8)
  }

  suspend fun search(query: String): List<VideoResult> {
    delay(400)
    if (query.isBlank()) return emptyList()
    val q = query.lowercase()
    val hits = catalog.filter {
      it.title.lowercase().contains(q) || it.channel.lowercase().contains(q)
    }
    return hits.ifEmpty { catalog }
  }
}
