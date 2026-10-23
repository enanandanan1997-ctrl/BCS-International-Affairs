package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity to allow users to save important BCS news topics
 * for later offline reading.
 */
@Entity(tableName = "saved_news_topics")
data class SavedNewsTopic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: String,
    val source: String,
    val offlineReading: Boolean = true
)
