package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ManagedNewspaper
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.BcsViewModel

import com.example.data.sync.NewspaperSyncInfo
import com.example.data.time.BangladeshTimeHelper
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val managedNewspapers by viewModel.managedNewspapers.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newspaperToEdit by remember { mutableStateOf<ManagedNewspaper?>(null) }
    var newspaperToDelete by remember { mutableStateOf<ManagedNewspaper?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.refreshSyncCountdown()
            delay(30000L)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            SectionHeader(
                title = "⚙️ সেটিংস ও রিসোর্স কনফিগারেশন",
                subtitle = "গুগল সাইন-ইন এবং আপনার পছন্দের আন্তর্জাতিক সংবাদপত্র ও রিসোর্স ব্যবস্থাপনা",
                icon = Icons.Default.Settings
            )
        }

        // 1. Google Sign-In & Firebase Account Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("auth_account_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ব্যবহারকারী প্রোফাইল (Firebase Auth)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentUser != null) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (currentUser != null) "✓ ক্লাউড সিঙ্ক সক্রিয়" else "অতিথি মোড (অফলাইন)",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentUser != null) {
                        val user = currentUser!!
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val initial = user.displayName?.firstOrNull()?.uppercaseChar()?.toString()
                                        ?: user.email?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                                    Text(
                                        text = initial,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.displayName ?: "BCS শিক্ষার্থী",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = user.email ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Text("সাইন আউট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "💡 আপনার কাস্টম যুক্তকৃত সংবাদপত্র, অফলাইন পড়া ও বুকমার্ক ফায়ারবেস ক্লাউডে স্বয়ংক্রিয়ভাবে সংরক্ষিত রয়েছে।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column {
                            Text(
                                text = "গুগল একাউন্টে সাইন ইন করুন",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "আপনার নিজস্ব যুক্তকৃত সংবাদপত্র, সংরক্ষিত টপিক ও পরীক্ষার ফলাফল ক্লাউডে সিঙ্ক রাখতে গুগল সাইন-ইন ব্যবহার করুন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )

                            if (authError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = authError ?: "",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    activity?.let { viewModel.signInWithGoogle(it) }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("google_sign_in_button"),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isAuthLoading
                            ) {
                                if (isAuthLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("সংযোগ করা হচ্ছে...")
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign in with Google (গুগল দিয়ে প্রবেশ)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Automated Daily 6:00 AM BST Update & 4 Newspapers Real-time Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_sync_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "স্বয়ংক্রিয় দৈনিক আপডেট (সকাল ৬:০০ BST)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "বাংলাদেশ সময় প্রতিদিন সকাল ৬:০০ টায় রিয়েল-টাইম তথ্য",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Switch(
                            checked = syncState.isAutoUpdateEnabled,
                            onCheckedChange = { viewModel.toggleAutoUpdate(it) },
                            modifier = Modifier.testTag("auto_sync_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Clock & Countdown Badges
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "বাংলাদেশ মান সময়: ${BangladeshTimeHelper.getFormattedBstTime()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (syncState.isAutoUpdateEnabled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (syncState.isAutoUpdateEnabled) {
                                        "পরবর্তী স্বয়ংক্রিয় আপডেট: ${syncState.nextScheduledBst} (${syncState.countdownText})"
                                    } else {
                                        "স্বয়ংক্রিয় আপডেট বন্ধ রয়েছে (ম্যানুয়াল আপডেট সচল)"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (syncState.isAutoUpdateEnabled) Color(0xFF1B5E20) else MaterialTheme.colorScheme.outline
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "সর্বশেষ সফল আপডেট: ${syncState.lastSyncFormattedBst}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Sync in-progress banner
                    if (syncState.isSyncing) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = syncState.progressMessage.ifBlank { "তথ্য আপডেট করা হচ্ছে..." },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Newspapers Real-time status cards
                    Text(
                        text = "৪টি জাতীয় দৈনিকের রিয়েল-টাইম কভারেজ স্ট্যাটাস:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    syncState.newspaperStatuses.forEach { paper ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${paper.name} (${paper.englishName})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${paper.section} • ${paper.status}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = "${paper.articlesCount}টি সংবাদ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Manual update now button
                    Button(
                        onClick = { viewModel.triggerDailySync() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("trigger_daily_sync_button"),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !syncState.isSyncing
                    ) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("আপডেট সংগ্রহ হচ্ছে...")
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("এখনই ৪টি পত্রিকা থেকে রিয়েল-টাইম তথ্য আপডেট করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Newspaper Management Header & Controls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val activeCount = managedNewspapers.count { it.isActive }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "সংবাদপত্র ও উৎস ব্যবস্থাপনা",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "সক্রিয় উৎস: $activeCount / ${managedNewspapers.size}টি পত্রিকা",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_newspaper_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন উৎস", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "আপনি যেকোনো আন্তর্জাতিক সংবাদপত্র, সংস্থা বা থিংক ট্যাঙ্কের URL যুক্ত করতে পারেন। প্রতিদিনের বিশ্লেষণে কোন কোন পত্রিকা থাকবে তা ডানপাশের সুইচ দিয়ে অন/অফ করুন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.resetNewspapersToDefault() },
                            modifier = Modifier.testTag("reset_defaults_button")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ডিফল্ট ৪টি পত্রিকায় রিসেট করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. List of Newspapers Cards
        items(managedNewspapers, key = { it.id }) { newspaper ->
            NewspaperSourceCard(
                newspaper = newspaper,
                onToggleActive = { viewModel.toggleNewspaperActive(newspaper.id) },
                onEdit = { newspaperToEdit = newspaper },
                onDelete = { newspaperToDelete = newspaper }
            )
        }

        // 4. Architectural Analysis Explainer Card (Direct answer to user's question)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "💡 এই পত্রিকাগুলোর জন্য আলাদা কোনো API যুক্ত করতে হবে কি?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• সংবাদপত্রগুলোর নিজস্ব API: না! প্রথম আলো, দ্য ডেইলি স্টার, বণিক বার্তা বা ঢাকা ট্রিবিউন—কোনো পত্রিকারই নিজস্ব কোনো ডেভেলপার API বা পেইড সাবস্ক্রিপশন প্রয়োজন নেই (এবং তাদের উন্মুক্ত কোনো পাবলিক API নেই-ও)।\n\n" +
                                "• একমাত্র প্রয়োজনীয় API (Gemini API): এই সিস্টেমটি স্বয়ংক্রিয়ভাবে চলার জন্য কেবল একটিমাত্র API ব্যবহার করে—Gemini API (গুগল সার্চ গ্রাউন্ডিং সহ)। AI Studio-এর Secrets প্যানেলে GEMINI_API_KEY দেওয়া থাকলেই যথেষ্ট।\n\n" +
                                "• যেভাবে স্বয়ংক্রিয়ভাবে সংবাদ সংগ্রহ হয়:\n" +
                                "  ১. উন্মুক্ত RSS ফিড: প্রথম আলো ও ডেইলি স্টার-এর ফ্রি পাবলিক RSS থেকে হেডলাইন সংগ্রহ করা হয় (কোনো চাবি বা ফি ছাড়া)।\n" +
                                "  ২. গুগল সার্চ গ্রাউন্ডিং: জেমিনি এআই সরাসরি গুগলের লাইভ সার্চ ইনডেক্স ব্যবহার করে ৪টি পত্রিকার আন্তর্জাতিক পাতার সর্বশেষ খবর নিয়ে আসে এবং পেওয়াল/স্ক্র্যাপিং ব্লকিং এড়িয়ে চলে।\n" +
                                "  ৩. নতুন যেকোনো ওয়েবসাইট: ব্যবহারকারী যেকোনো নতুন জাতীয়/আন্তর্জাতিক সংবাদপত্রের URL বা RSS যুক্ত করলেও জেমিনি একই পদ্ধতিতে তা বিশ্লেষণ করে নেবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runDailyAnalysisEngine() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("বর্তমান সক্রিয় উৎসগুলো নিয়ে বুলেটিন আপডেট করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Add Newspaper Dialog
    if (showAddDialog) {
        NewspaperEditDialog(
            initialNewspaper = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, englishName, section, webUrl, rssUrl, category ->
                viewModel.addCustomNewspaper(name, englishName, section, webUrl, rssUrl, category)
                showAddDialog = false
            }
        )
    }

    // Edit Newspaper Dialog
    newspaperToEdit?.let { newspaper ->
        NewspaperEditDialog(
            initialNewspaper = newspaper,
            onDismiss = { newspaperToEdit = null },
            onSave = { name, englishName, section, webUrl, rssUrl, category ->
                val updated = newspaper.copy(
                    name = name,
                    englishName = englishName,
                    section = section,
                    webUrl = webUrl,
                    rssUrl = rssUrl,
                    category = category
                )
                viewModel.updateNewspaper(updated)
                newspaperToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    newspaperToDelete?.let { newspaper ->
        AlertDialog(
            onDismissRequest = { newspaperToDelete = null },
            title = { Text("পত্রিকা উৎস মুছে ফেলবেন?") },
            text = { Text("'${newspaper.name}' উৎসটি আপনার তালিকা থেকে স্থায়ীভাবে মুছে ফেলা হবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNewspaper(newspaper.id)
                        newspaperToDelete = null
                    }
                ) {
                    Text("মুছে ফেলুন", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { newspaperToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun NewspaperSourceCard(
    newspaper: ManagedNewspaper,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (newspaper.isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (newspaper.isActive) 2.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("newspaper_card_${newspaper.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (newspaper.isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Newspaper,
                                contentDescription = null,
                                tint = if (newspaper.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = newspaper.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (newspaper.isDefault) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = if (newspaper.isDefault) "ডিফল্ট" else "কাস্টম",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = if (newspaper.isDefault) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                        Text(
                            text = "${newspaper.englishName} • ${newspaper.section}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Active Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (newspaper.isActive) "সক্রিয়" else "নিষ্ক্রিয়",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (newspaper.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = newspaper.isActive,
                        onCheckedChange = { onToggleActive() },
                        modifier = Modifier.testTag("toggle_switch_${newspaper.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // URL Row & Actions
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newspaper.webUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignored
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = newspaper.webUrl,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "ওয়েবসাইট খুলুন",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            if (newspaper.rssUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RssFeed,
                        contentDescription = "RSS Feed",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFF57C00)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RSS Feed: ${newspaper.rssUrl}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "ক্যাটাগরি: ${newspaper.category}",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp).testTag("edit_newspaper_${newspaper.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "সম্পাদনা",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_newspaper_${newspaper.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "মুছে ফেলুন",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewspaperEditDialog(
    initialNewspaper: ManagedNewspaper?,
    onDismiss: () -> Unit,
    onSave: (name: String, englishName: String, section: String, webUrl: String, rssUrl: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf(initialNewspaper?.name ?: "") }
    var englishName by remember { mutableStateOf(initialNewspaper?.englishName ?: "") }
    var section by remember { mutableStateOf(initialNewspaper?.section ?: "আন্তর্জাতিক") }
    var webUrl by remember { mutableStateOf(initialNewspaper?.webUrl ?: "https://") }
    var rssUrl by remember { mutableStateOf(initialNewspaper?.rssUrl ?: "") }
    var category by remember { mutableStateOf(initialNewspaper?.category ?: "আন্তর্জাতিক") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val isEditing = initialNewspaper != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "সংবাদপত্র সম্পাদনা করুন" else "নতুন সংবাদপত্র / উৎস যোগ করুন",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (validationError != null) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("পত্রিকার নাম (বাংলা)") },
                    placeholder = { Text("যেমন: আল জাজিরা / বিবিসি বাংলা") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_name")
                )

                OutlinedTextField(
                    value = englishName,
                    onValueChange = { englishName = it },
                    label = { Text("ইংরেজি নাম (English Name)") },
                    placeholder = { Text("যেমন: Al Jazeera / Reuters") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_english_name")
                )

                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("বিভাগ / সেকশন") },
                    placeholder = { Text("যেমন: World News / আন্তর্জাতিক") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_section")
                )

                OutlinedTextField(
                    value = webUrl,
                    onValueChange = { webUrl = it },
                    label = { Text("ওয়েবসাইট URL") },
                    placeholder = { Text("https://www.example.com/world") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_url")
                )

                OutlinedTextField(
                    value = rssUrl,
                    onValueChange = { rssUrl = it },
                    label = { Text("RSS Feed URL (ঐচ্ছিক)") },
                    placeholder = { Text("https://www.example.com/rss") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_rss")
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("ক্যাটাগরি") },
                    placeholder = { Text("যেমন: আন্তর্জাতিক, কূটনীতি, বিশ্ব বাণিজ্য") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_newspaper_category")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        validationError = "অনুগ্রহ করে পত্রিকার নাম লিখুন"
                        return@Button
                    }
                    if (webUrl.isBlank() || (!webUrl.startsWith("http://") && !webUrl.startsWith("https://"))) {
                        validationError = "সঠিক URL প্রদান করুন (http:// বা https:// দিয়ে শুরু)"
                        return@Button
                    }
                    onSave(name, englishName, section, webUrl, rssUrl, category)
                },
                modifier = Modifier.testTag("save_newspaper_button")
            ) {
                Text(if (isEditing) "আপডেট করুন" else "সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
