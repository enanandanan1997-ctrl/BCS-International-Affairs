package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BcsDataSource
import com.example.data.local.BcsDatabase
import com.example.data.local.SavedTopicEntity
import com.example.data.time.BangladeshTimeHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        assertEquals("BCS Daily Digest", appName)
    }

    @Test
    fun `test bangladesh time helper and dynamic report`() {
        val dateStr = BangladeshTimeHelper.getFormattedBstDate()
        assertTrue(dateStr.isNotBlank())

        val timeStr = BangladeshTimeHelper.getFormattedBstTime()
        assertTrue(timeStr.contains("BST"))

        val report = BcsDataSource.generateDynamicTodayReport()
        assertNotNull(report)
        assertEquals(dateStr, report.banglaDate)
        assertEquals(4, report.sources.size)
    }

    @Test
    fun `test saved topic entity conversion and room operations`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, BcsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.bcsDao()

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

        dao.insertSavedTopic(entity)
        val isSaved = dao.isTopicSaved(firstTopic.id)
        assertTrue(isSaved)

        db.close()
    }
}
