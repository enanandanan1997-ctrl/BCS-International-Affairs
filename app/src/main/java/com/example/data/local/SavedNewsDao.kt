package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNewsDao {
    @Query("SELECT * FROM saved_news_topics ORDER BY id DESC")
    fun getAllSavedNews(): Flow<List<SavedNewsTopic>>

    @Query("SELECT * FROM saved_news_topics WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: Long): SavedNewsTopic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: SavedNewsTopic): Long

    @Delete
    suspend fun deleteTopic(topic: SavedNewsTopic)

    @Query("DELETE FROM saved_news_topics WHERE title = :title")
    suspend fun deleteByTitle(title: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news_topics WHERE title = :title)")
    fun isTopicSavedFlow(title: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_news_topics WHERE title = :title)")
    suspend fun isTopicSaved(title: String): Boolean
}
