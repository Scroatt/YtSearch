package com.example.ytsearch.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface YTApi {
  @GET("videos")
  suspend fun popular(
    @Query("key") key: String,
    @Query("chart") chart: String = "mostPopular",
    @Query("regionCode") region: String = "BR",
    @Query("part") part: String = "snippet,statistics,contentDetails",
    @Query("maxResults") max: Int = 15
  ): Map<String, Any?>

  @GET("search")
  suspend fun search(
    @Query("key") key: String,
    @Query("q") q: String,
    @Query("part") part: String = "snippet",
    @Query("type") type: String = "video",
    @Query("maxResults") max: Int = 15
  ): Map<String, Any?>

  @GET("videos")
  suspend fun details(
    @Query("key") key: String,
    @Query("id") id: String,
    @Query("part") part: String = "snippet,statistics,contentDetails"
  ): Map<String, Any?>

  @GET("commentThreads")
  suspend fun comments(
    @Query("key") key: String,
    @Query("videoId") videoId: String,
    @Query("part") part: String = "snippet",
    @Query("maxResults") max: Int = 10,
    @Query("textFormat") format: String = "plainText"
  ): Map<String, Any?>
}
