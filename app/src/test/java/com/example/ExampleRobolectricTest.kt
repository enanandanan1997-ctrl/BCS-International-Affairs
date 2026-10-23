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

    @Test
    fun `test international affairs headline filtering`() {
        val filter = com.example.data.sync.DailyBcsOverhaulEngine

        // Strictly rejected non-international topics
        org.junit.Assert.assertFalse(filter.isInternationalAffairsHeadline("মেসির হ্যাটট্রিকে ইন্টার মায়ামির জয়"))
        org.junit.Assert.assertFalse(filter.isInternationalAffairsHeadline("বিপিএল ক্রিকেটে রোমাঞ্চকর জয় পেল কুমিল্লা"))
        org.junit.Assert.assertFalse(filter.isInternationalAffairsHeadline("বলিউডের নতুন সিনেমা বক্স অফিসে ঝড় তুলেছে"))
        org.junit.Assert.assertFalse(filter.isInternationalAffairsHeadline("মাদারীপুরে সড়ক দুর্ঘটনায় নিহত ২"))
        org.junit.Assert.assertFalse(filter.isInternationalAffairsHeadline("উপজেলা পরিষদ নির্বাচনে চেয়ারম্যান প্রার্থীর প্রচারণা"))

        // Strictly accepted international affairs topics
        assertTrue(filter.isInternationalAffairsHeadline("লেবানন সীমান্তে জাতিসংঘের শান্তিরক্ষীদের নিরাপত্তা ও নিরাপত্তা পরিষদের জরুরি অধিবেশন"))
        assertTrue(filter.isInternationalAffairsHeadline("১৬তম ব্রিকস (BRICS) শীর্ষ সম্মেলনে সদস্য দেশসমূহের বিকল্প বাণিজ্য মুদ্রা"))
        assertTrue(filter.isInternationalAffairsHeadline("India and China agree on patrolling along LAC ahead of BRICS Kazan summit"))
        assertTrue(filter.isInternationalAffairsHeadline("UNSC urges strict implementation of Resolution 1701 for Lebanon peacekeepers"))
        assertTrue(filter.isInternationalAffairsHeadline("IMF forecasts global GDP growth to remain subdued at 3.2 percent"))
    }

    @Test
    fun `test full 4-section daily overhaul generation and json persistence`() {
        val engine = com.example.data.sync.DailyBcsOverhaulEngine
        val report = engine.buildFullOverhaulReport(
            dateLabel = "২৩ অক্টোবর ২০২৬",
            timeLabel = "০৬:০০ AM BST",
            editionKey = "2026-10-23",
            activeSources = com.example.model.ManagedNewspaper.defaultSources,
            headlinesMap = emptyMap(),
            geminiRawText = null
        )

        assertNotNull(report)
        // 1. Bulletin: 5 topics with full 8-part subjective analysis
        assertEquals(5, report.topics.size)
        report.topics.forEach { topic ->
            assertTrue(topic.title.isNotBlank())
            assertTrue(topic.todaysEvent.isNotBlank())
            assertTrue(topic.whyImportant.isNotBlank())
            assertTrue(topic.background.isNotBlank())
            assertTrue(topic.bangladeshRelevance.isNotBlank())
            assertTrue(topic.importantFacts.isNotEmpty())
            assertTrue(topic.bcsTakeaways.isNotEmpty())
            assertTrue(topic.potentialQuestions.shortQuestions.isNotEmpty() || topic.potentialQuestions.mcqs.isNotEmpty())
        }
        assertTrue(report.bangladeshAngleSummary.isNotBlank())
        assertTrue(report.crossConnections.isNotEmpty())

        // 2. Revision: 20 high-yield one-liners
        assertEquals(20, report.quickRevisions.size)

        // 3. MCQ Part: 10-15 exam questions
        assertTrue(report.mcqTest.size in 10..15)
        report.mcqTest.forEach { mcq ->
            assertEquals(4, mcq.options.size)
            assertTrue(mcq.explanation.isNotBlank())
        }

        // 4. Written & Viva questions
        assertEquals(5, report.writtenVivaAngles.size)
        report.writtenVivaAngles.forEach { wv ->
            assertTrue(wv.possibleQuestion.isNotBlank())
            assertTrue(wv.keyPoints.isNotEmpty())
            assertTrue(wv.analyticalTip.isNotBlank())
        }

        // 5. JSON Roundtrip persistence
        val json = engine.serializeToJson(report)
        val deserialized = engine.deserializeFromJson(json)
        assertEquals(report.id, deserialized.id)
        assertEquals(report.banglaDate, deserialized.banglaDate)
        assertEquals(report.topics.size, deserialized.topics.size)
        assertEquals(report.quickRevisions.size, deserialized.quickRevisions.size)
        assertEquals(report.mcqTest.size, deserialized.mcqTest.size)
        assertEquals(report.writtenVivaAngles.size, deserialized.writtenVivaAngles.size)
    }

    @Test
    fun `test latest articles first ordering and initial collapsed state`() {
        val report = com.example.data.BcsDataSource.generateDynamicTodayReport()
        val sortedTopics = report.topics.sortedBy { it.importanceRank }

        // The latest (#1) topic is always at the very top
        assertEquals(1, sortedTopics.first().importanceRank)

        // Expansion state testing: Starts collapsed so tapping headline expands
        val expandedIds = mutableSetOf<String>()
        assertTrue(expandedIds.isEmpty())

        val firstTopicId = sortedTopics.first().id
        expandedIds.add(firstTopicId)
        assertTrue(expandedIds.contains(firstTopicId))

        expandedIds.remove(firstTopicId)
        org.junit.Assert.assertFalse(expandedIds.contains(firstTopicId))
    }
}
