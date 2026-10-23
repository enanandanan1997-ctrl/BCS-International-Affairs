package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmarked_mcqs")
data class BookmarkedMcq(
    @PrimaryKey val id: String,
    val questionNumber: Int,
    val question: String,
    val optionsJson: String,
    val correctIndex: Int,
    val explanation: String,
    val topicReference: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Long = 0,
    val reportId: String,
    val dateLabel: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val skippedCount: Int,
    val calculatedScore: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface BcsDao {
    @Query("SELECT * FROM bookmarked_mcqs ORDER BY savedAtTimestamp DESC")
    suspend fun getAllBookmarks(): List<BookmarkedMcq>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(mcq: BookmarkedMcq)

    @Query("DELETE FROM bookmarked_mcqs WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_mcqs WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    suspend fun getAllQuizAttempts(): List<QuizAttempt>

    @Insert
    suspend fun insertQuizAttempt(attempt: QuizAttempt): Long

    // Saved Topics for Offline Reading
    @Query("SELECT * FROM saved_topics ORDER BY savedAtTimestamp DESC")
    fun getAllSavedTopics(): Flow<List<SavedTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedTopic(topic: SavedTopicEntity)

    @Query("DELETE FROM saved_topics WHERE id = :id")
    suspend fun deleteSavedTopic(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_topics WHERE id = :id)")
    fun isTopicSavedFlow(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_topics WHERE id = :id)")
    suspend fun isTopicSaved(id: String): Boolean

    @Query("UPDATE saved_topics SET userNote = :note WHERE id = :id")
    suspend fun updateUserNote(id: String, note: String)
}

@Database(
    entities = [BookmarkedMcq::class, QuizAttempt::class, SavedTopicEntity::class],
    version = 4,
    exportSchema = false
)
abstract class BcsDatabase : RoomDatabase() {
    abstract fun bcsDao(): BcsDao

    companion object {
        @Volatile
        private var INSTANCE: BcsDatabase? = null

        fun getDatabase(context: android.content.Context): BcsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BcsDatabase::class.java,
                    "bcs_international.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
