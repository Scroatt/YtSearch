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

  val categories = listOf("Tudo", "Tecnologia", "Android", "Música", "Jogos", "Culinária")

  private val catalog = listOf(
    VideoResult("1", "Como funciona a busca por voz no Android", "Lume Lab", "12:34", "1,2 mi", category = "Tecnologia", seed = 1),
    VideoResult("2", "Busca com debounce: teoria e prática", "Android Dev BR", "08:10", "340 mil", category = "Android", seed = 2),
    VideoResult("3", "Jetpack Compose do zero ao SearchBar", "Compose Lab", "15:02", "98 mil", category = "Android", seed = 3),
    VideoResult("4", "Room: histórico offline com Flow", "Mobile DB", "10:45", "210 mil", category = "Android", seed = 4),
    VideoResult("5", "Retrofit + paginação de resultados", "API Guys", "18:20", "75 mil", category = "Tecnologia", seed = 5),
    VideoResult("6", "Top 10 synthwave para programar", "Onda Neon", "1:02:10", "2,4 mi", category = "Música", seed = 6),
    VideoResult("7", "Speedrun comentada: fase final", "Pixel Arena", "24:56", "890 mil", category = "Jogos", seed = 7),
    VideoResult("8", "Risoto cremoso em 20 minutos", "Cozinha Prática", "11:11", "430 mil", category = "Culinária", seed = 8),
    VideoResult("9", "Navigation Compose sem mistério", "Android Dev BR", "14:33", "150 mil", category = "Android", seed = 9),
    VideoResult("10", "Chips de filtro igual aos grandes apps", "Lume Lab", "07:48", "66 mil", category = "Tecnologia", seed = 10),
    VideoResult("11", "Trilha sonora épica para focar", "Onda Neon", "58:00", "1,8 mi", category = "Música", seed = 11),
    VideoResult("12", "Bolo de cenoura fofinho", "Cozinha Prática", "09:27", "512 mil", category = "Culinária", seed = 12)
  )

  fun home(category: String): List<VideoResult> =
    if (category == "Tudo") catalog else catalog.filter { it.category == category }

  fun related(video: VideoResult): List<VideoResult> =
    (catalog.filter { it.category == video.category && it.id != video.id } +
      catalog.filter { it.category != video.category }).take(8)

  fun byId(id: String): VideoResult = catalog.firstOrNull { it.id == id } ?: catalog.first()

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
