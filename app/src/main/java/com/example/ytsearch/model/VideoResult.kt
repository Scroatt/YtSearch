package com.example.ytsearch.model

data class YTComment(
  val author: String,
  val text: String,
  val likes: String
)

data class VideoResult(
  val id: String,
  val title: String,
  val channel: String,
  val duration: String,
  val views: String,
  val likes: String = "12 mil",
  val subs: String = "1,1 mi",
  val category: String = "Geral",
  val description: String = "Vídeo de demonstração do MVP Lume Play.",
  val seed: Int = 0,
  val thumb: String = "",
  val real: Boolean = false
)
