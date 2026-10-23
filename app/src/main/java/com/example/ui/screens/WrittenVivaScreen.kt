package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WrittenVivaItem
import com.example.ui.components.SectionHeader
import com.example.ui.viewmodel.BcsViewModel

@Composable
fun WrittenVivaScreen(
    viewModel: BcsViewModel,
    modifier: Modifier = Modifier
) {
    val report by viewModel.currentReport.collectAsState()
    val writtenVivaList = report.writtenVivaAngles

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("written_viva_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "✍️ Written & Viva Angle",
                subtitle = "আজকের আন্তর্জাতিক ঘটনাবলি থেকে লিখিত পরীক্ষা ও ভাইভায় সম্ভাব্য প্রশ্ন এবং পয়েন্টভিত্তিক প্রস্তুতি",
                icon = Icons.Default.EditNote
            )
        }

        // BCS Written Guidance Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎓", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BCS আন্তর্জাতিক লিখিত ও ভাইভা টিপস",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "১. লিখিত পরীক্ষায় আন্তর্জাতিক বিষয়ের উত্তর লেখার সময় সংশ্লিষ্ট ভৌগোলিক মানচিত্র (যেমন: ব্লু লাইন, বাব-এল-মান্দেব, লাদাখ) ও ফ্লোচার্ট আঁকলে সর্বোচ্চ নম্বর পাওয়া যায়।\n" +
                                "২. জাতিসংঘ রেজোলিউশন নম্বর (যেমন: Res 1701), চুক্তি ও সংশ্লিষ্ট সংস্থাগুলোর সঠিক নাম বাংলায় লিখে বন্ধনীতে ইংরেজিতে উল্লেখ করুন।\n" +
                                "৩. ভাইভায় সবসময় বাংলাদেশের স্বার্থ ও সংবিধানের ২৫ নম্বর অনুচ্ছেদের (শান্তিপূর্ণ সহাবস্থান ও বহুপাক্ষিকতা) আলোকে উত্তর উপস্থাপন করবেন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Written & Viva Topic Cards
        items(writtenVivaList, key = { it.id }) { item ->
            WrittenVivaCard(item = item)
        }
    }
}

@Composable
fun WrittenVivaCard(item: WrittenVivaItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = item.examCategory,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Issue Title
            Text(
                text = "📌 ইস্যু: ${item.issue}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Possible Question
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "❓ সম্ভাব্য প্রশ্ন (Possible Question):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.possibleQuestion,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Key Points (বুলেট পয়েন্ট)
            Text(
                text = "📝 উত্তরে যে মূল বিষয়গুলো উল্লেখ করবেন (Key Points):",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))

            item.keyPoints.forEachIndexed { index, point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = point,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Analytical Tip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "💡", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "পরীক্ষার কৌশল / ভাইভা সতর্কতা:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.analyticalTip,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
