package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.McqQuestion
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.BcsViewModel
import com.example.ui.viewmodel.ExamMode

@Composable
fun DailyExamScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.currentReport.collectAsState()
    val examMode by viewModel.examMode.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val isSubmitted by viewModel.isExamSubmitted.collectAsState()
    val scoreSummary by viewModel.scoreSummary.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()

    val testQuestions = report.mcqTest

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_exam_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "📝 আজকের MCQ পরীক্ষা",
                subtitle = "শুধুমাত্র আজকের আলোচিত বিষয়ের ওপর ভিত্তি করে সংকলিত ${testQuestions.size}টি আদর্শ প্রশ্ন",
                icon = Icons.Default.Quiz
            )
        }

        // Mode Selector & Control Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "মোড নির্বাচন করুন:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = examMode == ExamMode.REAL_EXAM,
                                onClick = { viewModel.setExamMode(ExamMode.REAL_EXAM) },
                                label = { Text("পরীক্ষা মোড", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = if (examMode == ExamMode.REAL_EXAM) {
                                    { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                            FilterChip(
                                selected = examMode == ExamMode.PRACTICE,
                                onClick = { viewModel.setExamMode(ExamMode.PRACTICE) },
                                label = { Text("অনুশীলন মোড", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = if (examMode == ExamMode.PRACTICE) {
                                    { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (examMode == ExamMode.REAL_EXAM) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⏱️ BCS স্ট্যান্ডার্ড: প্রতিটি সঠিক উত্তরে ১ নম্বর, প্রতিটি ভুল উত্তরে ০.৫ নম্বর কাটা যাবে। সমস্ত প্রশ্নের উত্তর দিয়ে 'ফলাফল জমা দিন' বাটনে চাপুন।",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 অনুশীলন মোড: প্রতিটি অপশনে ক্লিক করার সাথে সাথেই তাৎক্ষণিক সঠিক উত্তর এবং ব্যাখ্যা প্রদর্শিত হবে।",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Score Card if Exam Submitted
        if (isSubmitted && scoreSummary != null) {
            item {
                val score = scoreSummary!!
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯 পরীক্ষার ফলাফল ও স্কোরকার্ড",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = String.format("%.2f / %d", score.calculatedScore, score.totalQuestions),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "প্রাপ্ত মোট নম্বর (নেগেটিভ মার্কিং সহ)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScoreMetric(label = "সঠিক", value = "${score.correctCount}", color = Color(0xFF2E7D32))
                            ScoreMetric(label = "ভুল (-০.৫)", value = "${score.wrongCount}", color = Color(0xFFC62828))
                            ScoreMetric(label = "উত্তর দেননি", value = "${score.skippedCount}", color = Color(0xFFEF6C00))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { viewModel.resetExam() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("পুনরায় পরীক্ষা দিন (Retake Test)")
                        }
                    }
                }
            }

            // Answer Key Summary (Prompt section 8: Answer Key: 1. B, 2. C, 3. A...)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Answer Key (একনজরে উত্তরমালা)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val chunked = testQuestions.chunked(5)
                        chunked.forEach { rowList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowList.forEach { q ->
                                    val optLetter = when (q.correctIndex) {
                                        0 -> "A"
                                        1 -> "B"
                                        2 -> "C"
                                        else -> "D"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = "${q.questionNumber}. $optLetter",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Questions List
        items(testQuestions, key = { it.id }) { question ->
            val selectedOption = userAnswers[question.questionNumber]
            val isBookmarked = bookmarkedIds.contains(question.id)

            // Look up if any topic with a verified YouTube video relates to this question
            val matchingVideo = remember(question, report.topics) {
                val directTopic = report.topics.firstOrNull { topic ->
                    topic.potentialQuestions.mcqs.any { it.id == question.id || it.question == question.question }
                }
                if (directTopic?.youtubeVideo != null) {
                    directTopic.youtubeVideo
                } else {
                    report.topics.firstOrNull { topic ->
                        val v = topic.youtubeVideo
                        if (v != null) {
                            val text = "${question.question} ${question.explanation}"
                            when (topic.id) {
                                "topic_01" -> text.contains("UNIFIL") || text.contains("লেবানন") || text.contains("১৭০১") || text.contains("ব্লু লাইন")
                                "topic_02" -> text.contains("ব্রিকস") || text.contains("BRICS") || text.contains("LAC") || text.contains("লাদাখ") || text.contains("টহল")
                                "topic_03" -> text.contains("IMF") || text.contains("আইএমএফ") || text.contains("SDR") || text.contains("ব্রেটন")
                                "topic_05" -> text.contains("বাব-এল-মান্দেব") || text.contains("সুয়েজ") || text.contains("লোহিত সাগর") || text.contains("হরমুজ")
                                else -> false
                            }
                        } else false
                    }?.youtubeVideo
                }
            }

            ExamQuestionCard(
                question = question,
                selectedOption = selectedOption,
                isSubmitted = isSubmitted,
                examMode = examMode,
                onSelectOption = { optIdx ->
                    viewModel.selectAnswer(question.questionNumber, optIdx)
                },
                onBookmark = { viewModel.toggleBookmark(question) },
                isBookmarked = isBookmarked,
                youtubeVideo = matchingVideo
            )
        }

        // Submit Button (only in Real Exam mode when not yet submitted)
        if (examMode == ExamMode.REAL_EXAM && !isSubmitted) {
            item {
                val answeredCount = userAnswers.size
                Button(
                    onClick = { viewModel.submitExam() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_exam_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ফলাফল জমা দিন ($answeredCount/${testQuestions.size} উত্তর দেওয়া হয়েছে)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun ExamQuestionCard(
    question: McqQuestion,
    selectedOption: Int?,
    isSubmitted: Boolean,
    examMode: ExamMode,
    onSelectOption: (Int) -> Unit,
    onBookmark: () -> Unit,
    isBookmarked: Boolean,
    youtubeVideo: com.example.model.YouTubeSuggestion? = null
) {
    val revealAnswer = isSubmitted || (examMode == ExamMode.PRACTICE && selectedOption != null)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_question_${question.questionNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Question header & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${question.questionNumber}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }

                IconButton(
                    onClick = onBookmark,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "বুকমার্ক",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Options (A, B, C, D)
            question.options.forEachIndexed { optIndex, optionText ->
                val isSelected = selectedOption == optIndex
                val isCorrect = optIndex == question.correctIndex

                val optionBorderColor = when {
                    revealAnswer && isCorrect -> Color(0xFF2E7D32)
                    revealAnswer && isSelected && !isCorrect -> Color(0xFFC62828)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }

                val optionBgColor = when {
                    revealAnswer && isCorrect -> Color(0xFF2E7D32).copy(alpha = 0.12f)
                    revealAnswer && isSelected && !isCorrect -> Color(0xFFC62828).copy(alpha = 0.12f)
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }

                val optionLetter = when (optIndex) {
                    0 -> "A"
                    1 -> "B"
                    2 -> "C"
                    else -> "D"
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !isSubmitted || examMode == ExamMode.PRACTICE) {
                            onSelectOption(optIndex)
                        }
                        .border(1.dp, optionBorderColor, RoundedCornerShape(10.dp)),
                    color = optionBgColor
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                revealAnswer && isCorrect -> Color(0xFF2E7D32)
                                revealAnswer && isSelected && !isCorrect -> Color(0xFFC62828)
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = optionLetter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || (revealAnswer && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (revealAnswer && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (revealAnswer && isCorrect) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "সঠিক",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (revealAnswer && isSelected && !isCorrect) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "ভুল",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Explanation Section (revealed after submission or in practice mode)
            if (revealAnswer) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 ব্যাখ্যা:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // YouTube video suggestion button for easier understanding of the topic and question
            if (youtubeVideo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val context = androidx.compose.ui.platform.LocalContext.current
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(youtubeVideo.videoUrl)
                            ).apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                            context.startActivity(intent)
                        }
                        .border(1.dp, Color(0xFFFF0000).copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
                    color = Color(0xFFFF0000).copy(alpha = 0.05f)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF0000),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "YouTube",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ভিডিও টিউটোরিয়াল: প্রশ্নের বিষয়বস্তু সহজে বুঝুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCC0000)
                            )
                            Text(
                                text = "${youtubeVideo.channelName} • ${youtubeVideo.title} (${youtubeVideo.duration})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "ভিডিও দেখুন",
                            tint = Color(0xFFCC0000),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
