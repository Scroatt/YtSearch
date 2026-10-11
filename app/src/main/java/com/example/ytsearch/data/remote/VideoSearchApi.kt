package com.example.ytsearch.data.remote

import com.example.ytsearch.model.VideoResult
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoSearchApi {
  @GET("search")
  suspend fun search(@Query("q") query: String): List<VideoResult>

  @GET("suggest")
  suspend fun suggest(@Query("q") query: String): List<String>
}
