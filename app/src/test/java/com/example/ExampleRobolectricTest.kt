package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BcsDataSource
import com.example.data.local.AppDatabase
import com.example.data.local.SavedNewsTopic
import com.example.data.local.SavedTopicEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BCS আন্তর্জাতিক", appName)
    }

    @Test
    fun `test saved topic entity conversion with robolectric`() {
        val firstTopic = BcsDataSource.report16Oct2026.topics.first()
        val entity = SavedTopicEntity.fromTopicAnalysis(firstTopic, "১৬ অক্টোবর ২০২৬")

        assertEquals(firstTopic.id, entity.id)
        assertEquals(firstTopic.title, entity.title)
        assertEquals(firstTopic.category, entity.category)
        assertEquals("১৬ অক্টোবর ২০২৬", entity.sourceDateLabel)

        val parsedTakeaways = entity.parseTakeaways()
        assertEquals(firstTopic.bcsTakeaways.size, parsedTakeaways.size)

        val parsedFacts = entity.parseFacts()
        assertEquals(firstTopic.importantFacts.size, parsedFacts.size)

        val parsedEntities = entity.parseEntities()
        assertEquals(firstTopic.importantEntities.persons.size, parsedEntities.persons.size)
    }

    @Test
    fun `test AppDatabase and SavedNewsDao operations`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.savedNewsDao()

        val topic = SavedNewsTopic(
            id = "news_01",
            title = "ব্রিকস শীর্ষ সম্মেলন ও ভারত-চীন সমঝোতা",
            content = "লাদাখ সীমান্তে টহল সমঝোতা ও বৈশ্বিক বহুমুখী বিশ্বব্যবস্থা।",
            date = "১৬ অক্টোবর ২০২৬",
            source = "প্রথম আলো & The Daily Star",
            isAvailableOffline = true
        )

        dao.insertSavedNewsTopic(topic)

        val retrieved = dao.getSavedNewsTopicById("news_01")
        assertNotNull(retrieved)
        assertEquals("news_01", retrieved?.id)
        assertEquals("ব্রিকস শীর্ষ সম্মেলন ও ভারত-চীন সমঝোতা", retrieved?.title)
        assertEquals(true, retrieved?.isAvailableOffline)

        db.close()
    }
}
