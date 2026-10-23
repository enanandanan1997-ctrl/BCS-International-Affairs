package com.example.data.sync

data class NewspaperSyncInfo(
    val id: String,
    val name: String,
    val englishName: String,
    val section: String,
    val webUrl: String,
    val rssUrl: String = "",
    val status: String = "অপেক্ষমাণ",
    val articlesCount: Int = 0,
    val lastUpdatedBst: String = "",
    val isSuccess: Boolean = true
)

data class DailySyncState(
    val isAutoUpdateEnabled: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val lastSyncFormattedBst: String = "আজ সকাল ০৬:০০ AM BST",
    val lastSyncDateKey: String = "",
    val nextScheduledBst: String = "আগামীকাল সকাল ০৬:০০ AM BST",
    val countdownText: String = "",
    val progressMessage: String = "",
    val newspaperStatuses: List<NewspaperSyncInfo> = defaultNewspaperStatuses
) {
    companion object {
        val defaultNewspaperStatuses = listOf(
            NewspaperSyncInfo(
                id = "src_prothom_alo",
                name = "প্রথম আলো",
                englishName = "Prothom Alo",
                section = "আন্তর্জাতিক পাতা",
                webUrl = "https://www.prothomalo.com/world",
                rssUrl = "https://www.prothomalo.com/feed",
                status = "রিয়েল-টাইম সংস্করণ সংগৃহীত",
                articlesCount = 8,
                lastUpdatedBst = "০৬:০০ AM BST",
                isSuccess = true
            ),
            NewspaperSyncInfo(
                id = "src_daily_star",
                name = "দ্য ডেইলি স্টার",
                englishName = "The Daily Star",
                section = "World & International Desk",
                webUrl = "https://www.thedailystar.net/news/world",
                rssUrl = "https://www.thedailystar.net/rss",
                status = "রিয়েল-টাইম সংস্করণ সংগৃহীত",
                articlesCount = 7,
                lastUpdatedBst = "০৬:০০ AM BST",
                isSuccess = true
            ),
            NewspaperSyncInfo(
                id = "src_bonik_barta",
                name = "বণিক বার্তা",
                englishName = "Bonik Barta",
                section = "আন্তর্জাতিক বাণিজ্য ও অর্থনীতি",
                webUrl = "https://bonikbarta.net/international",
                rssUrl = "",
                status = "রিয়েল-টাইম সংস্করণ সংগৃহীত",
                articlesCount = 5,
                lastUpdatedBst = "০৬:০০ AM BST",
                isSuccess = true
            ),
            NewspaperSyncInfo(
                id = "src_dhaka_tribune",
                name = "ঢাকা ট্রিবিউন",
                englishName = "Dhaka Tribune",
                section = "World News",
                webUrl = "https://www.dhakatribune.com/world",
                rssUrl = "",
                status = "রিয়েল-টাইম সংস্করণ সংগৃহীত",
                articlesCount = 6,
                lastUpdatedBst = "০৬:০০ AM BST",
                isSuccess = true
            )
        )
    }
}
