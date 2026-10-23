package com.example.model

data class TopicAnalysis(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val source: String,
    val summary: String,
    val event: String,
    val significance: String,
    val historicalBackground: String,
    val keyTermsAndData: List<String>,
    val bcsQuestions: List<String>,
    val keyPersonalities: List<String>,
    val dataCapsule: Map<String, String>,
    val examTips: String,
    val quizQuestions: List<QuizQuestion> = emptyList()
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
