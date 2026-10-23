package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.*
import com.example.ui.components.BstDateBanner
import com.example.ui.components.InfoCapsule
import com.example.ui.components.SectionHeader
import com.example.ui.components.YouTubeSuggestionCard
import com.example.ui.viewmodel.BcsViewModel

@Composable
fun DailyReportScreen(
    viewModel: BcsViewModel,
    onOpenDatePicker: () -> Unit,
    onTakeTestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report by viewModel.currentReport.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val expandedTopicIds by viewModel.expandedTopicIds.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val savedTopicIds by viewModel.savedTopicIds.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val categories = listOf("সব", "ভূ-রাজনীতি ও আন্তর্জাতিক নিরাপত্তা", "দ্বিপাক্ষিক সম্পর্ক ও আঞ্চলিক কূটনীতি", "বৈশ্বিক অর্থনীতি ও বাণিজ্য", "পরিবেশ, জলবায়ু কূটনীতি ও আন্তর্জাতিক বাণিজ্য", "ভৌগোলিক রাজনীতি ও কৌশলগত জলপথ")

    val filteredTopics = remember(report, selectedCategory) {
        if (selectedCategory == "সব") report.topics
        else report.topics.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_report_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Bangladesh Standard Time (BST) Banner
        item {
            BstDateBanner(
                report = report,
                onSwitchDateClick = onOpenDatePicker
            )
        }

        // 1.1 6:00 AM BST Daily Edition & Real-Time Sync Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (syncState.isSyncing) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (syncState.isSyncing) {
                                syncState.progressMessage.ifBlank { "৪টি পত্রিকার খবর আপডেট হচ্ছে..." }
                            } else {
                                "🌅 প্রতিদিন সকাল ০৬:০০ BST স্বয়ংক্রিয় সংস্করণ • ৪টি জাতীয় দৈনিকের রিয়েল-টাইম কভারেজ"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!syncState.isSyncing) {
                        IconButton(
                            onClick = { viewModel.triggerDailySync() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "এখনই ৪টি পত্রিকা রিফ্রেশ করুন",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily Comparison / Continuity Card (🆕 নতুন কী? 🔄 চলমান? 📌 আরও গুরুত্বপূর্ণ?)
        item {
            DailyComparisonCard(dayComparison = report.dayComparison)
        }

        // 3. Top International Affairs Headlines of the Day
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE53935).copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🔥", fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "আজকের প্রধান আন্তর্জাতিক ঘটনাবলি (Top Headlines)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    report.topHeadlines.forEachIndexed { index, headline ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = headline,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 4. Category Filter Chips
        item {
            Column {
                SectionHeader(
                    title = "🌍 বিস্তারিত বিষয়ভিত্তিক বিশ্লেষণ",
                    subtitle = "BCS সিলেবাসের আলোকে ৮ স্তরের গভীর বিশ্লেষণ ও সম্ভাব্য প্রশ্নমালা",
                    icon = Icons.Default.Public
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setCategoryFilter(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            leadingIcon = if (selectedCategory == cat) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }
        }

        // 5. Individual Topic Cards (with 8-part structured breakdown)
        items(filteredTopics, key = { it.id }) { topic ->
            val isExpanded = expandedTopicIds.contains(topic.id)
            val isSaved = savedTopicIds.contains(topic.id)
            TopicCard(
                topic = topic,
                isExpanded = isExpanded,
                onToggleExpand = { viewModel.toggleTopicExpansion(topic.id) },
                onBookmarkMcq = { mcq -> viewModel.toggleBookmark(mcq) },
                bookmarkedIds = bookmarkedIds,
                isSaved = isSaved,
                onToggleSave = { viewModel.toggleSaveTopic(topic, report.banglaDate) }
            )
        }

        // 6. Cross-Topic Connections (🔗 আন্তঃসংযোগ)
        item {
            CrossConnectionsCard(connections = report.crossConnections)
        }

        // 7. Overall Bangladesh Angle Summary (🇧🇩 বাংলাদেশ প্রেক্ষিত)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🇧🇩", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সার্বিক বাংলাদেশ পরিপ্রেক্ষিত (Strategic Takeaways)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.bangladeshAngleSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // 8. Call to Action: Take today's test
        item {
            Button(
                onClick = onTakeTestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("take_daily_test_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("আজকের বিষয়গুলোর ওপর MCQ পরীক্ষা দিন (${report.mcqTest.size}টি প্রশ্ন)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DailyComparisonCard(dayComparison: DayComparison) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 পূর্ববর্তী দিনের সাথে তুলনা ও ধারাবাহিকতা",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 🆕 নতুন কী?
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(text = "🆕", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "নতুন কী?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    dayComparison.whatsNew.forEach {
                        Text(text = "• $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔄 চলমান?
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(text = "🔄", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "কোন বিষয় চলমান?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    dayComparison.ongoingIssues.forEach {
                        Text(text = "• $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 📌 আরও গুরুত্বপূর্ণ?
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(text = "📌", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "কোন বিষয়টি আরও গুরুত্বপূর্ণ হয়ে উঠেছে?", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC2185B))
                    dayComparison.escalatedIssues.forEach {
                        Text(text = "• $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun TopicCard(
    topic: TopicAnalysis,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onBookmarkMcq: (McqQuestion) -> Unit,
    bookmarkedIds: Set<String>,
    isSaved: Boolean,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("topic_card_${topic.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Rank + Category + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "#${topic.importanceRank}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = topic.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Save for Offline Reading Button (Room database entity)
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkAdd,
                        contentDescription = if (isSaved) "অফলাইন তালিকা থেকে সরান" else "অফলাইনে পড়ার জন্য সংরক্ষণ করুন",
                        tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "সংকুচিত করুন" else "সম্প্রসারিত করুন"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🟦 Topic Title
            Text(
                text = "🟦 ${topic.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onToggleExpand() }
            )

            // Newspapers reported tag
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "উৎস:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                topic.reportedBy.forEach { paper ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Text(
                            text = paper,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ১. আজকের ঘটনা (Always visible summary)
            Text(
                text = "১. আজকের ঘটনা",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = topic.todaysEvent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // YouTube Video Suggestion (ONLY if verified video exists)
            if (topic.youtubeVideo != null) {
                Spacer(modifier = Modifier.height(10.dp))
                YouTubeSuggestionCard(suggestion = topic.youtubeVideo)
            }

            // Expandable Sections (২ to ৮)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // ২. কেন গুরুত্বপূর্ণ?
                    SubSectionItem(
                        sectionNumber = "২. কেন গুরুত্বপূর্ণ?",
                        content = topic.whyImportant,
                        icon = "🎯"
                    )

                    // ৩. পটভূমি
                    SubSectionItem(
                        sectionNumber = "৩. পটভূমি",
                        content = topic.background,
                        icon = "📜"
                    )

                    // ৪. গুরুত্বপূর্ণ ব্যক্তি ও দেশ
                    EntitiesSection(entities = topic.importantEntities)

                    // ৫. গুরুত্বপূর্ণ তথ্য (Capsules: Dates, Capitals, Headquarters, Treaties, Straits)
                    FactsSection(facts = topic.importantFacts)

                    // ৬. বাংলাদেশের জন্য প্রাসঙ্গিকতা
                    SubSectionItem(
                        sectionNumber = "৬. বাংলাদেশের জন্য প্রাসঙ্গিকতা",
                        content = topic.bangladeshRelevance,
                        icon = "🇧🇩",
                        highlightBg = true
                    )

                    // ৭. BCS-এর জন্য কী পড়বেন?
                    BcsTakeawaysSection(takeaways = topic.bcsTakeaways)

                    // ৮. সম্ভাব্য BCS প্রশ্ন (MCQs, Short, Analytical)
                    PotentialQuestionsSection(
                        questions = topic.potentialQuestions,
                        onBookmark = onBookmarkMcq,
                        bookmarkedIds = bookmarkedIds,
                        youtubeVideo = topic.youtubeVideo
                    )
                }
            }
        }
    }
}

@Composable
fun SubSectionItem(
    sectionNumber: String,
    content: String,
    icon: String,
    highlightBg: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .then(
                if (highlightBg) Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(10.dp)
                else Modifier
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = sectionNumber,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun EntitiesSection(entities: ImportantEntities) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = "৪. গুরুত্বপূর্ণ ব্যক্তি ও দেশ",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (entities.persons.isNotEmpty()) {
            EntityRow(title = "ব্যক্তি:", items = entities.persons)
        }
        if (entities.countries.isNotEmpty()) {
            EntityRow(title = "দেশ:", items = entities.countries)
        }
        if (entities.organizations.isNotEmpty()) {
            EntityRow(title = "সংস্থা:", items = entities.organizations)
        }
        if (entities.alliances.isNotEmpty()) {
            EntityRow(title = "জোট / ফোরাম:", items = entities.alliances)
        }
        if (entities.institutions.isNotEmpty()) {
            EntityRow(title = "প্রতিষ্ঠান:", items = entities.institutions)
        }
    }
}

@Composable
fun EntityRow(title: String, items: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = items.joinToString(", "),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun FactsSection(facts: List<FactItem>) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = "৫. গুরুত্বপূর্ণ তথ্য (High-Yield Facts)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        facts.forEach { fact ->
            InfoCapsule(
                label = "${fact.category} : ${fact.title}",
                value = fact.detail,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            )
        }
    }
}

@Composable
fun BcsTakeawaysSection(takeaways: List<String>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(10.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "৭. BCS-এর জন্য কী পড়বেন?",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(6.dp))
            takeaways.forEach { takeaway ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "✔", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = takeaway,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun PotentialQuestionsSection(
    questions: TopicQuestions,
    onBookmark: (McqQuestion) -> Unit,
    bookmarkedIds: Set<String>,
    youtubeVideo: com.example.model.YouTubeSuggestion? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "৮. সম্ভাব্য BCS প্রশ্ন (Topic Practice)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // YouTube video suggestion button specifically for easier understanding of the topic and question
        if (youtubeVideo != null) {
            val context = androidx.compose.ui.platform.LocalContext.current
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(youtubeVideo.videoUrl)
                        ).apply { addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK) }
                        context.startActivity(intent)
                    }
                    .border(1.dp, Color(0xFFFF0000).copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                color = Color(0xFFFF0000).copy(alpha = 0.05f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF0000),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "YouTube",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "▶ ভিডিও দেখে টপিক ও প্রশ্নসমূহ সহজভাবে বুঝুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCC0000)
                        )
                        Text(
                            text = "${youtubeVideo.channelName} • ${youtubeVideo.title} (${youtubeVideo.duration})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "ভিডিও দেখুন",
                        tint = Color(0xFFCC0000),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // MCQs
        questions.mcqs.forEachIndexed { idx, mcq ->
            var showExplanation by remember { mutableStateOf(false) }
            val isBookmarked = bookmarkedIds.contains(mcq.id)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "MCQ ${idx + 1}: ${mcq.question}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onBookmark(mcq) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "বুকমার্ক",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    mcq.options.forEachIndexed { optIdx, opt ->
                        val isCorrect = optIdx == mcq.correctIndex
                        val optPrefix = when (optIdx) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }
                        Text(
                            text = "($optPrefix) $opt",
                            fontSize = 12.sp,
                            color = if (showExplanation && isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (showExplanation && isCorrect) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showExplanation = !showExplanation },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (showExplanation) "উত্তর ও ব্যাখ্যা লুকান" else "সঠিক উত্তর ও ব্যাখ্যা দেখুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showExplanation) {
                        val correctOptionPrefix = when (mcq.correctIndex) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "সঠিক উত্তর: ($correctOptionPrefix) ${mcq.options.getOrNull(mcq.correctIndex)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ব্যাখ্যা: ${mcq.explanation}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Short Questions
        if (questions.shortQuestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "সংক্ষিপ্ত প্রশ্ন (Short Questions):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
            questions.shortQuestions.forEach { sq ->
                Text(
                    text = "• $sq",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // Analytical / Written Question
        if (questions.analyticalQuestion.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "বিশ্লেষণধর্মী লিখিত প্রশ্ন (Written):",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "✍ ${questions.analyticalQuestion}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun CrossConnectionsCard(connections: List<CrossConnection>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔗", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "আন্তঃসংযোগ (Geopolitical Mental Map)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "আজকের ঘটনাগুলোর ভেতরের অদৃশ্য যোগসূত্র ও বৈশ্বিক প্রভাব শৃঙ্খল:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            connections.forEach { conn ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = conn.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = conn.connectionChain,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 মূল শিক্ষা: ${conn.geopoliticalAnalysis}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
