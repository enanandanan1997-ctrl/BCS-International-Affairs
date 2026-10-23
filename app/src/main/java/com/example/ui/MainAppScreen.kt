package com.example.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.*
import com.example.ui.viewmodel.BcsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentReport by viewModel.currentReport.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val selectedTab by viewModel.selectedTabIndex.collectAsState()
    val savedTopics by viewModel.savedTopics.collectAsState()

    var showArchiveDialog by remember { mutableStateOf(false) }
    var showBookmarksDialog by remember { mutableStateOf(false) }
    var showChatSheet by remember { mutableStateOf(false) }

    // BackHandler to return to tab 0 if in any other tab
    BackHandler(enabled = selectedTab != 0) {
        viewModel.selectTab(0)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_bcs_icon_1792149194719),
                                    contentDescription = "BCS আন্তর্জাতিক লোগো",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BCS আন্তর্জাতিক",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentReport.banglaDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "অফলাইন প্রস্তুত",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                actions = {
                    // Global Search button
                    IconButton(
                        onClick = { viewModel.selectTab(0) },
                        modifier = Modifier.testTag("open_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "অনুসন্ধান",
                            tint = if (selectedTab == 0 && viewModel.globalSearchQuery.collectAsState().value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Archive switcher button
                    IconButton(
                        onClick = { showArchiveDialog = true },
                        modifier = Modifier.testTag("open_archive_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "আর্কাইভ"
                        )
                    }

                    // Sources & Engine Button
                    IconButton(
                        onClick = { viewModel.selectTab(5) },
                        modifier = Modifier.testTag("open_sources_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = "সংবাদপত্রের উৎস",
                            tint = if (selectedTab == 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Bookmarked MCQs button
                    IconButton(
                        onClick = { showBookmarksDialog = true },
                        modifier = Modifier.testTag("open_bookmarks_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "বুকমার্কস"
                        )
                    }

                    // Settings & Newspaper Management Button
                    IconButton(
                        onClick = { viewModel.selectTab(6) },
                        modifier = Modifier.testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "সেটিংস ও রিসোর্স",
                            tint = if (selectedTab == 6) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Share daily summary button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${currentReport.heading}\nসময়: ${currentReport.banglaTime}\n\n" +
                                            "শীর্ষ আন্তর্জাতিক খবর:\n" +
                                            currentReport.topHeadlines.mapIndexed { i, h -> "${i + 1}. $h" }.joinToString("\n") +
                                            "\n\nআজকের কুইক রিভিশন:\n" +
                                            currentReport.quickRevisions.take(5).map { "${it.serial}. ${it.questionOrFact} → ${it.answer}" }.joinToString("\n") +
                                            "\n\n[BCS আন্তর্জাতিক প্রস্তুতি অ্যাপ থেকে শেয়ারকৃত]"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "আজকের BCS নোট শেয়ার করুন"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার করুন"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showChatSheet = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                text = {
                    Text(
                        text = "মেন্টর AI চ্যাট",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("fab_mentor_chat")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.Feed, contentDescription = "দৈনিক বুলেটিন") },
                    label = { Text("বুলেটিন", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_daily_report")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "কুইক রিভিশন") },
                    label = { Text("রিভিশন", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_quick_revision")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "আজকের পরীক্ষা") },
                    label = { Text("MCQ টেস্ট", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_daily_test")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = "লিখিত ও ভাইভা") },
                    label = { Text("লিখিত/ভাইভা", fontSize = 10.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_written_viva")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (savedTopics.isNotEmpty()) {
                                    Badge {
                                        Text("${savedTopics.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = "অফলাইন পাঠ")
                        }
                    },
                    label = { Text("অফলাইন পাঠ", fontSize = 10.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_offline_saved")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DailyReportScreen(
                    viewModel = viewModel,
                    onOpenDatePicker = { showArchiveDialog = true },
                    onTakeTestClick = { viewModel.selectTab(2) }
                )
                1 -> QuickRevisionScreen(viewModel = viewModel)
                2 -> DailyExamScreen(viewModel = viewModel)
                3 -> WrittenVivaScreen(viewModel = viewModel)
                4 -> OfflineSavedTopicsScreen(viewModel = viewModel)
                5 -> SourcesAndEngineScreen(viewModel = viewModel)
                6 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Bottom Sheet for Multi-turn Gemini Chatbot with Google Search
    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.88f)) {
                ChatbotScreen(viewModel = viewModel)
            }
        }
    }

    // Archive Dialog
    if (showArchiveDialog) {
        DateArchiveDialog(
            allReports = allReports,
            currentReportId = currentReport.id,
            onSelectReport = { reportId ->
                viewModel.selectReport(reportId)
            },
            onDismiss = { showArchiveDialog = false }
        )
    }

    // Bookmarks Dialog
    if (showBookmarksDialog) {
        BookmarksDialog(
            onDismiss = { showBookmarksDialog = false }
        )
    }
}
