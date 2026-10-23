package com.example.model

/**
 * Model representing a newspaper or resource source that users can manage,
 * add, edit, or remove in Settings.
 */
data class ManagedNewspaper(
    val id: String,
    val name: String,
    val englishName: String,
    val section: String,
    val webUrl: String,
    val rssUrl: String = "",
    val category: String = "আন্তর্জাতিক",
    val isActive: Boolean = true,
    val isDefault: Boolean = false
) {
    companion object {
        val defaultSources = listOf(
            ManagedNewspaper(
                id = "src_prothom_alo",
                name = "প্রথম আলো",
                englishName = "Prothom Alo",
                section = "আন্তর্জাতিক (World Desk)",
                webUrl = "https://www.prothomalo.com/world",
                rssUrl = "",
                category = "ভূ-রাজনীতি ও কূটনীতি",
                isActive = true,
                isDefault = true
            ),
            ManagedNewspaper(
                id = "src_daily_star",
                name = "দ্য ডেইলি স্টার",
                englishName = "The Daily Star",
                section = "World / International Desk",
                webUrl = "https://www.thedailystar.net/news/world",
                rssUrl = "",
                category = "আন্তর্জাতিক সম্পর্ক",
                isActive = true,
                isDefault = true
            ),
            ManagedNewspaper(
                id = "src_bonik_barta",
                name = "বণিক বার্তা",
                englishName = "Bonik Barta",
                section = "আন্তর্জাতিক বাণিজ্য ও অর্থনীতি",
                webUrl = "https://bonikbarta.net/international",
                rssUrl = "",
                category = "বৈশ্বিক অর্থনীতি ও বাণিজ্য",
                isActive = true,
                isDefault = true
            ),
            ManagedNewspaper(
                id = "src_dhaka_tribune",
                name = "ঢাকা ট্রিবিউন",
                englishName = "Dhaka Tribune",
                section = "World News Desk",
                webUrl = "https://www.dhakatribune.com/world",
                rssUrl = "",
                category = "বিশ্ব রাজনীতি ও কূটনীতি",
                isActive = true,
                isDefault = true
            )
        )
    }
}
