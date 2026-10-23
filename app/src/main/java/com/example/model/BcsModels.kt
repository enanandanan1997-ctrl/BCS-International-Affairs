package com.example.model

/**
 * BCS International Affairs Core Data Models
 */

data class DailyReport(
    val id: String,
    val banglaDate: String,
    val banglaTime: String,
    val rawDateIso: String,
    val heading: String,
    val bulletinVideo: YouTubeSuggestion? = null,
    val topHeadlines: List<String> = emptyList(),
    val dayComparison: DayComparison = DayComparison(),
    val topics: List<TopicAnalysis> = emptyList(),
    val crossConnections: List<CrossConnection> = emptyList(),
    val bangladeshAngleSummary: String = "",
    val quickRevisions: List<QuickRevisionItem> = emptyList(),
    val mcqTest: List<McqQuestion> = emptyList(),
    val writtenVivaAngles: List<WrittenVivaItem> = emptyList(),
    val sources: List<DailySource> = emptyList()
)

data class YouTubeSuggestion(
    val title: String,
    val channelName: String,
    val duration: String,
    val videoUrl: String,
    val description: String = ""
)

data class DayComparison(
    val whatsNew: List<String> = emptyList(),
    val ongoingIssues: List<String> = emptyList(),
    val escalatedIssues: List<String> = emptyList()
)

data class TopicAnalysis(
    val id: String,
    val title: String,
    val category: String,
    val importanceRank: Int = 1,
    val todaysEvent: String,
    val whyImportant: String,
    val background: String,
    val importantEntities: ImportantEntities = ImportantEntities(),
    val importantFacts: List<FactItem> = emptyList(),
    val bangladeshRelevance: String = "",
    val bcsTakeaways: List<String> = emptyList(),
    val potentialQuestions: TopicQuestions = TopicQuestions(),
    val reportedBy: List<String> = emptyList(),
    val youtubeVideo: YouTubeSuggestion? = null
)

data class ImportantEntities(
    val persons: List<String> = emptyList(),
    val countries: List<String> = emptyList(),
    val organizations: List<String> = emptyList(),
    val alliances: List<String> = emptyList(),
    val institutions: List<String> = emptyList()
)

data class FactItem(
    val category: String,
    val title: String,
    val detail: String
)

data class TopicQuestions(
    val mcqs: List<McqQuestion> = emptyList(),
    val shortQuestions: List<String> = emptyList(),
    val analyticalQuestion: String = ""
)

data class McqQuestion(
    val id: String,
    val questionNumber: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val topicReference: String = ""
)

data class CrossConnection(
    val id: String,
    val title: String,
    val connectionChain: String,
    val geopoliticalAnalysis: String
)

data class QuickRevisionItem(
    val serial: Int,
    val questionOrFact: String,
    val answer: String,
    val category: String
)

data class WrittenVivaItem(
    val id: String,
    val issue: String,
    val examCategory: String,
    val possibleQuestion: String,
    val keyPoints: List<String>,
    val analyticalTip: String = ""
)

data class DailySource(
    val newspaperName: String,
    val englishName: String,
    val section: String,
    val articlesCovered: List<SourceArticle> = emptyList(),
    val hasContent: Boolean = true,
    val note: String = ""
)

data class SourceArticle(
    val headline: String,
    val topicMatched: String = "",
    val url: String = ""
)

data class QuizQuestion(
    val id: String,
    val topicTitle: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String
)

enum class ReadingTheme {
    LIGHT,
    DARK,
    SEPIA
}

enum class ReadingFontSize(val label: String, val bodySp: Float, val titleSp: Float) {
    SMALL("ছোট", 14f, 20f),
    MEDIUM("স্বাভাবিক", 17f, 23f),
    LARGE("বড়", 20f, 26f),
    EXTRA_LARGE("বৃহৎ", 23f, 29f)
}
