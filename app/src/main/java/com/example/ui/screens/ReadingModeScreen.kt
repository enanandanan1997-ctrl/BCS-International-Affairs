package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ReadingFontSize
import com.example.model.ReadingTheme
import com.example.model.TopicAnalysis
import com.example.ui.viewmodel.BcsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingModeScreen(
    topic: TopicAnalysis,
    viewModel: BcsViewModel,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val readingTheme by viewModel.readingTheme.collectAsState()
    val fontSize by viewModel.readingFontSize.collectAsState()
    val isRelaxedSpacing by viewModel.isRelaxedSpacing.collectAsState()
    val savedTopics by viewModel.savedTopics.collectAsState()
    val isSaved = savedTopics.any { it.title == topic.title }

    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Color definitions for Distraction-Free themes
    val (bgColor, textColor, cardBgColor, accentColor) = when (readingTheme) {
        ReadingTheme.LIGHT -> QuadColors(
            bg = Color(0xFFFBFBFE),
            text = Color(0xFF1A1C1E),
            card = Color(0xFFF0F4F8),
            accent = Color(0xFF0F3A66)
        )
        ReadingTheme.DARK -> QuadColors(
            bg = Color(0xFF121417),
            text = Color(0xFFE2E2E6),
            card = Color(0xFF1E2125),
            accent = Color(0xFF82B1FF)
        )
        ReadingTheme.SEPIA -> QuadColors(
            bg = Color(0xFFF5EFEB),
            text = Color(0xFF3E2723),
            card = Color(0xFFEBE1DA),
            accent = Color(0xFF8D4925)
        )
    }

    val bodyLineHeight = if (isRelaxedSpacing) (fontSize.bodySp * 1.6f).sp else (fontSize.bodySp * 1.35f).sp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Distraction-free Top Bar
            TopAppBar(
                title = {
                    Text(
                        text = "পঠন মোড",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.closeReadingMode() },
                        modifier = Modifier.testTag("reading_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Theme Quick Cycle Button
                    IconButton(
                        onClick = {
                            val nextTheme = when (readingTheme) {
                                ReadingTheme.LIGHT -> ReadingTheme.SEPIA
                                ReadingTheme.SEPIA -> ReadingTheme.DARK
                                ReadingTheme.DARK -> ReadingTheme.LIGHT
                            }
                            viewModel.setReadingTheme(nextTheme)
                        },
                        modifier = Modifier.testTag("reading_theme_toggle")
                    ) {
                        Icon(
                            imageVector = when (readingTheme) {
                                ReadingTheme.LIGHT -> Icons.Default.WbSunny
                                ReadingTheme.SEPIA -> Icons.Default.LightMode
                                ReadingTheme.DARK -> Icons.Default.DarkMode
                            },
                            contentDescription = "Theme",
                            tint = accentColor
                        )
                    }

                    // Font Size Options Sheet
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("reading_font_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Font Size",
                            tint = accentColor
                        )
                    }

                    // Save Bookmark to Room DB
                    IconButton(
                        onClick = { viewModel.toggleSaveTopic(topic) },
                        modifier = Modifier.testTag("reading_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Offline",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )

            // Jump to section quick nav
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sections = listOf(
                    "১. মূল ঘটনা" to 1,
                    "২. গুরুত্ব" to 2,
                    "৩. প্রেক্ষাপট" to 3,
                    "৪. পরিভাষা" to 4,
                    "৫. বিসিএস প্রশ্ন" to 5,
                    "৬. ব্যক্তিত্ব" to 6,
                    "৭. ক্যাপসুল" to 7,
                    "৮. পরামর্শ" to 8
                )
                items(sections.size) { idx ->
                    val (label, targetIndex) = sections[idx]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardBgColor,
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                listState.animateScrollToItem(targetIndex)
                            }
                        }
                    ) {
                        Text(
                            text = label,
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = textColor.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Content List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp)
            ) {
                // Header item: Title, Source & Date
                item {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = topic.category,
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = topic.title,
                            fontSize = (fontSize.titleSp + 4).sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            lineHeight = (fontSize.titleSp * 1.3f).sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "তারিখ: ${topic.date}",
                                fontSize = 13.sp,
                                color = textColor.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "সূত্র: ${topic.source}",
                                fontSize = 13.sp,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }

                        if (topic.quizQuestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    viewModel.startQuizForTopic(topic)
                                    viewModel.closeReadingMode()
                                    onNavigateToQuiz()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reading_start_quiz_button")
                            ) {
                                Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("এই বিষয়ের ওপর কুইজ দিন (${topic.quizQuestions.size} টি প্রশ্ন)")
                            }
                        }
                    }
                }

                // Section 1: আজকের মূল ঘটনা
                item {
                    ReadingSectionBlock(
                        sectionNumber = "১",
                        sectionTitle = "আজকের মূল ঘটনা (Today's Key Event)",
                        content = topic.event,
                        fontSize = fontSize.bodySp,
                        lineHeight = bodyLineHeight,
                        textColor = textColor,
                        cardColor = cardBgColor,
                        accentColor = accentColor
                    )
                }

                // Section 2: জাতীয় ও আন্তর্জাতিক গুরুত্ব
                item {
                    ReadingSectionBlock(
                        sectionNumber = "২",
                        sectionTitle = "জাতীয় ও আন্তর্জাতিক গুরুত্ব (Significance)",
                        content = topic.significance,
                        fontSize = fontSize.bodySp,
                        lineHeight = bodyLineHeight,
                        textColor = textColor,
                        cardColor = cardBgColor,
                        accentColor = accentColor
                    )
                }

                // Section 3: ঐতিহাসিক প্রেক্ষাপট
                item {
                    ReadingSectionBlock(
                        sectionNumber = "৩",
                        sectionTitle = "ঐতিহাসিক প্রেক্ষাপট (Historical Background)",
                        content = topic.historicalBackground,
                        fontSize = fontSize.bodySp,
                        lineHeight = bodyLineHeight,
                        textColor = textColor,
                        cardColor = cardBgColor,
                        accentColor = accentColor
                    )
                }

                // Section 4: গুরুত্বপূর্ণ পরিভাষা ও উপাত্ত
                item {
                    Column {
                        SectionHeader(
                            number = "৪",
                            title = "গুরুত্বপূর্ণ পরিভাষা ও উপাত্ত (Key Facts)",
                            accentColor = accentColor,
                            textColor = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        topic.keyTermsAndData.forEach { term ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(accentColor)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = term,
                                        fontSize = fontSize.bodySp.sp,
                                        lineHeight = bodyLineHeight,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 5: বিসিএস প্রিলিমিনারি ও লিখিত সম্ভাব্য প্রশ্ন
                item {
                    Column {
                        SectionHeader(
                            number = "৫",
                            title = "বিসিএস প্রিলিমিনারি ও লিখিত প্রশ্ন (BCS Questions)",
                            accentColor = accentColor,
                            textColor = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        topic.bcsQuestions.forEachIndexed { qIdx, question ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "প্রশ্ন ${qIdx + 1}:",
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        fontSize = (fontSize.bodySp - 1).sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question,
                                        fontSize = fontSize.bodySp.sp,
                                        lineHeight = bodyLineHeight,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 6: প্রাসঙ্গিক ব্যক্তিত্ব ও সংস্থা
                item {
                    Column {
                        SectionHeader(
                            number = "৬",
                            title = "প্রাসঙ্গিক ব্যক্তিত্ব ও সংস্থা (Key Figures)",
                            accentColor = accentColor,
                            textColor = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        topic.keyPersonalities.forEach { pers ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "★",
                                    color = accentColor,
                                    fontSize = fontSize.bodySp.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pers,
                                    fontSize = fontSize.bodySp.sp,
                                    color = textColor,
                                    lineHeight = bodyLineHeight
                                )
                            }
                        }
                    }
                }

                // Section 7: দ্রুত রিভিশন ক্যাপসুল
                item {
                    Column {
                        SectionHeader(
                            number = "৭",
                            title = "দ্রুত রিভিশন ক্যাপসুল (Data Capsule)",
                            accentColor = accentColor,
                            textColor = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        topic.dataCapsule.forEach { (key, value) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = cardBgColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = key,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        fontSize = fontSize.bodySp.sp
                                    )
                                    Text(
                                        text = value,
                                        color = textColor,
                                        fontSize = fontSize.bodySp.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 8: পরীক্ষার্থীদের জন্য বিশেষ পরামর্শ
                item {
                    ReadingSectionBlock(
                        sectionNumber = "৮",
                        sectionTitle = "পরীক্ষার প্রস্তুতি পরামর্শ (Exam Tips)",
                        content = topic.examTips,
                        fontSize = fontSize.bodySp,
                        lineHeight = bodyLineHeight,
                        textColor = textColor,
                        cardColor = cardBgColor,
                        accentColor = accentColor
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Bottom Sheet for Font Size & Settings
        if (showSettingsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "পঠন মোড কাস্টমাইজেশন",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ফন্ট সাইজ নির্বাচন করুন",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReadingFontSize.values().forEach { size ->
                            FilterChip(
                                selected = fontSize == size,
                                onClick = { viewModel.setReadingFontSize(size) },
                                label = { Text(size.label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "থিম / পটভূমি নির্বাচন করুন",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = readingTheme == ReadingTheme.LIGHT,
                            onClick = { viewModel.setReadingTheme(ReadingTheme.LIGHT) },
                            label = { Text("লাইট") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = readingTheme == ReadingTheme.SEPIA,
                            onClick = { viewModel.setReadingTheme(ReadingTheme.SEPIA) },
                            label = { Text("সেপিয়া (চোখের স্বস্তি)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = readingTheme == ReadingTheme.DARK,
                            onClick = { viewModel.setReadingTheme(ReadingTheme.DARK) },
                            label = { Text("ডার্ক") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "লাইনের ফাঁক (Line Spacing): ${if (isRelaxedSpacing) "খোলামেলা" else "স্বাভাবিক"}",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                        FilledTonalButton(onClick = { viewModel.toggleSpacing() }) {
                            Text("পরিবর্তন")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

data class QuadColors(
    val bg: Color,
    val text: Color,
    val card: Color,
    val accent: Color
)

@Composable
fun SectionHeader(
    number: String,
    title: String,
    accentColor: Color,
    textColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = textColor
        )
    }
}

@Composable
fun ReadingSectionBlock(
    sectionNumber: String,
    sectionTitle: String,
    content: String,
    fontSize: Float,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    textColor: Color,
    cardColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(
                number = sectionNumber,
                title = sectionTitle,
                accentColor = accentColor,
                textColor = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontSize = fontSize.sp,
                lineHeight = lineHeight,
                color = textColor
            )
        }
    }
}
