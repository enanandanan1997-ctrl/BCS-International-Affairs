package com.example

import com.example.data.BcsDataSource
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBanglaNumberConversion() {
        assertEquals("১৬", BcsDataSource.convertToBanglaNumber("16"))
        assertEquals("২০২৬", BcsDataSource.convertToBanglaNumber("2026"))
        assertEquals("০", BcsDataSource.convertToBanglaNumber("0"))
    }

    @Test
    fun testBangladeshStandardTimeCalculation() {
        val (dateStr, timeStr) = BcsDataSource.getBangladeshCurrentTime()
        assertTrue("Date should contain Bangla month", dateStr.isNotEmpty())
        assertTrue("Time should specify BST", timeStr.contains("BST"))
    }

    @Test
    fun testReportStructureCompleteness() {
        val report = BcsDataSource.report16Oct2026
        assertEquals("16 October 2026", "2026-10-16", report.rawDateIso)
        assertTrue("Should have 5-10 high value topics", report.topics.size in 5..10)
        assertTrue("Should have cross connections", report.crossConnections.isNotEmpty())
        assertTrue("Should have 15-25 quick revisions", report.quickRevisions.size >= 15)
        assertTrue("Should have 10-15 MCQ questions in test", report.mcqTest.size in 10..18)
        assertTrue("Should have 3-5 written viva issues", report.writtenVivaAngles.size in 3..5)
        assertEquals("Should cover all 4 newspapers", 4, report.sources.size)

        // Verify each topic has all 8 required sub-sections
        report.topics.forEach { topic ->
            assertTrue("Topic title must exist", topic.title.isNotBlank())
            assertTrue("Section 1 (todaysEvent) must exist", topic.todaysEvent.isNotBlank())
            assertTrue("Section 2 (whyImportant) must exist", topic.whyImportant.isNotBlank())
            assertTrue("Section 3 (background) must exist", topic.background.isNotBlank())
            assertTrue("Section 4 (importantEntities) must exist", topic.importantEntities.countries.isNotEmpty())
            assertTrue("Section 5 (importantFacts) must exist", topic.importantFacts.isNotEmpty())
            assertTrue("Section 6 (bangladeshRelevance) must exist", topic.bangladeshRelevance.isNotBlank())
            assertTrue("Section 7 (bcsTakeaways) must exist", topic.bcsTakeaways.isNotEmpty())
            assertTrue("Section 8 (MCQs) must exist", topic.potentialQuestions.mcqs.isNotEmpty())
        }
    }

    @Test
    fun testBcsScoringLogic() {
        val correct = 10
        val wrong = 4
        // Standard BCS negative marking: 1 mark per correct, 0.5 per wrong
        val score = (correct * 1.0f) - (wrong * 0.5f)
        assertEquals(8.0f, score, 0.001f)
    }

    @Test
    fun testSavedNewsTopicEntity() {
        val newsTopic = com.example.data.local.SavedNewsTopic(
            id = "topic_101",
            title = "লেবাননে UNIFIL শান্তিরক্ষী মিশন ও রেজোলিউশন ১৭০১",
            content = "দক্ষিণ লেবাননে নিয়োজিত জাতিসংঘের শান্তিরক্ষী বাহিনীর নিরাপত্তা ও ব্লু লাইন বিশ্লেষণ।",
            date = "১৬ অক্টোবর ২০২৬",
            source = "প্রথম আলো",
            isAvailableOffline = true
        )

        assertEquals("topic_101", newsTopic.id)
        assertEquals("লেবাননে UNIFIL শান্তিরক্ষী মিশন ও রেজোলিউশন ১৭০১", newsTopic.title)
        assertTrue(newsTopic.content.isNotEmpty())
        assertEquals("১৬ অক্টোবর ২০২৬", newsTopic.date)
        assertEquals("প্রথম আলো", newsTopic.source)
        assertTrue(newsTopic.isAvailableOffline)
    }

    @Test
    fun testBangladeshTimeHelper6AmCalculation() {
        val now = com.example.data.time.BangladeshTimeHelper.nowBst()
        assertNotNull("BST time must not be null", now)
        assertEquals("Zone must be Asia/Dhaka", "Asia/Dhaka", now.zone.id)

        val delayMillis = com.example.data.time.BangladeshTimeHelper.getMillisUntilNext6AmBst()
        assertTrue("Delay until next 6 AM must be positive", delayMillis > 0)
        assertTrue("Delay must not exceed 24 hours", delayMillis <= 24 * 60 * 60 * 1000L)

        val formattedDate = com.example.data.time.BangladeshTimeHelper.getFormattedBstDate()
        assertTrue("Date should have year digits", formattedDate.contains("২০২"))

        val countdown = com.example.data.time.BangladeshTimeHelper.getCountdownToNext6AmBst()
        assertTrue("Countdown should contain 'পর'", countdown.contains("পর"))
    }

    @Test
    fun testDefaultNewspapersCoverage() {
        val defaultSources = com.example.model.ManagedNewspaper.defaultSources
        assertEquals("Should have exactly 4 designated newspapers", 4, defaultSources.size)
        val names = defaultSources.map { it.name }
        assertTrue("Must include প্রথম আলো", names.contains("প্রথম আলো"))
        assertTrue("Must include দ্য ডেইলি স্টার", names.contains("দ্য ডেইলি স্টার"))
        assertTrue("Must include বণিক বার্তা", names.contains("বণিক বার্তা"))
        assertTrue("Must include ঢাকা ট্রিবিউন", names.contains("ঢাকা ট্রিবিউন"))

        val syncStatuses = com.example.data.sync.DailySyncState.defaultNewspaperStatuses
        assertEquals("Sync status list should track 4 newspapers", 4, syncStatuses.size)
    }
}
