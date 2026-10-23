package com.example.data.repository

import com.example.data.local.BcsDao
import com.example.data.local.SavedTopicEntity
import kotlinx.coroutines.flow.Flow

class SavedTopicRepository(private val dao: BcsDao) {

    val allSavedTopics: Flow<List<SavedTopicEntity>> = dao.getAllSavedTopics()

    fun isTopicSavedFlow(id: String): Flow<Boolean> = dao.isTopicSavedFlow(id)

    suspend fun saveTopic(topic: SavedTopicEntity) {
        dao.insertSavedTopic(topic)
    }

    suspend fun removeTopic(id: String) {
        dao.deleteSavedTopic(id)
    }

    suspend fun isTopicSaved(id: String): Boolean {
        return dao.isTopicSaved(id)
    }

    suspend fun updateNote(id: String, note: String) {
        dao.updateUserNote(id, note)
    }
}
