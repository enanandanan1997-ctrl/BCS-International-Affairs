package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "quiz_score_records")
data class QuizScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topicTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface QuizScoreDao {
    @Query("SELECT * FROM quiz_score_records ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<QuizScoreRecord>>

    @Insert
    suspend fun insertScore(record: QuizScoreRecord): Long

    @Query("SELECT COUNT(*) FROM quiz_score_records")
    fun getTotalQuizzesTaken(): Flow<Int>

    @Query("SELECT AVG(percentage) FROM quiz_score_records")
    fun getAveragePercentage(): Flow<Double?>
}
