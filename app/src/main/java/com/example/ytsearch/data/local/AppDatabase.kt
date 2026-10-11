package com.example.ytsearch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun historyDao(): HistoryDao

  companion object {
    @Volatile private var instance: AppDatabase? = null
    fun get(context: Context): AppDatabase =
      instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(context, AppDatabase::class.java, "ytsearch.db")
          .fallbackToDestructiveMigration()
          .build().also { instance = it }
      }
  }
}
