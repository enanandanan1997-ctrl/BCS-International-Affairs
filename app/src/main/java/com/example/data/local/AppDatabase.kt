package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase class that extends RoomDatabase to register the 'SavedNewsTopic'
 * entity and provide access to the 'SavedNewsDao' and 'QuizScoreDao'.
 */
@Database(
    entities = [SavedNewsTopic::class, QuizScoreRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedNewsDao(): SavedNewsDao
    abstract fun quizScoreDao(): QuizScoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bcs_daily_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
