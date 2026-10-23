package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedTopicEntity
import com.example.ui.viewmodel.BcsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineSavedTopicsScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val savedTopics by viewModel.savedTopics.collectAsState()
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "অফলাইন সংরক্ষিত বিষয়সমূহ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ইন্টারনেট ছাড়া যেকোনো সময় পড়ুন (Room DB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.testTag("saved_topics_top_bar")
        )

        if (savedTopics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DownloadDone,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "কোনো সংরক্ষিত বিষয় নেই",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ডেইলি বুলেটিনের যেকোনো খবরের বুকমার্ক আইকনে ক্লিক করে অফলাইনে পড়ার জন্য সংরক্ষণ করুন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(savedTopics, key = { it.id }) { savedItem ->
                    SavedTopicCard(
                        topic = savedItem,
                        isExpanded = expandedTopicId == savedItem.id,
                        onToggleExpand = {
                            expandedTopicId = if (expandedTopicId == savedItem.id) null else savedItem.id
                        },
                        onDelete = { viewModel.removeSavedTopic(savedItem.id) },
                        onSaveNote = { note -> viewModel.updateSavedTopicNote(savedItem.id, note) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedTopicCard(
    topic: SavedTopicEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDelete: () -> Unit,
    onSaveNote: (String) -> Unit
) {
    var isEditingNote by remember { mutableStateOf(false) }
    var noteText by remember(topic.userNote) { mutableStateOf(topic.userNote) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_topic_card_${topic.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.OfflinePin,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "অফলাইন প্রস্তুত",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_saved_${topic.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = topic.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${topic.sourceDateLabel} • ${topic.category} • ${topic.reportedBy}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onToggleExpand,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("read_saved_btn_${topic.id}"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.AutoStories,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isExpanded) "সংক্ষেপ করুন" else "সম্পূর্ণ অফলাইন বিশ্লেষণ পড়ুন", fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    if (topic.todaysEvent.isNotBlank()) {
                        Text(
                            text = "১. মূল ঘটনা:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = topic.todaysEvent,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }

                    if (topic.whyImportant.isNotBlank()) {
                        Text(
                            text = "২. কেন গুরুত্বপূর্ণ?:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = topic.whyImportant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }

                    if (topic.background.isNotBlank()) {
                        Text(
                            text = "৩. ঐতিহাসিক পটভূমি:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = topic.background,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }

                    if (topic.bangladeshRelevance.isNotBlank()) {
                        Text(
                            text = "৪. বাংলাদেশের জন্য প্রাসঙ্গিকতা:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = topic.bangladeshRelevance,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    }

                    val takeaways = topic.parseTakeaways()
                    if (takeaways.isNotEmpty()) {
                        Text(
                            text = "৫. BCS পরীক্ষার জন্য যা পড়বেন:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        takeaways.forEach { takeaway ->
                            Text(
                                text = "• $takeaway",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // User Personal Note Section
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "আপনার ব্যক্তিগত নোট",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                TextButton(
                                    onClick = {
                                        if (isEditingNote) {
                                            onSaveNote(noteText)
                                            isEditingNote = false
                                        } else {
                                            isEditingNote = true
                                        }
                                    }
                                ) {
                                    Text(if (isEditingNote) "সংরক্ষণ" else if (topic.userNote.isBlank()) "নোট যোগ করুন" else "সম্পাদনা")
                                }
                            }

                            if (isEditingNote) {
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("এই বিষয়ের উপর আপনার নোট লিখুন...") }
                                )
                            } else if (topic.userNote.isNotBlank()) {
                                Text(
                                    text = topic.userNote,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
