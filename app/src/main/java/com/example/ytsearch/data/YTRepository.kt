package com.example.ytsearch.data

import com.example.ytsearch.model.VideoResult
import com.example.ytsearch.model.YTComment
import com.example.ytsearch.data.remote.YTApi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class YTRepository(private val key: String, private val local: SearchRepository) {
  val hasKey = key.isNotBlank()

  private val api: YTApi? = if (hasKey) Retrofit.Builder()
    .baseUrl("https://www.googleapis.com/youtube/v3/")
    .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
    .build().create(YTApi::class.java) else null

  @Suppress("UNCHECKED_CAST")
  private fun items(body: Map<String, Any?>): List<Map<String, Any?>> =
    (body["items"] as? List<*>)?.filterIsInstance<Map<String, Any?>>() ?: emptyList()

  @Suppress("UNCHECKED_CAST")
  private fun str(map: Map<String, Any?>, vararg path: String): String {
    var cur: Any? = map
    for (p in path) cur = (cur as? Map<String, Any?>)?.get(p) ?: return ""
    return cur?.toString() ?: ""
  }

  private fun num(map: Map<String, Any?>, vararg path: String): Long {
    var cur: Any? = map
    for (p in path) cur = (cur as? Map<String, Any?>)?.get(p)
    return when (cur) {
      is Number -> cur.toLong()
      is String -> cur.toLongOrNull() ?: 0L
      else -> 0L
    }
  }

  fun compact(n: Long): String = when {
    n >= 1_000_000 -> "${n / 1_000_000},${(n % 1_000_000) / 100_000} mi"
    n >= 1_000 -> "${n / 1_000} mil"
    else -> n.toString()
  }

  fun isoDuration(iso: String): String {
    val m = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?").matchEntire(iso) ?: return "--:--"
    val h = (m.groupValues[1].toIntOrNull() ?: 0)
    val mi = (m.groupValues[2].toIntOrNull() ?: 0)
    val s = (m.groupValues[3].toIntOrNull() ?: 0)
    return if (h > 0) "%d:%02d:%02d".format(h, mi, s) else "%d:%02d".format(mi, s)
  }

  private fun mapVideo(item: Map<String, Any?>, seed: Int): VideoResult {
    val id = (item["id"] as? String)
      ?: str(item, "id", "videoId").ifBlank { "v$seed" }
    val snippet = (item["snippet"] as? Map<String, Any?>) ?: emptyMap()
    val thumbs = (snippet["thumbnails"] as? Map<String, Any?>) ?: emptyMap()
    val thumb = str(thumbs, "medium", "url").ifBlank { str(thumbs, "high", "url") }
      .ifBlank { str(thumbs, "default", "url") }
    val stats = (item["statistics"] as? Map<String, Any?>) ?: emptyMap()
    return VideoResult(
      id = id,
      title = str(snippet, "title"),
      channel = str(snippet, "channelTitle"),
      duration = isoDuration(str(item, "contentDetails", "duration")),
      views = compact(num(stats, "viewCount")),
      likes = compact(num(stats, "likeCount")),
      description = str(snippet, "description").take(300),
      seed = seed,
      thumb = thumb,
      real = true
    )
  }

  suspend fun home(category: String): List<VideoResult> {
    val a = api ?: return local.home(category)
    return try {
      if (category == "Tudo") {
        items(a.popular(key)).mapIndexed { i, it -> mapVideo(it, i) }
      } else {
        items(a.search(key, category)).mapIndexed { i, it -> mapVideo(it, 100 + i) }
      }
    } catch (_: Exception) {
      local.home(category)
    }
  }

  suspend fun search(query: String): List<VideoResult> {
    val a = api ?: return local.search(query)
    return try {
      items(a.search(key, query)).mapIndexed { i, it -> mapVideo(it, 200 + i) }
    } catch (_: Exception) {
      local.search(query)
    }
  }

  suspend fun details(id: String): Pair<String, List<YTComment>> {
    val a = api ?: return "--:--" to emptyList()
    return try {
      val v = items(a.details(key, id)).firstOrNull()
      val dur = if (v == null) "--:--" else isoDuration(str(v, "contentDetails", "duration"))
      val comments = items(a.comments(key, id)).mapNotNull { item ->
        val s = ((item["snippet"] as? Map<String, Any?>)
          ?.get("topLevelComment") as? Map<String, Any?>)
          ?.get("snippet") as? Map<String, Any?>
          ?: return@mapNotNull null
        YTComment(
          author = s["authorDisplayName"]?.toString() ?: "?",
          text = (s["textDisplay"]?.toString() ?: "").take(280),
          likes = compact((s["likeCount"] as? Number)?.toLong() ?: 0L)
        )
      }
      dur to comments
    } catch (_: Exception) {
      "--:--" to emptyList()
    }
  }
}
