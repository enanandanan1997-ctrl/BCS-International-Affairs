package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuickRevisionItem
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.BcsViewModel

@Composable
fun QuickRevisionScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.currentReport.collectAsState()
    val searchQuery by viewModel.revisionSearchQuery.collectAsState()
    val isFlashcardMode by viewModel.revisionFlashcardMode.collectAsState()
    val revealedIndices by viewModel.revealedFlashcardIndices.collectAsState()

    var selectedTag by remember { mutableStateOf("সব") }

    val tags = remember(report) {
        listOf("সব") + report.quickRevisions.map { it.category }.distinct()
    }

    val filteredItems = remember(report, searchQuery, selectedTag) {
        report.quickRevisions.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.questionOrFact.contains(searchQuery, ignoreCase = true) ||
                    item.answer.contains(searchQuery, ignoreCase = true)

            val matchesTag = selectedTag == "সব" || item.category == selectedTag

            matchesSearch && matchesTag
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quick_revision_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "⚡ আজকের BCS Quick Revision",
                subtitle = "আজকের গুরুত্বপূর্ণ ঘটনাবলি থেকে ১৫-২৫টি অতি-গুরুত্বপূর্ণ এক লাইনের প্রশ্ন-উত্তর",
                icon = Icons.Default.Bolt
            )
        }

        // Mode switch & Search Bar
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Search text field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setRevisionSearch(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_revision_search"),
                        placeholder = { Text("তথ্য বা প্রশ্ন খুঁজুন...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setRevisionSearch("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "মুছুন")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "মোট তথ্য: ${filteredItems.size}টি",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )

                        // Flashcard toggle button
                        FilterChip(
                            selected = isFlashcardMode,
                            onClick = { viewModel.toggleFlashcardMode() },
                            label = {
                                Text(
                                    text = if (isFlashcardMode) "ফ্ল্যাশ কার্ড মোড চালু" else "ফ্ল্যাশ কার্ড মোড",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isFlashcardMode) Icons.Default.VisibilityOff else Icons.Default.Flip,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Category Tags
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(tags) { tag ->
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = { selectedTag = tag },
                        label = { Text(tag, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Quick Revision Items
        items(filteredItems, key = { it.serial }) { item ->
            val isRevealed = revealedIndices.contains(item.serial)
            QuickRevisionCard(
                item = item,
                isFlashcardMode = isFlashcardMode,
                isRevealed = isRevealed,
                onToggleReveal = { viewModel.toggleFlashcardReveal(item.serial) }
            )
        }
    }
}

@Composable
fun QuickRevisionCard(
    item: QuickRevisionItem,
    isFlashcardMode: Boolean,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isFlashcardMode) { onToggleReveal() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${item.serial}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = item.category,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isFlashcardMode) {
                    Text(
                        text = if (isRevealed) "উত্তর লুকান" else "উত্তর দেখতে চাপুন",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // প্রশ্ন বা মূল তথ্য
            Text(
                text = "${item.serial}. ${item.questionOrFact}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // উত্তর অংশ
            if (isFlashcardMode) {
                AnimatedVisibility(visible = isRevealed) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👉", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.answer,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                if (!isRevealed) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = "🔒 উত্তর দেখতে ট্যাপ করুন (Flashcard Mode)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                // Direct Format: প্রশ্ন/তথ্য → উত্তর
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "➔", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.answer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
