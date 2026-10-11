package com.example.ytsearch.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "search_history")
data class HistoryEntity(
  @PrimaryKey val query: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface HistoryDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(item: HistoryEntity)

  @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
  fun observe(): Flow<List<HistoryEntity>>

  @Query("DELETE FROM search_history WHERE `query` = :query")
  suspend fun delete(query: String)

  @Query("DELETE FROM search_history")
  suspend fun clear()
}
