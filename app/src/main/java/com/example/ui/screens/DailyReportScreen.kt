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
import androidx.compose.ui.text.style.TextOverflow
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
    val globalSearchQuery by viewModel.globalSearchQuery.collectAsState()

    val categories = listOf("সব", "ভূ-রাজনীতি ও আন্তর্জাতিক নিরাপত্তা", "দ্বিপাক্ষিক সম্পর্ক ও আঞ্চলিক কূটনীতি", "বৈশ্বিক অর্থনীতি ও বাণিজ্য", "পরিবেশ, জলবায়ু কূটনীতি ও আন্তর্জাতিক বাণিজ্য", "ভৌগোলিক রাজনীতি ও কৌশলগত জলপথ")

    val filteredTopics = remember(report, selectedCategory) {
        val baseList = if (selectedCategory == "সব") report.topics
        else report.topics.filter { it.category == selectedCategory }
        // Latest articles are shown at first (#1 is the latest, primary headline of the day)
        baseList.sortedBy { it.importanceRank }
    }

    // Global Search across all international affairs content
    val queryTrimmed = globalSearchQuery.trim()
    val isSearchActive = queryTrimmed.isNotBlank()

    val searchResults = remember(report, queryTrimmed) {
        if (queryTrimmed.isBlank()) null
        else {
            val q = queryTrimmed.lowercase()
            val matchesText = { text: String -> text.lowercase().contains(q) }

            val matchingTopics = report.topics.filter { t ->
                matchesText(t.title) ||
                matchesText(t.category) ||
                matchesText(t.todaysEvent) ||
                matchesText(t.whyImportant) ||
                matchesText(t.background) ||
                matchesText(t.bangladeshRelevance) ||
                t.bcsTakeaways.any { matchesText(it) } ||
                t.importantFacts.any { matchesText(it.title) || matchesText(it.detail) } ||
                t.importantEntities.persons.any { matchesText(it) } ||
                t.importantEntities.countries.any { matchesText(it) } ||
                t.importantEntities.organizations.any { matchesText(it) } ||
                t.importantEntities.alliances.any { matchesText(it) } ||
                t.reportedBy.any { matchesText(it) }
            }.sortedBy { it.importanceRank }

            val matchingHeadlines = report.topHeadlines.filter { matchesText(it) }

            val matchingRevisions = report.quickRevisions.filter {
                matchesText(it.questionOrFact) || matchesText(it.answer) || matchesText(it.category)
            }

            val matchingMcqs = report.mcqTest.filter {
                matchesText(it.question) || matchesText(it.explanation) || it.options.any { opt -> matchesText(opt) }
            }

            val matchingWrittenViva = report.writtenVivaAngles.filter {
                matchesText(it.issue) || matchesText(it.possibleQuestion) || it.keyPoints.any { kp -> matchesText(kp) } || matchesText(it.analyticalTip)
            }

            GlobalSearchResultsHolder(
                query = queryTrimmed,
                topics = matchingTopics,
                headlines = matchingHeadlines,
                revisions = matchingRevisions,
                mcqs = matchingMcqs,
                writtenViva = matchingWrittenViva
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 0. Global Search Bar pinned at the very top of the main screen
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            GlobalSearchBar(
                query = globalSearchQuery,
                onQueryChange = { viewModel.setGlobalSearchQuery(it) },
                onClearQuery = { viewModel.clearGlobalSearchQuery() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("daily_report_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isSearchActive && searchResults != null) {
                // ==================== SEARCH RESULTS MODE ====================
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🔍 অনুসন্ধান: \"$queryTrimmed\"",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = if (searchResults.totalCount > 0) {
                                        "মোট ${searchResults.totalCount}টি তথ্য পাওয়া গেছে (${searchResults.topics.size}টি বিষয়, ${searchResults.revisions.size}টি রিভিশন, ${searchResults.mcqs.size}টি MCQ)"
                                    } else {
                                        "কোনো ফলাফল পাওয়া যায়নি"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            TextButton(
                                onClick = { viewModel.clearGlobalSearchQuery() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("মুছুন", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // If No Results Found
                if (searchResults.totalCount == 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "🔎", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "\"$queryTrimmed\" সম্পর্কিত তথ্য মেলেনি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "বানান সঠিক কিনা যাচাই করুন অথবা নিচের জনপ্রিয় কোনো আন্তর্জাতিক বিষয় বেছে নিন:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    listOf("BRICS", "লেবানন", "আইএমএফ", "হরমুজ").forEach { sug ->
                                        AssistChip(
                                            onClick = { viewModel.setGlobalSearchQuery(sug) },
                                            label = { Text(sug, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 1. Matching Topics
                if (searchResults.topics.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "🌍 বিষয়ভিত্তিক বিশ্লেষণ (${searchResults.topics.size}টি)",
                            subtitle = "শিরোনামে ট্যাপ করে বিস্তারিত ৮ স্তরের বিশ্লেষণ পড়ুন",
                            icon = Icons.Default.Public
                        )
                    }
                    items(searchResults.topics, key = { "search_${it.id}" }) { topic ->
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
                }

                // 2. Matching Quick Revisions
                if (searchResults.revisions.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "⚡ কুইক রিভিশন তথ্য (${searchResults.revisions.size}টি)",
                            subtitle = "পরীক্ষায় সরাসরি কমন উপযোগী ওয়ান-লাইনার",
                            icon = Icons.Default.Bolt
                        )
                    }
                    items(searchResults.revisions, key = { "rev_${it.serial}" }) { rev ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "#${rev.serial} ${rev.category}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = rev.questionOrFact,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "উত্তর: ${rev.answer}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // 3. Matching MCQs
                if (searchResults.mcqs.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "📝 MCQ পরীক্ষা প্রশ্ন (${searchResults.mcqs.size}টি)",
                            subtitle = "অনুসন্ধান ফলাফলে প্রাপ্ত প্রিলিমিনারি প্রশ্ন",
                            icon = Icons.Default.Quiz
                        )
                    }
                    items(searchResults.mcqs, key = { "mcq_${it.id}" }) { mcq ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${mcq.questionNumber}. ${mcq.question}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                mcq.options.forEachIndexed { optIndex, optText ->
                                    val isCorrect = optIndex == mcq.correctIndex
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${optIndex + 1}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCorrect) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optText,
                                            fontSize = 12.sp,
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ব্যাখ্যা: ${mcq.explanation}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // 4. Matching Written & Viva questions
                if (searchResults.writtenViva.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "✍️ লিখিত ও ভাইভা প্রস্তুতি (${searchResults.writtenViva.size}টি)",
                            subtitle = "বিশ্লেষণধর্মী প্রশ্ন ও ভাইভা পরামর্শ",
                            icon = Icons.Default.MenuBook
                        )
                    }
                    items(searchResults.writtenViva, key = { "wv_${it.id}" }) { wv ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "ইস্যু: ${wv.issue}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "প্রশ্ন: ${wv.possibleQuestion}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                wv.keyPoints.forEach { kp ->
                                    Text(
                                        text = "• $kp",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                                if (wv.analyticalTip.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💡 ভাইভা টিপস: ${wv.analyticalTip}",
                                            modifier = Modifier.padding(8.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                // ==================== REGULAR DAILY VIEW MODE ====================
                // 1. Bangladesh Standard Time (BST) Banner
                item {
                    BstDateBanner(
                        report = report,
                        onSwitchDateClick = onOpenDatePicker
                    )
                }

                // 1.1 Visual Indicator: Content Successfully Synced & Available Offline
                item {
                    OfflineSyncStatusCard(
                        syncState = syncState,
                        reportDateLabel = report.banglaDate,
                        onManualSync = { viewModel.triggerDailySync() }
                    )
                }

                // 2. Daily Comparison / Continuity Card
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

                // 5. Individual Topic Cards (with 8-part structured breakdown, tap-to-expand)
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
    }
}

data class GlobalSearchResultsHolder(
    val query: String,
    val topics: List<TopicAnalysis>,
    val headlines: List<String>,
    val revisions: List<QuickRevisionItem>,
    val mcqs: List<McqQuestion>,
    val writtenViva: List<WrittenVivaItem>
) {
    val totalCount: Int
        get() = topics.size + headlines.size + revisions.size + mcqs.size + writtenViva.size
}

@Composable
fun GlobalSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickKeywords = listOf("BRICS", "লেবানন", "UNIFIL", "আইএমএফ", "হরমুজ প্রণালী", "CBAM শুল্ক", "উত্তর কোরিয়া", "ন্যাটো")

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("global_search_input"),
            placeholder = {
                Text(
                    text = "আন্তর্জাতিক বিষয়াবলি বা কি-ওয়ার্ড অনুসন্ধান করুন...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "অনুসন্ধান",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(
                        onClick = onClearQuery,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "মুছুন",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Search Keyword Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Text(
                    text = "জনপ্রিয়:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            items(quickKeywords) { keyword ->
                val isSelected = query.equals(keyword, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp),
                    modifier = Modifier.clickable {
                        if (isSelected) onClearQuery() else onQueryChange(keyword)
                    }
                ) {
                    Text(
                        text = keyword,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
fun OfflineSyncStatusCard(
    syncState: com.example.data.sync.DailySyncState,
    reportDateLabel: String,
    onManualSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("offline_sync_status_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (syncState.isSyncing) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
            } else {
                Color(0xFFE8F5E9)
            }
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (syncState.isSyncing) MaterialTheme.colorScheme.tertiary else Color(0xFF81C784)
            ),
            width = 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (syncState.isSyncing) MaterialTheme.colorScheme.primaryContainer else Color(0xFF2E7D32),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (syncState.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "অফলাইন সিঙ্ক সফল",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (syncState.isSyncing) "সিঙ্ক হচ্ছে..." else "কন্টেন্ট সফলভাবে সিঙ্ক সম্পন্ন",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (syncState.isSyncing) MaterialTheme.colorScheme.onTertiaryContainer else Color(0xFF1B5E20)
                            )
                            if (!syncState.isSyncing) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "অফলাইন প্রস্তুত",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (syncState.isSyncing) {
                                syncState.progressMessage.ifBlank { "৪টি পত্রিকার খবর আপডেট হচ্ছে..." }
                            } else {
                                "ইন্টারনেট ছাড়াও পুরো সংস্করণ পড়ার জন্য ডিভাইসে সংরক্ষিত"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (syncState.isSyncing) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) else Color(0xFF2E7D32)
                        )
                    }
                }

                IconButton(
                    onClick = onManualSync,
                    enabled = !syncState.isSyncing,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "এখনই সিঙ্ক রিফ্রেশ করুন",
                        tint = if (syncState.isSyncing) MaterialTheme.colorScheme.outline else Color(0xFF1B5E20),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (!syncState.isSyncing) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFC8E6C9), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📶 অফলাইন মোড", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📰 ৪টি আন্তর্জাতিক পাতা", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B5E20))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏱️ ${syncState.lastSyncFormattedBst.ifBlank { "আজ ০৬:০০ AM" }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Rank + Category + Latest Tag + Save Action + Expand Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (topic.importanceRank == 1) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
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

                Spacer(modifier = Modifier.width(8.dp))

                if (topic.importanceRank == 1) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = "⚡ সর্বশেষ (Latest)",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

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

            Spacer(modifier = Modifier.height(10.dp))

            // 🟦 Clickable Headline (Tapping headline expands into full article)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleExpand() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "🟦 ${topic.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.weight(1f)
                )
            }

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

            // When COLLAPSED: Show brief teaser and clean invitation to tap headline to expand
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = topic.todaysEvent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp,
                    modifier = Modifier.clickable { onToggleExpand() }
                )

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleExpand() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "হেডলাইনে ট্যাপ করে সম্পূর্ণ ৮ স্তরের বিশ্লেষণ পড়ুন",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "প্রসারিত করুন",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // When EXPANDED: Show the COMPLETE 8-PART FULL ARTICLE
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                    // ১. আজকের ঘটনা
                    Text(
                        text = "১. আজকের ঘটনা",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.todaysEvent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    // YouTube Video Suggestion (ONLY if verified video exists)
                    if (topic.youtubeVideo != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        YouTubeSuggestionCard(suggestion = topic.youtubeVideo)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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

                    // Bottom collapse button
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ExpandLess, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "প্রতিবেদন সংক্ষেপ করুন", fontWeight = FontWeight.Bold)
                    }
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
