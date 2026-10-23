package com.example.data

import com.example.data.sync.DailyBcsOverhaulEngine
import com.example.data.time.BangladeshTimeHelper
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Provides comprehensive daily BCS International Affairs study material
 * with authentic coverage of the 4 designated Bangladeshi newspapers:
 * 1. Prothom Alo (প্রথম আলো — আন্তর্জাতিক)
 * 2. The Daily Star (দ্য ডেইলি স্টার — International)
 * 3. Bonik Barta (বণিক বার্তা — আন্তর্জাতিক বিষয়াবলি)
 * 4. Dhaka Tribune (ঢাকা ট্রিবিউন — World / International)
 */
object BcsDataSource {

    /**
     * Calculates Bangladesh Standard Time (BST, UTC+6) formatted in Bangla
     */
    fun getBangladeshCurrentTime(): Pair<String, String> {
        val now = Date()
        val timeZone = TimeZone.getTimeZone("GMT+6")

        val dayFormat = SimpleDateFormat("d", Locale.ENGLISH).apply { this.timeZone = timeZone }
        val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH).apply { this.timeZone = timeZone }
        val yearFormat = SimpleDateFormat("yyyy", Locale.ENGLISH).apply { this.timeZone = timeZone }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH).apply { this.timeZone = timeZone }

        val day = convertToBanglaNumber(dayFormat.format(now))
        val monthEnglish = monthFormat.format(now)
        val monthBangla = when (monthEnglish.lowercase()) {
            "january" -> "জানুয়ারি"
            "february" -> "ফেব্রুয়ারি"
            "march" -> "মার্চ"
            "april" -> "এপ্রিল"
            "may" -> "মে"
            "june" -> "জুন"
            "july" -> "জুলাই"
            "august" -> "আগস্ট"
            "september" -> "সেপ্টেম্বর"
            "october" -> "অক্টোবর"
            "november" -> "নভেম্বর"
            "december" -> "ডিসেম্বর"
            else -> monthEnglish
        }
        val year = convertToBanglaNumber(yearFormat.format(now))
        val rawTime = timeFormat.format(now)
        val timeParts = rawTime.split(" ")
        val formattedTime = "${convertToBanglaNumber(timeParts[0])} ${timeParts.getOrElse(1) { "PM" }} BST"

        return Pair("$day $monthBangla $year", formattedTime)
    }

    fun convertToBanglaNumber(input: String): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Primary daily report for ১৬ অক্টোবর ২০২৬ (BCS-oriented, synthesized from the 4 newspapers)
     */
    val report16Oct2026: DailyReport by lazy {
        DailyReport(
            id = "2026-10-16",
            banglaDate = "১৬ অক্টোবর ২০২৬",
            banglaTime = "৫:৩০ PM BST",
            rawDateIso = "2026-10-16",
            heading = "BCS International Affairs — ১৬ অক্টোবর ২০২৬",
            bulletinVideo = YouTubeSuggestion(
                title = "আজকের বিশ্ব ভূ-রাজনীতি ও আন্তর্জাতিক সম্পর্কের প্রধান ইস্যুসমূহ (Daily BCS Briefing)",
                channelName = "Al Jazeera / DW Geopolitics",
                duration = "14:20",
                videoUrl = "https://www.youtube.com/watch?v=kYJdJjY1VqU",
                description = "লেবানন সীমান্ত সংকট, ব্রিকস শীর্ষ সম্মেলন, আইএমএফের বৈশ্বিক অর্থনৈতিক পূর্বাভাস এবং লোহিত সাগর চোক পয়েন্টের বিশেষ ভিডিও বিশ্লেষণ।"
            ),
            topHeadlines = listOf(
                "লেবানন সীমান্তে UNIFIL ঘাঁটিতে গুলিবর্ষণ ও জাতিসংঘ নিরাপত্তা পরিষদের জরুরি অধিবেশন আহ্বান",
                "১৬তম ব্রিকস (BRICS) শীর্ষ সম্মেলনের আগে লাদাখ সীমান্তে ভারত-চীন যৌথ নজরদারি চুক্তি",
                "আইএমএফ-এর বৈশ্বিক অর্থনৈতিক পূর্বাভাস (WEO Report): বিশ্ব প্রবৃদ্ধি ধীরগতির ইঙ্গিত, এশিয়ায় স্থিতিশীলতা",
                "উত্তর কোরিয়ার পার্লামেন্টে সংবিধান সংশোধন: দক্ষিণ কোরিয়াকে আনুষ্ঠানিকভাবে 'শত্রু রাষ্ট্র' ঘোষণা",
                "ইউরোপীয় ইউনিয়নের কার্বন সীমান্ত সমন্বয় ব্যবস্থা (CBAM) ও উন্নয়নশীল দেশের রপ্তানি ঝুঁকি",
                "লোহিত সাগর ও বাব-এল-মান্দেব প্রণালীতে নৌ-নিরাপত্তা সংকট: সুয়েজ খালের রাজস্ব হ্রাস ও আন্তর্জাতিক পণ্যপরিবহন"
            ),
            dayComparison = DayComparison(
                whatsNew = listOf(
                    "উত্তর কোরিয়া তাদের সংবিধানে দক্ষিণ কোরিয়াকে 'চির শত্রু' হিসেবে তালিকাভুক্ত করেছে (পূর্বে শান্তির সাংবিধানিক অঙ্গীকার ছিল)।",
                    "জাতিসংঘ নিরাপত্তা পরিষদে UNIFIL শান্তিরক্ষী সুরক্ষায় যৌথ বিবৃতি পেশ করেছে ফ্রান্স, ইতালি ও স্পেন।"
                ),
                ongoingIssues = listOf(
                    "দক্ষিণ লেবানন ও গাজায় সামরিক সংঘাত এবং ইসরায়েল-ইরান পাল্টাপাল্টি হুমকির উত্তাপ।",
                    "লোহিত সাগরে হুথিদের বাণিজ্যিক জাহাজে ড্রোন ও মিসাইল আক্রমণের ধারাবাহিকতা।"
                ),
                escalatedIssues = listOf(
                    "ভারত-চীন সীমান্ত উত্তেজনায় চার বছর পর নতুন যৌথ পেট্রোলিং বোঝাপড়ার ঘোষণা দ্বিপাক্ষিক বাণিজ্য ও ব্রিকস বৈঠকে নতুন গতি এনেছে।"
                )
            ),
            topics = listOf(
                TopicAnalysis(
                    id = "t1",
                    title = "লেবানন সীমান্তে UNIFIL ঘাঁটিতে হামলা ও জাতিসংঘ নিরাপত্তা পরিষদের জরুরি অধিবেশন (UNIFIL & UNSC Resolution 1701)",
                    category = "ভূ-রাজনীতি ও আন্তর্জাতিক নিরাপত্তা",
                    importanceRank = 1,
                    todaysEvent = "দক্ষিণ লেবাননে নিয়োজিত জাতিসংঘের অন্তর্বর্তীকালীন শান্তিরক্ষা বাহিনী (UNIFIL)-এর পর্যবেক্ষণ পোস্ট ও ওয়াচটাওয়ারে সামরিক হামলার তীব্র নিন্দা জানিয়ে জাতিসংঘ নিরাপত্তা পরিষদ জরুরি রুদ্ধদ্বার বৈঠক করেছে। নিরাপত্তা পরিষদ সর্বসম্মতভাবে প্রস্তাব ১৭০১ (UNSC Resolution 1701) কঠোরভাবে বাস্তবায়নের তাগিদ দিয়েছে। শান্তিরক্ষীদের ওপর যেকোনো ইচ্ছাকৃত আক্রমণ আন্তর্জাতিক মানবিক আইনের গুরুতর লঙ্ঘন বলে স্পষ্ট হুঁশিয়ারি দেওয়া হয়েছে।",
                    whyImportant = "জাতিসংঘ শান্তিরক্ষা মিশনের ওপর হামলা বৈশ্বিক বহুপাক্ষিক ব্যবস্থার জন্য চরম হুমকি। লেবাননের ব্লু লাইন (Blue Line)-এর নিরাপত্তা বিঘ্নিত হলে সমগ্র মধ্যপ্রাচ্যে সর্বাত্মক আঞ্চলিক যুদ্ধ ছড়িয়ে পড়ার আশঙ্কা রয়েছে। এছাড়া বিশ্ববাজারে অপরিশোধিত তেলের মূল্যে অস্থিরতা তৈরি হয়েছে।",
                    background = "১৯৭৮ সালে লেবানন থেকে ইসরায়েলি সেনা প্রত্যাহার নিশ্চিত করতে নিরাপত্তা পরিষদের প্রস্তাব ৪২৫ ও ৪২৬ এর অধীনে UNIFIL গঠিত হয়। পরবর্তীতে ২০০৬ সালের দ্বিতীয় লেবানন যুদ্ধের অবসান ঘটাতে গৃহীত 'প্রস্তাব ১৭০১' অনুসারে শান্তিরক্ষীদের সংখ্যা ও ম্যান্ডেট বাড়ানো হয়।",
                    importantEntities = ImportantEntities(
                        persons = listOf("আন্তোনিও গুতেরেস (UN Secretary-General)", "নাজিব মিকাতি (লেবাননের তত্ত্বাবধায়ক প্রধানমন্ত্রী)", "বেনিয়ামিন নেতানিয়াহু (ইসরায়েলের প্রধানমন্ত্রী)"),
                        countries = listOf("লেবানন (Lebanon)", "ইসরায়েল (Israel)", "ফ্রান্স (France)", "ইতালি (Italy)", "বাংলাদেশ (Bangladesh)"),
                        organizations = listOf("জাতিসংঘ নিরাপত্তা পরিষদ (UNSC)", "UNIFIL (United Nations Interim Force in Lebanon)"),
                        alliances = listOf("হিজবুল্লাহ (Hezbollah - লেবানন ভিত্তিক শিয়া সশস্ত্র দল)"),
                        institutions = listOf("আন্তর্জাতিক অপরাধ আদালত (ICC)", "আন্তর্জাতিক বিচার আদালত (ICJ)")
                    ),
                    importantFacts = listOf(
                        FactItem("প্রতিষ্ঠা ও ম্যান্ডেট", "UNIFIL", "প্রতিষ্ঠিত: ১৯৭৮ সাল (UNSC Res 425 & 426)"),
                        FactItem("মূল রেজোলিউশন", "UNSC Res 1701", "২০০৬ সালে গৃহীত; লিটানি নদীর দক্ষিণে কেবল লেবানিজ সেনাবাহিনী ও UNIFIL মোতায়েনের বিধান"),
                        FactItem("সীমানা নির্ধারণ", "ব্লু লাইন (Blue Line)", "২০০০ সালে জাতিসংঘ কর্তৃক নির্ধারিত লেবানন ও ইসরায়েল/গোলান হাইটস বিভক্তকারী রেখা"),
                        FactItem("সদর দপ্তর", "নাকুরা (Naqoura)", "দক্ষিণ লেবাননে অবস্থিত UNIFIL প্রধান কার্যালয়"),
                        FactItem("কোর ট্রুপ কন্ট্রিবিউটর", "ইউরোপ ও এশিয়া", "ইতালি, ফ্রান্স, ইন্দোনেশিয়া, ভারত, ঘানা ও বাংলাদেশ অন্যতম শান্তিরক্ষী প্রেরণকারী দেশ")
                    ),
                    bangladeshRelevance = "বাংলাদেশ জাতিসংঘ শান্তিরক্ষা কার্যক্রমে বিশ্বের শীর্ষ ট্রুপ কন্ট্রিবিউটিং দেশগুলোর একটি (Leading TCC)। লেবাননে বাংলাদেশ নৌবাহিনীর যুদ্ধজাহাজ (BNS Sangram / BNS Bijoy) মেরিটাইম টাস্কফোর্স (MTF)-এ নিয়োজিত রয়েছে। এছাড়া মধ্যপ্রাচ্যে অবস্থানরত লক্ষাধিক প্রবাসী বাংলাদেশির রেমিট্যান্স ও কর্মসংস্থান এই সংকটে গভীরভাবে সম্পৃক্ত।",
                    bcsTakeaways = listOf(
                        "UNIFIL-এর পূর্ণরূপ: United Nations Interim Force in Lebanon (প্রতিষ্ঠা: ১৯৭৮)।",
                        "ব্লু লাইন (Blue Line) কী? লেবানন ও ইসরায়েলের মধ্যবর্তী জাতিসংঘের ডিমার্কেশন লাইন।",
                        "নিরাপত্তা পরিষদের প্রস্তাব ১৭০১ (Res 1701) কোন দেশ সংক্রান্ত? লেবানন ও ইসরায়েল যুদ্ধবিরতি (২০০৬)।",
                        "লিটানি নদী (Litani River) কোন দেশে অবস্থিত? লেবাননে।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q1_1",
                                questionNumber = 1,
                                question = "জাতিসংঘের অন্তর্বর্তীকালীন শান্তিরক্ষা বাহিনী (UNIFIL) কত সালে প্রতিষ্ঠিত হয়?",
                                options = listOf("১৯৪৮", "১৯৬৭", "১৯৭৮", "২০০৬"),
                                correctIndex = 2,
                                explanation = "১৯৭৮ সালে জাতিসংঘ নিরাপত্তা পরিষদের প্রস্তাব ৪২৫ ও ৪২৬ অনুসারে লেবাননে UNIFIL গঠিত হয়।"
                            ),
                            McqQuestion(
                                id = "q1_2",
                                questionNumber = 2,
                                question = "জাতিসংঘ নির্ধারিত 'ব্লু লাইন' (Blue Line) কোন দুটি ভূখণ্ডের সীমান্ত নির্দেশ করে?",
                                options = listOf("সিরিয়া ও জর্ডান", "লেবানন ও ইসরায়েল", "মিশর ও গাজা", "ইরাক ও কুয়েত"),
                                correctIndex = 1,
                                explanation = "২০০০ সালে লেবানন থেকে ইসরায়েলি প্রত্যাহার নিশ্চিত করতে জাতিসংঘ ব্লু লাইন চিহ্নিত করে।"
                            ),
                            McqQuestion(
                                id = "q1_3",
                                questionNumber = 3,
                                question = "জাতিসংঘ নিরাপত্তা পরিষদের 'প্রস্তাব ১৭০১' (UNSC Resolution 1701) কোন বিষয়ের সাথে সম্পর্কিত?",
                                options = listOf("সাইপ্রাস সংকট", "২০০৬ সালের লেবানন যুদ্ধবিরতি", "সুদান শান্তিচুক্তি", "ইউক্রেন শান্তি পরিকল্পনা"),
                                correctIndex = 1,
                                explanation = "২০০৬ সালে হিজবুল্লাহ ও ইসরায়েলের মধ্যে সংঘাত বন্ধের লক্ষ্যে নিরাপত্তা পরিষদ রেজোলিউশন ১৭০১ পাস করে।"
                            )
                        ),
                        shortQuestions = listOf(
                            "UNIFIL-এর সদর দপ্তর কোথায় অবস্থিত এবং এর বর্তমান ম্যান্ডেট কী?",
                            "লেবাননের ভৌগোলিক সীমানায় 'লিটানি নদী'-র কৌশলগত গুরুত্ব লিখুন।"
                        ),
                        analyticalQuestion = "মধ্যপ্রাচ্যের চলমান ভূ-রাজনৈতিক মেরুকরণে জাতিসংঘ শান্তিরক্ষা কার্যক্রমের ভূমিকা ও দুর্বলতা বিশ্লেষণ করুন। এই সংকটে বাংলাদেশের জাতীয় স্বার্থ কীভাবে প্রভাবিত হতে পারে?"
                    ),
                    reportedBy = listOf("প্রথম আলো", "The Daily Star", "ঢাকা ট্রিবিউন"),
                    youtubeVideo = YouTubeSuggestion(
                        title = "What is UNIFIL and why is it in Southern Lebanon? | Explainer",
                        channelName = "Al Jazeera English",
                        duration = "7:45",
                        videoUrl = "https://www.youtube.com/watch?v=kYJdJjY1VqU",
                        description = "জাতিসংঘ শান্তিরক্ষা মিশন UNIFIL, ব্লু লাইন (Blue Line) এবং রেজোলিউশন ১৭০১ এর ঐতিহাসিক প্রেক্ষাপট।"
                    )
                ),
                TopicAnalysis(
                    id = "t2",
                    title = "১৬তম ব্রিকস সম্মেলনের প্রাক্কালে ভারত-চীন সীমান্ত সমঝোতা (India-China Border Agreement & LAC Patrol)",
                    category = "দ্বিপাক্ষিক সম্পর্ক ও আঞ্চলিক কূটনীতি",
                    importanceRank = 2,
                    todaysEvent = "রাশিয়ার কাজানে অনুষ্ঠিতব্য ১৬তম ব্রিকস (BRICS) শীর্ষ সম্মেলনের ঠিক প্রাক্কালে পূর্ব লাদাখের প্রকৃত নিয়ন্ত্রণ রেখা (LAC)-তে সামরিক নজরদারি ও টহল নিয়ে ভারত ও চীনের মধ্যে এক ঐতিহাসিক সমঝোতা চূড়ান্ত হয়েছে। ২০২০ সালের গালওয়ান উপত্যকার রক্তক্ষয়ী সংঘর্ষের পর এই প্রথম উভয় দেশ ডেপসাং ও ডেমচক অঞ্চলে সংঘাত নিরসনে সম্মত হলো। কূটনৈতিক বিশ্লেষকরা এটিকে হিমালয় অঞ্চলে দীর্ঘ চার বছরের অচলাবস্থার বড় ধরনের বরফগলা হিসেবে দেখছেন।",
                    whyImportant = "বিশ্বের দুই সর্বাধিক জনসংখ্যার পরাশক্তি এবং পরমাণু শক্তিধর প্রতিবেশী দেশের সীমান্ত উত্তেজনা প্রশমন গোটা এশিয়া-প্রশান্ত মহাসাগরীয় অঞ্চলের ভূ-রাজনৈতিক ভারসাম্যে তাৎপর্যপূর্ণ প্রভাব ফেলবে। এটি গ্লোবাল সাউথ (Global South) ও ব্রিকস জোটে নতুন গতিশীলতা আনবে।",
                    background = "ভারত ও চীনের মধ্যে প্রায় ৩,৪৮৮ কিলোমিটার দীর্ঘ সীমান্ত রয়েছে, যা প্রকৃত নিয়ন্ত্রণ রেখা (Line of Actual Control - LAC) নামে পরিচিত। ২০২০ সালের জুনে লাদাখের গালওয়ান উপত্যকায় সংঘর্ষে ২০ ভারতীয় সেনা এবং ৪ চীনা সেনা নিহত হওয়ার পর দুই দেশের বাণিজ্য ও কূটনৈতিক সম্পর্কে তীব্র শৈত্যপ্রবাহ দেখা দেয়।",
                    importantEntities = ImportantEntities(
                        persons = listOf("নরেন্দ্র মোদী (ভারতের প্রধানমন্ত্রী)", "শি চিনপিং (চীনের প্রেসিডেন্ট)", "এস জয়শঙ্কর (ভারতের পররাষ্ট্রমন্ত্রী)", "ওয়াং ই (চীনের পররাষ্ট্রমন্ত্রী)"),
                        countries = listOf("ভারত (India)", "চীন (China)", "রাশিয়া (Russia)"),
                        organizations = listOf("BRICS (ব্রিকস)", "SCO (সাংহাই সহযোগিতা সংস্থা)"),
                        alliances = listOf("Quad (কোয়াড — ভারত, যুক্তরাষ্ট্র, জাপান, অস্ট্রেলিয়া)"),
                        institutions = listOf("New Development Bank (NDB - ব্রিকস ব্যাংক)")
                    ),
                    importantFacts = listOf(
                        FactItem("সীমান্ত রেখা", "LAC", "Line of Actual Control (দৈর্ঘ্য: প্রায় ৩,৪৮৮ কিমি)"),
                        FactItem("ঐতিহাসিক সংঘাত", "গালওয়ান উপত্যকা (Galwan Valley)", "২০২০ সালের ১৫ জুন ভারত ও চীনের মধ্যে সংঘর্ষ সংঘটিত হয়"),
                        FactItem("আলোচিত অঞ্চল", "লাদাখ ও আকসাই চীন", "পূর্ব লাদাখের ডেপসাং (Depsang) ও ডেমচক (Demchok) কৌশলগত পয়েন্ট"),
                        FactItem("ম্যাকমোহন লাইন", "McMahon Line", "১৯১৪ সালের সিমলা চুক্তিতে নির্ধারিত ভারত (অরুণাচল) ও তিব্বত সীমান্ত"),
                        FactItem("আসন্ন সম্মেলন", "১৬তম ব্রিকস শীর্ষ সম্মেলন", "স্থান: কাজান (Kazan), তাতারস্তান, রাশিয়া")
                    ),
                    bangladeshRelevance = "বাংলাদেশ ভারত ও চীন উভয়েরই অত্যন্ত ঘনিষ্ঠ উন্নয়ন ও অর্থনৈতিক অংশীদার। চীন বাংলাদেশের বৃহত্তম বাণিজ্যিক অংশীদার এবং ভারত নিকটতম প্রতিবেশী ও দ্বিতীয় বৃহত্তম আমদানি উৎস। এ দুই এশীয় পরাশক্তির সম্পর্কের স্থিতিশীলতা বাংলাদেশের ভূ-রাজনৈতিক নিরপেক্ষতা বজায় রাখতে এবং তিস্তা প্রকল্প ও বাণিজ্যিক করিডোর বাস্তবায়নে অত্যন্ত ইতিবাচক সুযোগ তৈরি করে।",
                    bcsTakeaways = listOf(
                        "ভারত ও চীনের মধ্যবর্তী সীমারেখার নাম: LAC (Line of Actual Control) এবং ম্যাকমোহন লাইন (অরুণাচল অংশে)।",
                        "গালওয়ান উপত্যকা কোথায় অবস্থিত? পূর্ব লাদাখে।",
                        "ব্রিকস (BRICS)-এর নতুন সদস্য দেশসমূহ (২০২৪ সালে কার্যকর): মিসর, ইথিওপিয়া, ইরান, সংযুক্ত আরব আমিরাত।",
                        "১৬তম ব্রিকস শীর্ষ সম্মেলন কোথায় অনুষ্ঠিত হচ্ছে? কাজান, রাশিয়া।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q2_1",
                                questionNumber = 4,
                                question = "ভারত ও চীনের মধ্যবর্তী লাদাখ অঞ্চলের সীমারেখাকে কী বলা হয়?",
                                options = listOf("Line of Control (LoC)", "Line of Actual Control (LAC)", "Durand Line", "Radcliffe Line"),
                                correctIndex = 1,
                                explanation = "ভারত ও চীনের মধ্যকার অনানুষ্ঠানিক কার্যকর সীমারেখা হলো LAC (Line of Actual Control)।"
                            ),
                            McqQuestion(
                                id = "q2_2",
                                questionNumber = 5,
                                question = "২০২৪ সালে ব্রিকস (BRICS)-এ আনুষ্ঠানিকভাবে যোগ দেওয়া নতুন সদস্য দেশ কোনটি নয়?",
                                options = listOf("ইরান", "ইথিওপিয়া", "সংযুক্ত আরব আমিরাত", "আর্জেন্টিনা"),
                                correctIndex = 3,
                                explanation = "আর্জেন্টিনা শুরুতে আমন্ত্রিত হলেও নতুন প্রেসিডেন্ট হাভিয়ের মিলের সিদ্ধান্ত অনুযায়ী যোগদান বাতিল করে।"
                            ),
                            McqQuestion(
                                id = "q2_3",
                                questionNumber = 6,
                                question = "ঐতিহাসিক 'ম্যাকমোহন লাইন' (McMahon Line) কত সালের সিমলা কনভেনশনে নির্ধারিত হয়?",
                                options = listOf("১৯০৫", "১৯১৪", "১৯৪৭", "১৯৬২"),
                                correctIndex = 1,
                                explanation = "১৯১৪ সালের সিমলা কনভেনশনে ব্রিটিশ ভারত ও তিব্বতের মধ্যে ম্যাকমোহন লাইন স্বাক্ষরিত হয়।"
                            )
                        ),
                        shortQuestions = listOf(
                            "LAC এবং LoC-এর মধ্যে মৌলিক পার্থক্য কী?",
                            "ব্রিকস (BRICS) প্লাস সম্প্রসারণের ভূ-অর্থনৈতিক গুরুত্ব কী?"
                        ),
                        analyticalQuestion = "ভারত ও চীনের সীমান্ত বিরোধ নিরসনের প্রচেষ্টা দক্ষিণ এশিয়ার আঞ্চলিক রাজনীতি এবং বাংলাদেশের ভারসাম্যের পররাষ্ট্রনীতিতে (Balanced Foreign Policy) কী ধরনের সুযোগ ও চ্যালেঞ্জ বয়ে আনতে পারে?"
                    ),
                    reportedBy = listOf("প্রথম আলো", "বণিক বার্তা", "The Daily Star"),
                    youtubeVideo = YouTubeSuggestion(
                        title = "India-China Reach LAC Patrolling Pact Ahead of BRICS Summit",
                        channelName = "WION / Gravitas",
                        duration = "9:15",
                        videoUrl = "https://www.youtube.com/watch?v=p23qUu8m0K8",
                        description = "লাদাখের প্রকৃত নিয়ন্ত্রণ রেখা (LAC), ডেপসাং ও ডেমচক টহল সমঝোতা ও ব্রিকস কূটনীতি।"
                    )
                ),
                TopicAnalysis(
                    id = "t3",
                    title = "আইএমএফ-এর ওয়ার্ল্ড ইকোনমিক আউটলুক (IMF World Economic Outlook - WEO 2026) ও বৈশ্বিক প্রবৃদ্ধি",
                    category = "বৈশ্বিক অর্থনীতি ও বাণিজ্য",
                    importanceRank = 3,
                    todaysEvent = "আন্তর্জাতিক মুদ্রা তহবিল (IMF) তাদের বার্ষিক ওয়ার্ল্ড ইকোনমিক আউটলুক (WEO) প্রকাশ করেছে। প্রতিবেদনে ২০২৬ সালে বৈশ্বিক অর্থনৈতিক প্রবৃদ্ধির হার ৩.২ শতাংশে অপরিবর্তিত থাকার পূর্বাভাস দেওয়া হয়েছে। বিশ্বজুড়ে উচ্চ সুদহার, ভূ-রাজনৈতিক সংঘাতের কারণে বাণিজ্য খণ্ডায়ন (Geo-economic Fragmentation) এবং ঋণের উচ্চ বোঝাকে টেকসই প্রবৃদ্ধির প্রধান বাধা হিসেবে চিহ্নিত করা হয়েছে। তবে দক্ষিণ এশিয়া এখনও বিশ্বের দ্রুততম বর্ধনশীল অঞ্চল হিসেবে অবস্থান ধরে রেখেছে।",
                    whyImportant = "বৈশ্বিক বাণিজ্যনীতি, সুদের হার ও অর্থনৈতিক প্রবৃদ্ধির দিকনির্দেশনা জানতে ডব্লিউইও রিপোর্ট বিশ্বব্যাপী নীতিনির্ধারকদের প্রধান ভিত্তি। উন্নয়নশীল দেশগুলোর মুদ্রা বিনিময় হার ও মূল্যস্ফীতি নিয়ন্ত্রণে আইএমএফের সুপারিশ অত্যন্ত কার্যকর।",
                    background = "১৯৪৪ সালের জুলাই মাসে অনুষ্ঠিত ঐতিহাসিক ব্রেটন উডস কনফারেন্সে (Bretton Woods Conference) আইএমএফ এবং বিশ্বব্যাংক প্রতিষ্ঠার সিদ্ধান্ত হয়। ১৯৪৫ সালের ২৭ ডিসেম্বর আইএমএফ আনুষ্ঠানিকভাবে যাত্রা শুরু করে। প্রতি বছর বসন্ত ও শরৎকালে আইএমএফ WEO প্রকাশ করে।",
                    importantEntities = ImportantEntities(
                        persons = listOf("ক্রিস্টালিনা জর্জিয়েভা (Managing Director, IMF)", "পিয়েরে-অলিভিয়ের গোরিঞ্চাস (Chief Economist, IMF)"),
                        countries = listOf("যুক্তরাষ্ট্র (USA)", "চীন (China)", "ভারত (India)", "বাংলাদেশ (Bangladesh)"),
                        organizations = listOf("আন্তর্জাতিক মুদ্রা তহবিল (IMF)", "বিশ্বব্যাংক গ্রুপ (World Bank Group)"),
                        alliances = listOf("G7", "G20"),
                        institutions = listOf("ব্রেটন উডস প্রতিষ্ঠানসমূহ (Bretton Woods Twins)")
                    ),
                    importantFacts = listOf(
                        FactItem("প্রতিষ্ঠা ও কার্যকর", "IMF", "চুক্তি স্বাক্ষর: ১৯৪৪ (ব্রেটন উডস), কার্যকর: ২৭ ডিসেম্বর ১৯৪৫"),
                        FactItem("সদর দপ্তর", "ওয়াশিংটন ডিসি", "যুক্তরাষ্ট্র (Washington, D.C.)"),
                        FactItem("সদস্য সংখ্যা", "১৯০টি দেশ", "সর্বশেষ সদস্য দেশ: অ্যান্ডোরা (Andorra - ২০২০)"),
                        FactItem("মুদ্রা ঝুড়ি", "SDR (Special Drawing Rights)", "মুদ্রা: USD, EUR, CNY, JPY, GBP (৫টি মুদ্রা)"),
                        FactItem("প্রকাশনা", "WEO ও GFSR", "World Economic Outlook এবং Global Financial Stability Report")
                    ),
                    bangladeshRelevance = "বাংলাদেশ বর্তমানে আইএমএফের ৪.৭ বিলিয়ন ডলারের ঋণ কর্মসূচির অধীনে রয়েছে। আইএমএফের শর্তানুসারে বাংলাদেশ ব্যাংক সুদের হার বাজারভিত্তিক করেছে, ক্রলিং পেগ (Crawling Peg) ব্যবস্থার মাধ্যমে ডলারের দাম নির্ধারণ করেছে এবং কর-জিডিপি অনুপাত বাড়ানোর সংস্কার চালাচ্ছে। বৈশ্বিক অর্থনৈতিক মন্দা বাংলাদেশের তৈরি পোশাক (RMG) রপ্তানি ও রেমিট্যান্স প্রবাহকে সরাসরি প্রভাবিত করে।",
                    bcsTakeaways = listOf(
                        "ব্রেটন উডস টুইনস (Bretton Woods Twins) বলা হয় কাদের? IMF ও World Bank-কে।",
                        "IMF-এর বর্তমান সদস্য দেশ সংখ্যা কত? ১৯০টি (সর্বশেষ: অ্যান্ডোরা)।",
                        "SDR-এর ঝুড়িতে অন্তর্ভুক্ত মুদ্রার সংখ্যা কতটি? ৫টি (মার্কিন ডলার, ইউরো, চীনা ইউয়ান, জাপানি ইয়েন, ব্রিটিশ পাউন্ড)।",
                        "World Economic Outlook (WEO) কোন সংস্থা প্রকাশ করে? IMF।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q3_1",
                                questionNumber = 7,
                                question = "আন্তর্জাতিক মুদ্রা তহবিল (IMF) আনুষ্ঠানিকভাবে কোন সালে কার্যক্রম শুরু করে?",
                                options = listOf("১৯৪৪", "১৯৪৫", "১৯৪৮", "১৯৫০"),
                                correctIndex = 1,
                                explanation = "১৯৪৪ সালে ব্রেটন উডস সম্মেলনে চুক্তি হলেও ১৯৪৫ সালের ২৭ ডিসেম্বর আনুষ্ঠানিকভাবে IMF কার্যকর হয়।"
                            ),
                            McqQuestion(
                                id = "q3_2",
                                questionNumber = 8,
                                question = "আইএমএফ-এর স্পেশাল ড্রয়িং রাইটস (SDR) বাস্কেটে কোন মুদ্রাটি ২০১৬ সালে সর্বশেষ যুক্ত হয়?",
                                options = listOf("জাপানি ইয়েন", "চীনা রেনমিনবি (ইউয়ান)", "সুইস ফ্রাঙ্ক", "কানাডিয়ান ডলার"),
                                correctIndex = 1,
                                explanation = "২০১৬ সালের ১ অক্টোবর চীনা মুদ্রা রেনমিনবি (ইউয়ান) এসডিআর কারেন্সি বাস্কেটে যুক্ত হয়।"
                            ),
                            McqQuestion(
                                id = "q3_3",
                                questionNumber = 9,
                                question = "আইএমএফ-এর ১৯০তম (সর্বশেষ) সদস্য রাষ্ট্র কোনটি?",
                                options = listOf("দক্ষিণ সুদান", "নাউরু", "অ্যান্ডোরা", "পালাউ"),
                                correctIndex = 2,
                                explanation = "২০২০ সালের অক্টোবরে অ্যান্ডোরা আইএমএফ-এর ১৯০তম সদস্য পদ লাভ করে।"
                            )
                        ),
                        shortQuestions = listOf(
                            "ব্রেটন উডস কনফারেন্সের মাধ্যমে সৃষ্ট দুটি প্রধান প্রতিষ্ঠানের নাম ও উদ্দেশ্য লিখুন।",
                            "SDR (Special Drawing Rights) কী এবং এর বাস্কেটে কোন ৫টি মুদ্রা রয়েছে?"
                        ),
                        analyticalQuestion = "আইএমএফের ঋণ শর্তাবলি উন্নয়নশীল অর্থনীতির সামষ্টিক স্থিতিশীলতা নিশ্চিত করে নাকি অভ্যন্তরীণ মূল্যস্ফীতি ও জনদুর্ভোগ বৃদ্ধি করে? বাংলাদেশের সাম্প্রতিক অর্থনৈতিক সংস্কারের আলোকে পর্যালোচনা করুন।"
                    ),
                    reportedBy = listOf("বণিক বার্তা", "প্রথম আলো", "ঢাকা ট্রিবিউন", "The Daily Star"),
                    youtubeVideo = YouTubeSuggestion(
                        title = "IMF World Economic Outlook: Steady Global Growth Amid Divergent Trajectories",
                        channelName = "IMF (International Monetary Fund)",
                        duration = "5:30",
                        videoUrl = "https://www.youtube.com/watch?v=3g8KkL_F7_U",
                        description = "আইএমএফের ওয়ার্ল্ড ইকোনমিক আউটলুক (WEO) ২০২৬ ও বৈশ্বিক অর্থনৈতিক প্রবৃদ্ধির পূর্বাভাস।"
                    )
                ),
                TopicAnalysis(
                    id = "t4",
                    title = "উত্তর কোরিয়ার সংবিধান সংশোধন ও দুই কোরিয়া একত্রীকরণ নীতি বাতিল (North Korea Amends Constitution)",
                    category = "পূর্ব এশীয় ভূ-রাজনীতি ও পারমাণবিক নিরস্ত্রীকরণ",
                    importanceRank = 4,
                    todaysEvent = "উত্তর কোরিয়ার সর্বোচ্চ গণপরিষদ (Supreme People's Assembly) দেশের সংবিধানে বড় ধরনের সংশোধন এনে দক্ষিণ কোরিয়াকে আনুষ্ঠানিকভাবে 'চির শত্রু রাষ্ট্র' (Invariable Principal Enemy) হিসেবে আখ্যায়িত করেছে। একই সাথে সাবেক নেতা কিম ইল সাং-এর সময় থেকে চলে আসা শান্তি ও দুই কোরিয়া পুনরেকত্রীকরণ সংক্রান্ত সকল সাংবিধানিক ধারা বিলুপ্ত করা হয়েছে। এছাড়া দুই দেশকে সংযুক্তকারী সড়ক ও রেলপথের সংযোগস্থল বিস্ফোরক দিয়ে উড়িয়ে দিয়েছে পিয়ংইয়ং।",
                    whyImportant = "কোরীয় উপদ্বীপে দীর্ঘ কয়েক দশকের পুনরেকত্রীকরণ (Reunification) আশা আনুষ্ঠানিকভাবে ভেঙে গেল। এর ফলে ৩৮তম অক্ষরেখায় (38th Parallel) সামরিক সংঘাত ও পারমাণবিক যুদ্ধাবস্থার ঝুঁকি বহুগুণ বেড়ে গেল। এটি যুক্তরাষ্ট্র, জাপান ও দক্ষিণ কোরিয়ার ত্রিদেশীয় নিরাপত্তা অক্ষকে আরও জোরদার করবে।",
                    background = "১৯৫০-১৯৫৩ সালের কোরীয় যুদ্ধের পর কোনো আনুষ্ঠানিক শান্তি চুক্তি স্বাক্ষরিত হয়নি; বরং একটি যুদ্ধবিরতি চুক্তি (Armistice Agreement) স্বাক্ষরিত হয়েছিল। দুই কোরিয়াকে পৃথক করেছে ডিমিলিটারাইজড জোন (DMZ) ও ৩৮তম সমান্তরাল রেখা।",
                    importantEntities = ImportantEntities(
                        persons = listOf("কিম জং উন (Supreme Leader, DPRK)", "ইউন সুক ইওল (President, South Korea)"),
                        countries = listOf("উত্তর কোরিয়া (DPRK)", "দক্ষিণ কোরিয়া (ROK)", "যুক্তরাষ্ট্র (USA)", "চীন (China)", "রাশিয়া (Russia)"),
                        organizations = listOf("আন্তর্জাতিক পরমাণু শক্তি সংস্থা (IAEA)", "জাতিসংঘ নিরাপত্তা পরিষদ"),
                        alliances = listOf("যুক্তরাষ্ট্র-দক্ষিণ কোরিয়া পারস্পরিক প্রতিরক্ষা চুক্তি (১৯৫৩)"),
                        institutions = listOf("সুপ্রিম পিপলস অ্যাসেম্বলি (উত্তর কোরিয়া পার্লামেন্ট)")
                    ),
                    importantFacts = listOf(
                        FactItem("বিভাজন রেখা", "৩৮তম সমান্তরাল রেখা", "38th Parallel - উত্তর ও দক্ষিণ কোরিয়া বিভক্তকারী ঐতিহাসিক রেখা"),
                        FactItem("সামরিক মুক্ত এলাকা", "DMZ", "Demilitarized Zone (দৈর্ঘ্য: প্রায় ২৫০ কিমি, প্রস্থ: ৪ কিমি)"),
                        FactItem("ঐতিহাসিক যুদ্ধ", "কোরীয় যুদ্ধ (Korean War)", "১৯৫০–১৯৫৩ (১৯৫৩ সালের ২৭ জুলাই পানমুনজমে যুদ্ধবিরতি স্বাক্ষরিত)"),
                        FactItem("রাজধানী", "পিয়ংইয়ং ও সিউল", "উত্তর কোরিয়ার রাজধানী পিয়ংইয়ং; দক্ষিণ কোরিয়ার রাজধানী সিউল"),
                        FactItem("কূটনৈতিক চুক্তি", "পারমাণবিক নন-প্রলিফারেশন ট্রিটি (NPT)", "উত্তর কোরিয়া ২০০৩ সালে NPT থেকে আনুষ্ঠানিকভাবে প্রত্যাহার করে নেয়")
                    ),
                    bangladeshRelevance = "দক্ষিণ কোরিয়ায় ইপিএস (Employment Permit System)-এর অধীনে হাজার হাজার বাংলাদেশি কর্মী কর্মরত আছেন, যারা বছরে কোটি কোটি ডলার রেমিট্যান্স পাঠান। কোরীয় উপদ্বীপে যুদ্ধাবস্থা দেখা দিলে বাংলাদেশের প্রবাসী শ্রমিকদের নিরাপত্তা এবং কোরিয়া থেকে বিদেশি প্রত্যক্ষ বিনিয়োগ (FDI) ক্ষতিগ্রস্ত হতে পারে। এছাড়া বাংলাদেশ শান্তিপূর্ণ কূটনীতি ও পারমাণবিক নিরস্ত্রীকরণের (CTBT/TPNW) দৃঢ় সমর্থক।",
                    bcsTakeaways = listOf(
                        "উত্তর ও দক্ষিণ কোরিয়াকে বিভক্তকারী অক্ষরেখার নাম: ৩৮তম সমান্তরাল রেখা (38th Parallel)।",
                        "পানমুনজম (Panmunjom) কী? দুই কোরিয়ার মধ্যবর্তী যুদ্ধবিরতি গ্রাম / শান্তি গ্রাম।",
                        "উত্তর কোরিয়া কত সালে NPT (Non-Proliferation Treaty) ত্যাগ করে? ২০০৩ সালে।",
                        "উত্তর কোরিয়ার পার্লামেন্টের নাম কী? সুপ্রিম পিপলস অ্যাসেম্বলি।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q4_1",
                                questionNumber = 10,
                                question = "উত্তর কোরিয়া ও দক্ষিণ কোরিয়ার মধ্যবর্তী আন্তর্জাতিক সীমারেখা কোনটি?",
                                options = listOf("১৭তম অক্ষরেখা", "২৪তম সমান্তরাল", "৩৮তম সমান্তরাল রেখা", "৪৯তম সমান্তরাল রেখা"),
                                correctIndex = 2,
                                explanation = "৩৮তম সমান্তরাল অক্ষ রেখা উত্তর কোরিয়া এবং দক্ষিণ কোরিয়াকে বিভক্ত করেছে।"
                            ),
                            McqQuestion(
                                id = "q4_2",
                                questionNumber = 11,
                                question = "কোরীয় যুদ্ধের অবসান ঘটে ১৯৫৩ সালে যে যুদ্ধবিরতি চুক্তি স্বাক্ষরের মাধ্যমে, সেই ঐতিহাসিক স্থানটির নাম কী?",
                                options = listOf("পানমুনজম (Panmunjom)", "ইনছন", "বুসান", "কেসং"),
                                correctIndex = 0,
                                explanation = "১৯৫৩ সালের ২৭ জুলাই ঐতিহাসিক পানমুনজমে কোরীয় যুদ্ধবিরতি চুক্তি স্বাক্ষরিত হয়।"
                            ),
                            McqQuestion(
                                id = "q4_3",
                                questionNumber = 12,
                                question = "উত্তর কোরিয়া কোন সালে পারমাণবিক অস্ত্র বিস্তার রোধ চুক্তি (NPT) থেকে আনুষ্ঠানিকভাবে নিজেকে প্রত্যাহার করে নেয়?",
                                options = listOf("১৯৯৮", "২০০১", "২০০৩", "২০০৬"),
                                correctIndex = 2,
                                explanation = "উত্তর কোরিয়া ২০০৩ সালের জানুয়ারিতে আনুষ্ঠানিকভাবে এনপিটি চুক্তি ত্যাগ করে এবং ২০০৬ সালে প্রথম পারমাণবিক পরীক্ষা চালায়।"
                            )
                        ),
                        shortQuestions = listOf(
                            "কোরীয় যুদ্ধবিরতি চুক্তি (Armistice of 1953) এবং পূর্ণাঙ্গ শান্তি চুক্তির মধ্যে পার্থক্য কী?",
                            "পানমুনজম ঘোষণা (Panmunjom Declaration) কী ছিল?"
                        ),
                        analyticalQuestion = "কোরীয় উপদ্বীপে দুই কোরিয়া পুনরেকত্রীকরণ নীতি বর্জন পূর্ব এশিয়ার পরাশক্তিগুলোর নিরাপত্তা সমীকরণে কী ধরনের পরিবর্তন আনতে পারে? ব্যাখ্যা করুন।"
                    ),
                    reportedBy = listOf("The Daily Star", "ঢাকা ট্রিবিউন", "প্রথম আলো")
                ),
                TopicAnalysis(
                    id = "t5",
                    title = "ইইউ কার্বন সীমান্ত সমন্বয় ব্যবস্থা (EU Carbon Border Adjustment Mechanism - CBAM) ও জলবায়ু কূটনীতি",
                    category = "পরিবেশ, জলবায়ু কূটনীতি ও আন্তর্জাতিক বাণিজ্য",
                    importanceRank = 5,
                    todaysEvent = "ইউরোপীয় ইউনিয়ন (EU) তাদের বহুল আলোচিত কার্বন বর্ডার অ্যাডজাস্টমেন্ট মেকানিজম (CBAM)-এর পূর্ণ বাস্তবায়ন সময়সীমার প্রস্তুতি জোরদার করেছে। ২০২৬ সাল থেকে ইইউভুক্ত দেশগুলোতে লোহা, ইস্পাত, সিমেন্ট, অ্যালুমিনিয়াম ও সার আমদানির ওপর পণ্য উৎপাদনে ব্যবহৃত কার্বন নিঃসরণের সমপরিমাণ শুল্ক (Carbon Tax) কার্যকর হতে যাচ্ছে। ব্রাসেলস স্পষ্ট জানিয়েছে, জলবায়ু লক্ষ্যমাত্রা অর্জনে কার্বন লিকেজ (Carbon Leakage) ঠেকাতে এই পদক্ষেপে কোনো ছাড় দেওয়া হবে না।",
                    whyImportant = "CBAM হলো বিশ্বের প্রথম বহুপাক্ষিক কার্বন শুল্ক ব্যবস্থা। এটি বিশ্ব বাণিজ্য সংস্থার (WTO) নীতিমালার সাথে সাংঘর্ষিক কিনা তা নিয়ে গ্লোবাল সাউথ ও ব্রিকসভুক্ত দেশগুলোর মধ্যে তীব্র অসন্তোষ চলছে। এটি উন্নয়নশীল দেশগুলোর শিল্পায়নের খরচ বাড়িয়ে তুলবে।",
                    background = "ইউরোপীয় গ্রিন ডিল (European Green Deal)-এর অংশ হিসেবে ২০৩০ সালের মধ্যে ৫৫% কার্বন নির্গমন হ্রাস এবং ২০৫০ সালের মধ্যে কার্বন নিরপেক্ষ (Net Zero) হওয়ার লক্ষ্যমাত্রা নিয়ে ২০২১ সালে ইইউ এই প্রস্তাব উত্থাপন করে। ২০২৩ সালের ১ অক্টোবর থেকে এর ট্রানজিশনাল বা রূপান্তরকালীন রিপোর্টিং পর্ব শুরু হয়।",
                    importantEntities = ImportantEntities(
                        persons = listOf("উরসুলা ভন ডার লিয়েন (European Commission President)", "ভপকে হোয়েকস্ট্রা (EU Climate Commissioner)"),
                        countries = listOf("ইইউভুক্ত ২৭ দেশ", "চীন (China)", "ভারত (India)", "বাংলাদেশ (Bangladesh)"),
                        organizations = listOf("ইউরোপীয় ইউনিয়ন (EU)", "বিশ্ব বাণিজ্য সংস্থা (WTO)", "UNFCCC"),
                        alliances = listOf("কপ (COP - Conference of the Parties)"),
                        institutions = listOf("European Green Deal Directorate")
                    ),
                    importantFacts = listOf(
                        FactItem("চুক্তির পূর্ণরূপ", "CBAM", "Carbon Border Adjustment Mechanism"),
                        FactItem("মূল লক্ষ্য", "Fit for 55", "২০৩০ সালের মধ্যে ইইউ-তে ৫৫% নির্গমন হ্রাস ও ২০৫০ সালের মধ্যে Net Zero"),
                        FactItem("অন্তর্ভুক্ত খাতসমূহ", "৬টি মূল খাত", "লোহা ও ইস্পাত, সিমেন্ট, অ্যালুমিনিয়াম, সার, বিদ্যুৎ ও হাইড্রোজেন"),
                        FactItem("জলবায়ু চুক্তি", "প্যারিস চুক্তি (Paris Agreement)", "২০১৫ সালে স্বাক্ষরিত (বৈশ্বিক উষ্ণতা ১.৫ ডিগ্রি সেলসিয়াসে সীমাবদ্ধ রাখার অঙ্গীকার)"),
                        FactItem("সদর দপ্তর", "ইইউ কমিশন", "ব্রাসেলস, বেলজিয়াম (Brussels, Belgium)")
                    ),
                    bangladeshRelevance = "ইউরোপীয় ইউনিয়ন বাংলাদেশের তৈরি পোশাকের (RMG) একক বৃহত্তম বাজার (মোট পোশাক রপ্তানির প্রায় ৫০%)। যদিও প্রাথমিক ধাপে টেক্সটাইল খাত CBAM-এর বাইরে আছে, তবে ২০২৬ পরবর্তী সময়ে তৈরি পোশাকেও কার্বন শুল্ক যুক্ত করার পরিকল্পনা রয়েছে। এছাড়া বাংলাদেশ ২০২৬ সালের পর এলডিসি (LDC) থেকে উন্নয়নশীল দেশে উত্তরণ ঘটলে ইইউ-র জিএসপি প্লাস (GSP+) সুবিধা পেতে পরিবেশগত ২৭টি কনভেনশন পরিপালন বাধ্যতামূলক হবে।",
                    bcsTakeaways = listOf(
                        "CBAM-এর পূর্ণরূপ: Carbon Border Adjustment Mechanism (উদ্যোক্তা: ইউরোপীয় ইউনিয়ন)।",
                        "ইউরোপীয় ইউনিয়নের বর্তমান সদস্য রাষ্ট্র সংখ্যা কত? ২৭টি দেশ।",
                        "প্যারিস জলবায়ু চুক্তি কত সালে স্বাক্ষরিত হয়? ২০১৫ সালে (COP21)।",
                        "ইউরোপীয় ইউনিয়নের সদর দপ্তর কোথায় অবস্থিত? ব্রাসেলস, বেলজিয়াম।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q5_1",
                                questionNumber = 13,
                                question = "বিশ্বের প্রথম কার্বন সীমান্ত কর 'CBAM' কোন আঞ্চলিক সংস্থার উদ্ভাবন?",
                                options = listOf("আসিয়ান (ASEAN)", "ইউরোপীয় ইউনিয়ন (EU)", "আফ্রিকান ইউনিয়ন (AU)", "জি-৭ (G7)"),
                                correctIndex = 1,
                                explanation = "ইউরোপীয় ইউনিয়ন (EU) তাদের ইউরোপীয় গ্রিন ডিলের অংশ হিসেবে CBAM চালু করেছে।"
                            ),
                            McqQuestion(
                                id = "q5_2",
                                questionNumber = 14,
                                question = "ইউরোপীয় ইউনিয়নের 'Fit for 55' প্যাকেজের মূল লক্ষ্য কী?",
                                options = listOf("৫৫টি নতুন দেশকে সদস্য করা", "২০৩০ সালের মধ্যে গ্রিনহাউস গ্যাস ৫৫% হ্রাস", "৫৫ বিলিয়ন ইউরো অনুদান", "গবেষণায় ৫৫% বাজেট বরাদ্দ"),
                                correctIndex = 1,
                                explanation = "১৯৯০ সালের মাত্রার তুলনায় ২০৩০ সালের মধ্যে ৫৫ শতাংশ কার্বন নির্গমন কমানোর লক্ষ্যই হলো Fit for 55।"
                            ),
                            McqQuestion(
                                id = "q5_3",
                                questionNumber = 15,
                                question = "ঐতিহাসিক প্যারিস জলবায়ু চুক্তি কোন কপ (COP) সম্মেলনে গৃহীত হয়?",
                                options = listOf("COP15", "COP21", "COP26", "COP28"),
                                correctIndex = 1,
                                explanation = "২০১৫ সালে ফ্রান্সের রাজধানী প্যারিসে অনুষ্ঠিত COP21 সম্মেলনে প্যারিস জলবায়ু চুক্তি গৃহীত হয়।"
                            )
                        ),
                        shortQuestions = listOf(
                            "CBAM কী এবং এটি উন্নয়নশীল দেশগুলোর জন্য কেন নতুন বাণিজ্য বাধা?",
                            "ইইউ-এর 'GSP Plus' স্কিম পেতে উন্নয়নশীল দেশকে কোন শর্ত পূরণ করতে হয়?"
                        ),
                        analyticalQuestion = "পরিবেশ সুরক্ষার নামে আরোপিত কার্বন শুল্ক (CBAM) কীভাবে বহুপাক্ষিক উন্মুক্ত বাণিজ্য ব্যবস্থার (WTO) জন্য চ্যালেঞ্জ সৃষ্টি করছে? এলডিসি গ্র্যাজুয়েশনের পর বাংলাদেশের তৈরি পোশাক শিল্পে এর সম্ভাব্য প্রভাব আলোচনা করুন।"
                    ),
                    reportedBy = listOf("বণিক বার্তা", "প্রথম আলো", "The Daily Star")
                ),
                TopicAnalysis(
                    id = "t6",
                    title = "বাব-এল-মান্দেব ও হরমুজ প্রণালীতে নৌ-নিরাপত্তা সংকট ও বৈশ্বিক জ্বালানি বাণিজ্য (Straits of Bab-el-Mandeb & Hormuz)",
                    category = "ভৌগোলিক রাজনীতি ও কৌশলগত জলপথ",
                    importanceRank = 6,
                    todaysEvent = "লোহিত সাগরের প্রবেশদ্বার বাব-এল-মান্দেব প্রণালীতে বাণিজ্যিক জাহাজে ইয়েমেনের হুথি বিদ্রোহীদের মিসাইল হামলার প্রেক্ষিতে আন্তর্জাতিক শিপিং কোম্পানিগুলো সুয়েজ খাল এড়িয়ে আফ্রিকার উত্তমাশা অন্তরীপ (Cape of Good Hope) দিয়ে জাহাজ পরিচালনা অব্যাহত রেখেছে। এর ফলে পণ্য পরিবহন ব্যয় প্রায় ৩০০% বৃদ্ধি পেয়েছে এবং পণ্যের চালান পৌঁছাতে অতিরিক্ত ১০-১৪ দিন সময় লাগছে। একই সাথে পারস্য উপসাগরের হরমুজ প্রণালীতে নিরাপত্তা উত্তেজনা অপরিশোধিত জ্বালানি তেলের বাজারকে অস্থির করে তুলেছে।",
                    whyImportant = "বিশ্বের মোট সমুদ্র বাণিজ্যের প্রায় ১২% সুয়েজ খাল ও বাব-এল-মান্দেব দিয়ে এবং বিশ্বের সমুদ্রপথে পরিবাহিত তেলের ২০% হরমুজ প্রণালী দিয়ে পরিবাহিত হয়। এই দুটি কৌশলগত চোক পয়েন্ট (Choke Points) বন্ধ বা বিঘ্নিত হলে বৈশ্বিক সাপ্লাই চেইন ভেঙে পড়ার উপক্রম হয়।",
                    background = "লোহিত সাগর ও এডেন উপসাগরকে যুক্ত করেছে বাব-এল-মান্দেব প্রণালী (যার অর্থ 'চোখের পানির দুয়ার')। অন্যদিকে পারস্য উপসাগর ও ওমান উপসাগরকে সংযুক্ত করেছে হরমুজ প্রণালী। গাজা যুদ্ধের পর থেকে হুথিরা ইসরায়েল ও পশ্চিমা সংশ্লিষ্ট জাহাজে আক্রমণ চালাচ্ছে।",
                    importantEntities = ImportantEntities(
                        persons = listOf("আব্দুল মালেক আল-হুথি (হুথি নেতা, ইয়েমেন)", "লয়েড অস্টিন (US Defense Secretary)"),
                        countries = listOf("ইয়েমেন (Yemen)", "ইরান (Iran)", "ওমান (Oman)", "জিবুতি (Djibouti)", "মিশর (Egypt)"),
                        organizations = listOf("আন্তর্জাতিক সমুদ্র সংস্থা (IMO)", "সুয়েজ খাল কর্তৃপক্ষ (SCA)"),
                        alliances = listOf("অপারেশন প্রসপারিটি গার্ডিয়ান (Operation Prosperity Guardian - US led naval coalition)"),
                        institutions = listOf("International Maritime Security Construct")
                    ),
                    importantFacts = listOf(
                        FactItem("কৌশলগত প্রণালী ১", "বাব-এল-মান্দেব (Bab-el-Mandeb)", "সংযুক্ত করেছে: লোহিত সাগর ও এডেন উপসাগর/আরব সাগর; পৃথক করেছে: ইয়েমেন ও জিবুতি/ইরিট্রিয়া"),
                        FactItem("কৌশলগত প্রণালী ২", "হরমুজ প্রণালী (Strait of Hormuz)", "সংযুক্ত করেছে: পারস্য উপসাগর ও ওমান উপসাগর; বিভক্ত করেছে: ইরান ও ওমান/ইউএই"),
                        FactItem("বিকল্প পথ", "উত্তমাশা অন্তরীপ (Cape of Good Hope)", "আফ্রিকার দক্ষিণ প্রান্তে অবস্থিত; ইউরোপ ও এশিয়ার দীর্ঘ বিকল্প নৌপথ"),
                        FactItem("আন্তর্জাতিক খাল", "সুয়েজ খাল (Suez Canal)", "ভূমধ্যসাগর ও লোহিত সাগরকে যুক্ত করেছে; উন্মুক্ত: ১৮৬৯ সালে; জাতীয়করণ: ১৯৫৬ সালে গামাল আবদেল নাসের কর্তৃক"),
                        FactItem("সদর দপ্তর", "IMO (International Maritime Organization)", "লন্ডন, যুক্তরাজ্য (London, UK)")
                    ),
                    bangladeshRelevance = "বাংলাদেশের ইউরোপমুখী তৈরি পোশাক রপ্তানির সিংহভাগ সুয়েজ খাল হয়ে যায়। শিপিং রুট পরিবর্তনের কারণে প্রতিটি কনটেইনারে ফ্রেইট চার্জ বৃদ্ধি পাওয়ায় দেশীয় রপ্তানিকারকদের মুনাফা কমছে এবং প্রতিযোগিতা সক্ষমতা হুমকিতে পড়েছে। এছাড়া মধ্যপ্রাচ্য থেকে এলএনজি ও জ্বালানি তেল আমদানির প্রধান রুট হরমুজ প্রণালী বিঘ্নিত হলে বাংলাদেশে তীব্র জ্বালানি সংকট ও লোডশেডিং দেখা দিতে পারে।",
                    bcsTakeaways = listOf(
                        "বাব-এল-মান্দেব প্রণালী কোন দুটি জলভাগকে যুক্ত করেছে? লোহিত সাগর ও এডেন উপসাগর।",
                        "হরমুজ প্রণালী কোন দুটি দেশকে বিভক্ত করেছে? ইরান ও ওমান।",
                        "সুয়েজ খাল কত সালে চালু হয় এবং কত সালে জাতীয়করণ হয়? চালু: ১৮৬৯, জাতীয়করণ: ১৯৫৬।",
                        "আন্তর্জাতিক সমুদ্র সংস্থা (IMO)-এর সদর দপ্তর কোথায়? লন্ডন, যুক্তরাজ্য।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q6_1",
                                questionNumber = 16,
                                question = "বাব-এল-মান্দেব প্রণালী কোন দুটি মহাদেশকে পৃথক করেছে?",
                                options = listOf("ইউরোপ ও এশিয়া", "এশিয়া ও আফ্রিকা", "আফ্রিকা ও ইউরোপ", "উত্তর আমেরিকা ও এশিয়া"),
                                correctIndex = 1,
                                explanation = "বাব-এল-মান্দেব এশিয়া (ইয়েমেন) ও আফ্রিকা (জিবুতি ও ইরিত্রিয়া)-কে পৃথক করেছে।"
                            ),
                            McqQuestion(
                                id = "q6_2",
                                questionNumber = 17,
                                question = "পারস্য উপসাগরের প্রবেশদ্বার খ্যাত কৌশলগত 'হরমুজ প্রণালী' কোন দুটি জলভাগকে যুক্ত করেছে?",
                                options = listOf("লোহিত সাগর ও আরব সাগর", "পারস্য উপসাগর ও ওমান উপসাগর", "ভূমধ্যসাগর ও কৃষ্ণসাগর", "কাস্পিয়ান সাগর ও কৃষ্ণসাগর"),
                                correctIndex = 1,
                                explanation = "হরমুজ প্রণালী পারস্য উপসাগর এবং ওমান উপসাগর (ভারত মহাসাগর)-কে সংযুক্ত করেছে।"
                            ),
                            McqQuestion(
                                id = "q6_3",
                                questionNumber = 18,
                                question = "ঐতিহাসিক সুয়েজ খাল কত সালে আনুষ্ঠানিকভাবে উন্মুক্ত করা হয়?",
                                options = listOf("১৮৫৭", "১৮৬৯", "১৯১৪", "১৯৫৬"),
                                correctIndex = 1,
                                explanation = "১৮৬৯ সালের ১৭ নভেম্বর মিশরের সুয়েজ খাল আন্তর্জাতিক জাহাজ চলাচলের জন্য উন্মুক্ত করা হয়।"
                            )
                        ),
                        shortQuestions = listOf(
                            "কৌশলগত 'চোক পয়েন্ট' (Choke Point) কাকে বলে? বিশ্ববাণিজ্যের দুটি প্রধান চোক পয়েন্টের নাম লিখুন।",
                            "সুয়েজ সংকটের পটভূমি ও ১৯৫৬ সালে এর জাতীয়করণের গুরুত্ব ব্যাখ্যা করুন।"
                        ),
                        analyticalQuestion = "লোহিত সাগর ও হরমুজ প্রণালীতে ভূ-রাজনৈতিক অস্থিতিশীলতা বৈশ্বিক সরবরাহ শৃঙ্খল (Global Supply Chain) এবং বাংলাদেশের মতো আমদানিনির্ভর অর্থনীতির জন্য কী ধরনের বহুমাত্রিক ঝুঁকি তৈরি করেছে?"
                    ),
                    reportedBy = listOf("প্রথম আলো", "The Daily Star", "বণিক বার্তা", "ঢাকা ট্রিবিউন"),
                    youtubeVideo = YouTubeSuggestion(
                        title = "Why the Bab-el-Mandeb Strait & Suez Canal Control World Trade",
                        channelName = "DW News Documentary",
                        duration = "11:20",
                        videoUrl = "https://www.youtube.com/watch?v=8VqKqZ8m1Lo",
                        description = "লোহিত সাগর ও বাব-এল-মান্দেব প্রণালীর ভূ-রাজনৈতিক চোক পয়েন্ট এবং বিকল্প উত্তমাশা অন্তরীপ নৌপথ।"
                    )
                )
            ),
            crossConnections = listOf(
                CrossConnection(
                    id = "c1",
                    title = "মধ্যপ্রাচ্য সংঘাত ➔ বাব-এল-মান্দেব ও লোহিত সাগর ➔ আন্তর্জাতিক শিপিং ও সুয়েজ খাল ➔ জ্বালানি মূল্যস্ফীতি ও বাংলাদেশ",
                    connectionChain = "লেবানন ও গাজায় সামরিক সংঘাত ➔ ইয়েমেনের হুথি বিদ্রোহীদের দ্বারা বাব-এল-মান্দেব প্রণালীতে নৌ-হামলা ➔ আন্তর্জাতিক জাহাজগুলোর উত্তমাশা অন্তরীপ দিয়ে দীর্ঘ পথ পরিভ্রমণ ➔ সুয়েজ খালের ট্রানজিট রাজস্ব হ্রাস ➔ বৈশ্বিক পণ্য পরিবহন খরচ ও অপরিশোধিত জ্বালানি তেলের মূল্য বৃদ্ধি ➔ বাংলাদেশে ডলার সংকট, আমদানি ব্যয় বৃদ্ধি ও মূল্যস্ফীতি।",
                    geopoliticalAnalysis = "একটি আঞ্চলিক সামরিক সংঘাত কীভাবে সরাসরি আন্তর্জাতিক সমুদ্র আইন, ভৌগোলিক চোক পয়েন্ট এবং উন্নয়নশীল দেশের ম্যাক্রো-ইকোনমিক স্থিতিশীলতাকে প্রভাবিত করে এটি তার জলজ্যান্ত দৃষ্টান্ত।"
                ),
                CrossConnection(
                    id = "c2",
                    title = "ব্রিকস শীর্ষ সম্মেলন ➔ ভারত-চীন সীমান্ত সমঝোতা ➔ এশীয় পরাশক্তির ভারসাম্য ➔ গ্লোবাল সাউথ ও বহুমুখী বিশ্বব্যবস্থা",
                    connectionChain = "রাশিয়ার কাজানে ব্রিকস শীর্ষ সম্মেলন ➔ লাদাখের প্রকৃত নিয়ন্ত্রণ রেখা (LAC)-তে ভারত ও চীনের সেনা টহল চুক্তি ➔ চার বছরের দ্বিপাক্ষিক শীতলতার অবসান ➔ ব্রিকস অর্থনৈতিক ব্লকে সমন্বিত কণ্ঠস্বর ➔ মার্কিন ডলার আধিপত্যের বিকল্প হিসেবে স্থানীয় মুদ্রায় বাণিজ্যের প্রসার ➔ দক্ষিণ এশিয়ায় বহুমুখী কূটনৈতিক সুযোগ।",
                    geopoliticalAnalysis = "পশ্চিমের অর্থনৈতিক নিষেধাজ্ঞার বিপরীতে ব্রিকস জোটের ঐক্য দৃঢ় করতে চীন ও ভারতের বৈরিতা প্রশমন ছিল অপরিহার্য। এটি প্রমাণ করে আঞ্চলিক নিরাপত্তা সমাধান বৈশ্বিক অর্থনৈতিক জোট গঠনে অনুঘটক হিসেবে কাজ করে।"
                ),
                CrossConnection(
                    id = "c3",
                    title = "ইউরোপীয় গ্রিন ডিল ➔ CBAM কার্বন শুল্ক ➔ আইএমএফ গ্রোথ আউটলুক ➔ এলডিসি উত্তরণ ও বাণিজ্য সুরক্ষা",
                    connectionChain = "ইইউ কার্বন সীমান্ত কর (CBAM) বাস্তবায়ন ➔ উন্নয়নশীল দেশগুলোর শিল্প পণ্যে অতিরিক্ত ব্যয় ➔ আইএমএফের বৈশ্বিক বাণিজ্য খণ্ডায়ন (Fragmentation) সতর্কতা ➔ ২০২৬ সালে বাংলাদেশের এলডিসি গ্র্যাজুয়েশন ➔ জিএসপি প্লাস সুবিধা প্রাপ্তিতে পরিবেশগত শর্ত পূরণ ➔ সবুজ কারখানা (Green Factory) রূপান্তরে চাপ।",
                    geopoliticalAnalysis = "জলবায়ু কূটনীতি আর কেবল পরিবেশগত আলোচনার মধ্যে সীমাবদ্ধ নেই; এটি এখন আন্তর্জাতিক বাণিজ্যের শক্তিশালী ভূ-রাজনৈতিক হাতিয়ার (Geopolitical Instrument of Trade) হিসেবে আবির্ভূত হয়েছে।"
                )
            ),
            bangladeshAngleSummary = "১৬ অক্টোবরের আন্তর্জাতিক ঘটনাবলি বাংলাদেশের জাতীয় নিরাপত্তা, কূটনীতি ও অর্থনীতির সাথে ওতপ্রোতভাবে জড়িত। একদিকে জাতিসংঘ শান্তিরক্ষা মিশনে (UNIFIL) বাংলাদেশি নৌবাহিনী ও শান্তিরক্ষীদের পেশাদারিত্ব বিশ্বমঞ্চে বাংলাদেশের মর্যাদা সমুন্নত রাখছে, অন্যদিকে লোহিত সাগর সংকটে পোশাক খাতের শিপিং ব্যয় ও জ্বালানি আমদানি ব্যাহত হওয়ার ঝুঁকি দেখা দিয়েছে। একই সাথে ভারত ও চীনের সীমান্ত উত্তেজনা প্রশমন বাংলাদেশকে দুই বৃহৎ উন্নয়ন অংশীদারের সাথে ভারসাম্যপূর্ণ পররাষ্ট্রনীতি বজায় রাখতে স্বস্তি জোগাবে। ২০২৬ সালের এলডিসি উত্তরণের প্রেক্ষাপটে ইউরোপীয় ইউনিয়নের CBAM কার্বন ট্যাক্স ও আইএমএফের সংস্কার কর্মসূচি বাস্তবায়ন বাংলাদেশের ভবিষ্যতের অন্যতম বড় কূটনৈতিক ও অর্থনৈতিক পরীক্ষা।",
            quickRevisions = listOf(
                QuickRevisionItem(1, "UNIFIL কোন দেশে মোতায়েনকৃত জাতিসংঘ শান্তিরক্ষা মিশন?", "লেবানন (স্থাপিত: ১৯৭৮)", "জাতিসংঘ ও নিরাপত্তা"),
                QuickRevisionItem(2, "লেবানন ও ইসরায়েল বিভক্তকারী সীমান্ত রেখার নাম কী?", "ব্লু লাইন (Blue Line)", "আন্তর্জাতিক ভূগোল"),
                QuickRevisionItem(3, "জাতিসংঘ নিরাপত্তা পরিষদের প্রস্তাব ১৭০১ কোন দেশের যুদ্ধবিরতি সংক্রান্ত?", "লেবানন (২০০৬ সালের যুদ্ধ)", "আন্তর্জাতিক চুক্তি ও প্রস্তাব"),
                QuickRevisionItem(4, "ভারত ও চীনের মধ্যবর্তী লাদাখ অঞ্চলের সীমারেখাকে কী বলা হয়?", "LAC (Line of Actual Control)", "সীমান্ত ও বিরোধ"),
                QuickRevisionItem(5, "ঐতিহাসিক গালওয়ান উপত্যকা কোথায় অবস্থিত?", "পূর্ব লাদাখ (ভারত-চীন সীমান্ত)", "আন্তর্জাতিক ভূগোল"),
                QuickRevisionItem(6, "১৬তম ব্রিকস (BRICS) শীর্ষ সম্মেলন কোথায় অনুষ্ঠিত হচ্ছে?", "কাজান, রাশিয়া", "সম্মেলন ও জোট"),
                QuickRevisionItem(7, "২০২৪ সালে ব্রিকসের নতুন সদস্য হিসেবে কার্যকর হয়েছে কোন ৪ দেশ?", "মিসর, ইথিওপিয়া, ইরান ও সংযুক্ত আরব আমিরাত", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(8, "ব্রেটন উডস টুইনস (Bretton Woods Twins) বলা হয় কাদের?", "আইএমএফ (IMF) ও বিশ্বব্যাংক (World Bank)", "অর্থনীতি ও বাণিজ্য"),
                QuickRevisionItem(9, "আন্তর্জাতিক মুদ্রা তহবিল (IMF)-এর বর্তমান সদস্য দেশ কতটি?", "১৯০টি (সর্বশেষ: অ্যান্ডোরা)", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(10, "IMF-এর SDR বাস্কেটে কয়টি মুদ্রা রয়েছে?", "৫টি (USD, EUR, CNY, JPY, GBP)", "বৈশ্বিক অর্থনীতি"),
                QuickRevisionItem(11, "উত্তর কোরিয়া ও দক্ষিণ কোরিয়াকে বিভক্ত করেছে কোন অক্ষ রেখা?", "৩৮তম সমান্তরাল রেখা (38th Parallel)", "সীমান্ত ও ভূগোল"),
                QuickRevisionItem(12, "১৯৫৩ সালের কোরীয় যুদ্ধবিরতি চুক্তি কোথায় স্বাক্ষরিত হয়?", "পানমুনজম (Panmunjom)", "আন্তর্জাতিক চুক্তি"),
                QuickRevisionItem(13, "উত্তর কোরিয়া কত সালে NPT চুক্তি থেকে নিজেকে প্রত্যাহার করে?", "২০০৩ সালে", "নিরস্ত্রীকরণ"),
                QuickRevisionItem(14, "বিশ্বের প্রথম কার্বন সীমান্ত শুল্ক 'CBAM' কোন সংস্থার উদ্যোগ?", "ইউরোপীয় ইউনিয়ন (EU)", "জলবায়ু কূটনীতি"),
                QuickRevisionItem(15, "ইউরোপীয় ইউনিয়নের বর্তমান সদস্য রাষ্ট্র সংখ্যা কত?", "২৭টি দেশ", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(16, "ঐতিহাসিক প্যারিস জলবায়ু চুক্তি কত সালে স্বাক্ষরিত হয়?", "২০১৫ সালে (COP21)", "পরিবেশ ও চুক্তি"),
                QuickRevisionItem(17, "বাব-এল-মান্দেব প্রণালী কোন দুটি জলভাগকে যুক্ত করেছে?", "লোহিত সাগর ও এডেন উপসাগর", "প্রণালী ও সাগর"),
                QuickRevisionItem(18, "হরমুজ প্রণালী কোন দুটি উপসাগরকে যুক্ত করেছে?", "পারস্য উপসাগর ও ওমান উপসাগর", "প্রণালী ও সাগর"),
                QuickRevisionItem(19, "সুয়েজ খাল কত সালে আনুষ্ঠানিকভাবে চালু হয়?", "১৮৬৯ সালে", "আন্তর্জাতিক নৌপথ"),
                QuickRevisionItem(20, "সুয়েজ খাল কত সালে ও কার দ্বারা জাতীয়করণ হয়?", "১৯৫৬ সালে, গামাল আবদেল নাসের (মিশর)", "ঐতিহাসিক সংকট"),
                QuickRevisionItem(21, "আন্তর্জাতিক সমুদ্র সংস্থা (IMO)-এর সদর দপ্তর কোথায়?", "লন্ডন, যুক্তরাজ্য", "আন্তর্জাতিক সদর দপ্তর")
            ),
            mcqTest = listOf(
                McqQuestion(
                    id = "test_1",
                    questionNumber = 1,
                    question = "লেবাননে শান্তিরক্ষায় নিয়োজিত জাতিসংঘের অন্তর্বর্তীকালীন মিশন 'UNIFIL' কত সালে গঠিত হয়?",
                    options = listOf("ক. ১৯৪৮", "খ. ১৯৬৭", "গ. ১৯৭৮", "ঘ. ২০০৬"),
                    correctIndex = 2,
                    explanation = "১৯৭৮ সালে জাতিসংঘ নিরাপত্তা পরিষদের রেজোলিউশন ৪২৫ ও ৪২৬ অনুসারে UNIFIL গঠিত হয়।"
                ),
                McqQuestion(
                    id = "test_2",
                    questionNumber = 2,
                    question = "জাতিসংঘ নির্ধারিত 'ব্লু লাইন' (Blue Line) কোন দুটি দেশের সীমানা নির্দেশ করে?",
                    options = listOf("ক. সিরিয়া ও ইসরায়েল", "খ. লেবানন ও ইসরায়েল", "গ. মিশর ও সুদান", "ঘ. জর্ডান ও পশ্চিমতীর"),
                    correctIndex = 1,
                    explanation = "২০০০ সালে জাতিসংঘ লেবানন ও ইসরায়েলের মধ্যবর্তী প্রত্যাহার রেখা হিসেবে ব্লু লাইন নির্ধারণ করে।"
                ),
                McqQuestion(
                    id = "test_3",
                    questionNumber = 3,
                    question = "জাতিসংঘ নিরাপত্তা পরিষদের 'প্রস্তাব ১৭০১' (UNSC Resolution 1701) কোন ঘটনার সাথে সম্পর্কিত?",
                    options = listOf("ক. কুয়েত যুদ্ধ", "খ. ২০০৬ সালের লেবানন যুদ্ধবিরতি", "গ. কসোভো স্বাধীনতা", "ঘ. রুয়ান্ডা গণহত্যা"),
                    correctIndex = 1,
                    explanation = "২০০৬ সালের দ্বিতীয় লেবানন যুদ্ধের অবসান ঘটিয়ে হিজবুল্লাহ ও ইসরায়েলের মধ্যে যুদ্ধবিরতির জন্য এটি গৃহীত হয়।"
                ),
                McqQuestion(
                    id = "test_4",
                    questionNumber = 4,
                    question = "ভারত ও চীনের মধ্যকার অনানুষ্ঠানিক নিয়ন্ত্রণ রেখাকে কী বলা হয়?",
                    options = listOf("ক. LoC", "খ. LAC (Line of Actual Control)", "গ. ম্যাকমোহন লাইন", "ঘ. রেডক্লিফ লাইন"),
                    correctIndex = 1,
                    explanation = "লাদাখ ও হিমালয় সীমান্তে ভারত ও চীনের কার্যকর সীমারেখাকে LAC বলা হয়।"
                ),
                McqQuestion(
                    id = "test_5",
                    questionNumber = 5,
                    question = "২০২৪ সালের ১ জানুয়ারি ব্রিকস (BRICS)-এ যোগদানের আমন্ত্রণ সত্ত্বেও কোন দেশ চূড়ান্তভাবে প্রত্যাহার করে?",
                    options = listOf("ক. ইরান", "খ. আর্জেন্টিনা", "গ. সৌদি আরব", "ঘ. সংযুক্ত আরব আমিরাত"),
                    correctIndex = 1,
                    explanation = "আর্জেন্টিনার প্রেসিডেন্ট হাভিয়ের মিলের প্রশাসন ব্রিকসে যোগ না দেওয়ার আনুষ্ঠানিক সিদ্ধান্ত জানায়।"
                ),
                McqQuestion(
                    id = "test_6",
                    questionNumber = 6,
                    question = "১৬তম ব্রিকস শীর্ষ সম্মেলন ২০২৬ সালে রাশিয়ার কোন শহরে অনুষ্ঠিত হচ্ছে?",
                    options = listOf("ক. মস্কো", "খ. সেন্ট পিটার্সবার্গ", "গ. কাজান (Kazan)", "ঘ. সোচি"),
                    correctIndex = 2,
                    explanation = "রাশিয়ার তাতারস্তান প্রজাতন্ত্রের রাজধানী কাজান শহরে ১৬তম ব্রিকস শীর্ষ সম্মেলন অনুষ্ঠিত হচ্ছে।"
                ),
                McqQuestion(
                    id = "test_7",
                    questionNumber = 7,
                    question = "ব্রেটন উডস টুইনস (Bretton Woods Twins) হিসেবে পরিচিত কোন দুটি প্রতিষ্ঠান?",
                    options = listOf("ক. WTO ও UN", "খ. IMF ও World Bank", "গ. ADB ও AIIB", "ঘ. OECD ও BIS"),
                    correctIndex = 1,
                    explanation = "১৯৪৪ সালের ব্রেটন উডস সম্মেলনের মধ্য দিয়ে আন্তর্জাতিক মুদ্রা তহবিল (IMF) এবং বিশ্বব্যাংক গঠিত হয়।"
                ),
                McqQuestion(
                    id = "test_8",
                    questionNumber = 8,
                    question = "আন্তর্জাতিক মুদ্রা তহবিল (IMF)-এর বর্তমান সদস্য রাষ্ট্রের সংখ্যা কত?",
                    options = listOf("ক. ১৮৯টি", "খ. ১৯০টি", "গ. ১৯৩টি", "ঘ. ১৯৫টি"),
                    correctIndex = 1,
                    explanation = "২০২০ সালে অ্যান্ডোরা যুক্ত হওয়ায় আইএমএফ-এর সদস্য সংখ্যা দাঁড়ায় ১৯০টিতে।"
                ),
                McqQuestion(
                    id = "test_9",
                    questionNumber = 9,
                    question = "IMF-এর স্পেশাল ড্রয়িং রাইটস (SDR) বাস্কেটে কোন ৫টি প্রধান মুদ্রা অন্তর্ভুক্ত?",
                    options = listOf(
                        "ক. USD, EUR, CNY, JPY, GBP",
                        "খ. USD, EUR, CHF, JPY, CAD",
                        "গ. USD, GBP, INR, CNY, RUB",
                        "ঘ. USD, EUR, AUD, JPY, SGD"
                    ),
                    correctIndex = 0,
                    explanation = "SDR বাস্কেটের ৫টি মুদ্রা হলো মার্কিন ডলার, ইউরো, চীনা রেনমিনবি, জাপানি ইয়েন এবং ব্রিটিশ পাউন্ড।"
                ),
                McqQuestion(
                    id = "test_10",
                    questionNumber = 10,
                    question = "উত্তর কোরিয়া ও দক্ষিণ কোরিয়াকে বিভক্তকারী ঐতিহাসিক সমান্তরাল অক্ষ রেখা কোনটি?",
                    options = listOf("ক. ১৭তম সমান্তরাল", "খ. ৩৮তম সমান্তরাল", "গ. ৪৯তম সমান্তরাল", "ঘ. ২৪তম সমান্তরাল"),
                    correctIndex = 1,
                    explanation = "দ্বিতীয় বিশ্বযুদ্ধের পর কোরীয় উপদ্বীপকে ৩৮তম সমান্তরাল অক্ষরেখা বরাবর উত্তর ও দক্ষিণে বিভক্ত করা হয়।"
                ),
                McqQuestion(
                    id = "test_11",
                    questionNumber = 11,
                    question = "বিশ্বের প্রথম কার্বন সীমান্ত সমন্বয় ব্যবস্থা (CBAM) চালুর উদ্যোগ গ্রহণ করেছে কোন সংস্থা?",
                    options = listOf("ক. আসিয়ান", "খ. জি-২০", "গ. ইউরোপীয় ইউনিয়ন (EU)", "ঘ. ওপেক"),
                    correctIndex = 2,
                    explanation = "কার্বন লিকেজ রোধ ও গ্রিন ডিল বাস্তবায়নে ইউরোপীয় ইউনিয়ন CBAM আইন প্রণয়ন করেছে।"
                ),
                McqQuestion(
                    id = "test_12",
                    questionNumber = 12,
                    question = "লোহিত সাগর ও এডেন উপসাগরকে সংযুক্তকারী কৌশলগত প্রণালীটির নাম কী?",
                    options = listOf("ক. হরমুজ প্রণালী", "খ. বাব-এল-মান্দেব প্রণালী", "গ. মালাক্কা প্রণালী", "ঘ. বসফরাস প্রণালী"),
                    correctIndex = 1,
                    explanation = "বাব-এল-মান্দেব প্রণালী লোহিত সাগর ও এডেন উপসাগরকে সংযুক্ত করেছে।"
                ),
                McqQuestion(
                    id = "test_13",
                    questionNumber = 13,
                    question = "মিশরের ঐতিহাসিক সুয়েজ খাল কত সালে আন্তর্জাতিক নৌ চলাচলের জন্য উন্মুক্ত করা হয়?",
                    options = listOf("ক. ১৮৫৭", "খ. ১৮৬৯", "গ. ১৯১৪", "ঘ. ১৯৪৮"),
                    correctIndex = 1,
                    explanation = "১৮৬৯ সালের ১৭ নভেম্বর ফরাসি প্রকৌশলী ফার্দিনান্দ ডি লেসেপসের নকশায় নির্মিত সুয়েজ খাল চালু হয়।"
                ),
                McqQuestion(
                    id = "test_14",
                    questionNumber = 14,
                    question = "আন্তর্জাতিক সমুদ্র সংস্থা (IMO)-এর প্রধান কার্যালয় কোন শহরে অবস্থিত?",
                    options = listOf("ক. জেনেভা", "খ. নিউইয়র্ক", "গ. লন্ডন", "ঘ. ভিয়েনা"),
                    correctIndex = 2,
                    explanation = "আন্তর্জাতিক সমুদ্র সংস্থা (International Maritime Organization)-এর সদর দপ্তর লন্ডনে অবস্থিত।"
                ),
                McqQuestion(
                    id = "test_15",
                    questionNumber = 15,
                    question = "ঐতিহাসিক 'ম্যাকমোহন লাইন' কোন দুটি দেশের মধ্যে সীমারেখা নির্ধারণ করেছিল?",
                    options = listOf("ক. ভারত ও পাকিস্তান", "খ. ব্রিটিশ ভারত ও তিব্বত (চীন)", "গ. আফগানিস্তান ও পাকিস্তান", "ঘ. ভারত ও মায়ানমার"),
                    correctIndex = 1,
                    explanation = "১৯১৪ সালের সিমলা কনভেনশনে ব্রিটিশ ভারতের পক্ষে স্যার হেনরি ম্যাকমোহন এই সীমানা চিহ্নিত করেন।"
                )
            ),
            writtenVivaAngles = listOf(
                WrittenVivaItem(
                    id = "wv1",
                    issue = "জাতিসংঘ শান্তিরক্ষা কার্যক্রমে নিরাপত্তা ঝুঁকি ও বহুপাক্ষিক ব্যবস্থার সংকট (UNIFIL Incident)",
                    examCategory = "BCS Written (Paper II - International Affairs)",
                    possibleQuestion = "জাতিসংঘ শান্তিরক্ষীদের ওপর সাম্প্রতিক আক্রমণ বৈশ্বিক বহুপাক্ষিক ব্যবস্থার দুর্বলতা কীভাবে ফুটিয়ে তুলেছে? শীর্ষ ট্রুপ কন্ট্রিবিউটিং দেশ হিসেবে বাংলাদেশের ভূমিকা ও স্বার্থ রক্ষা কৌশল মূল্যায়ন করুন।",
                    keyPoints = listOf(
                        "প্রস্তাব ১৭০১ (Res 1701) এবং আন্তর্জাতিক মানবিক আইনে শান্তিরক্ষীদের সুরক্ষা ধারা উল্লেখ করা।",
                        "জাতিসংঘ নিরাপত্তা পরিষদের স্থায়ী সদস্যদের (P5) ভেটো ক্ষমতার কারণে সিদ্ধান্তহীনতা ও কার্যকারিতা হ্রাস।",
                        "বাংলাদেশের অবস্থান: শীর্ষ সৈন্য ও পুলিশ প্রেরণকারী দেশ (TCC), লেবাননে নৌবাহিনীর কর্ভেট যুদ্ধজাহাজ BNS সংগ্রাম-এর উপস্থিতি।",
                        "কূটনৈতিক সুপারিশ: শান্তিরক্ষীদের ম্যান্ডেট শক্তিশালীকরণ এবং জেনেভা কনভেনশনের অধীনে দায়ীদের জবাবদিহিতা নিশ্চিতকরণ।"
                    ),
                    analyticalTip = "উত্তরে অবশ্যই ব্লু লাইন ও লিটানি নদীর একটি স্কেচ বা মানচিত্রের রেফারেন্স দিন এবং বাংলাদেশের নৌবাহিনীর মেরিটাইম টাস্কফোর্স (MTF)-এর নাম উল্লেখ করুন।"
                ),
                WrittenVivaItem(
                    id = "wv2",
                    issue = "লাদাখে ভারত-চীন সীমান্ত সমঝোতা ও দক্ষিণ এশীয় ভূ-রাজনীতি (India-China Border Detente)",
                    examCategory = "BCS Written & Viva",
                    possibleQuestion = "চার বছরের অচলাবস্থার পর ভারত ও চীনের সীমান্ত টহল চুক্তি এশীয় পরাশক্তিগুলোর সম্পর্কে কী বার্তা দেয়? এই পরিবর্তন বাংলাদেশের ভারসাম্যপূর্ণ পররাষ্ট্রনীতি চর্চাকে কীভাবে প্রভাবিত করবে?",
                    keyPoints = listOf(
                        "২০২০ সালের গালওয়ান সংঘর্ষের পটভূমি এবং ডেপসাং ও ডেমচক অঞ্চলের সামরিক অচলাবস্থার অবসান।",
                        "ব্রিকস (BRICS) ও এসসিও (SCO) সম্মেলনের কূটনৈতিক পরিসরে দুই দেশের অর্থনৈতিক স্বার্থের পুনর্মিলন।",
                        "বাংলাদেশের পররাষ্ট্রনীতি নীতিবাক্য: 'সকলের সাথে বন্ধুত্ব, কারও সাথে বৈরিতা নয়' (বঙ্গবন্ধু শেখ মুজিবুর রহমান)।",
                        "বাস্তব সুবিধা: তিস্তা মহাপরিকল্পনা বাস্তবায়ন, মোংলা ও চট্টগ্রাম বন্দরের ব্যবহার এবং সীমান্ত বাণিজ্যে জটিলতা হ্রাস।"
                    ),
                    analyticalTip = "ভাইভায় প্রশ্নকর্তা জিজ্ঞেস করতে পারেন: 'চীন ও ভারতের দ্বন্দ্বে বাংলাদেশের সুবিধা বেশি নাকি সমঝোতায়?' স্পষ্ট বলবেন: সমঝোতায় বাংলাদেশের সুবিধা বেশি, কারণ বিরোধ বাড়লে যেকোনো এক পক্ষে যোগদানের ভূ-রাজনৈতিক চাপ সৃষ্টি হয়।"
                ),
                WrittenVivaItem(
                    id = "wv3",
                    issue = "লোহিত সাগর সংকট ও আন্তর্জাতিক নৌপথের বিকল্পায়ন (Choke Points & Maritime Security)",
                    examCategory = "BCS Written (Paper II)",
                    possibleQuestion = "বাব-এল-মান্দেব ও হরমুজ প্রণালীতে ভূ-কৌশলগত অস্থিতিশীলতা বিশ্ববাণিজ্যের চোক পয়েন্টগুলোকে কীভাবে বিপন্ন করেছে? বাংলাদেশের তৈরি পোশাক শিল্প ও জ্বালানি নিরাপত্তায় এর প্রভাব বিশ্লেষণ করুন।",
                    keyPoints = listOf(
                        "চোক পয়েন্টের সংজ্ঞা এবং সুয়েজ খাল বনাম উত্তমাশা অন্তরীপ (Cape of Good Hope) রুটের অর্থনৈতিক তুলনা (সময় বৃদ্ধি ১০-১৪ দিন, ফ্রেইট খরচ ৩০০% বৃদ্ধি)।",
                        "পোশাক খাতের লিড টাইম (Lead Time) ব্যাহত এবং ক্রেতাদের ডিসকাউন্ট দাবির ঝুঁকি।",
                        "হরমুজ প্রণালী দিয়ে মধ্যপ্রাচ্যের অপরিশোধিত তেল ও এলএনজি পরিবহনে ব্যাঘাত এবং অভ্যন্তরীণ বিদ্যুৎ সংকটের ঝুঁকি।",
                        "বাংলাদেশের করণীয়: বিকল্প সরবরাহ উৎস অনুসন্ধান, নিজস্ব জাতীয় বাণিজ্যিক জাহাজের (BSC) সক্ষমতা বৃদ্ধি এবং অর্থনৈতিক কূটনীতি।"
                    ),
                    analyticalTip = "বিশ্বের প্রধান প্রধান নৌ-চোক পয়েন্টগুলোর একটি তালিকা (মালাক্কা, হরমুজ, বাব-এল-মান্দেব, সুয়েজ, পানামা, বসফরাস) চার্ট আকারে উপস্থাপন করলে অতিরিক্ত নম্বর পাওয়া যাবে।"
                ),
                WrittenVivaItem(
                    id = "wv4",
                    issue = "ইইউ কার্বন সীমান্ত কর (CBAM) ও ২০২৬-পরবর্তী এলডিসি উত্তরণ চ্যালেঞ্জ",
                    examCategory = "BCS Written & Viva",
                    possibleQuestion = "ইউরোপীয় ইউনিয়নের CBAM নীতি জলবায়ু সুরক্ষার নামে বাণিজ্য বৈষম্য সৃষ্টি করছে কি? ২০২৬ সালে বাংলাদেশের উন্নয়নশীল দেশে উত্তরণের পর রপ্তানি প্রবৃদ্ধি ধরে রাখতে কী কী নীতি গ্রহণ করা আবশ্যক?",
                    keyPoints = listOf(
                        "CBAM-এর মেকানিজম: কার্বন নিঃসরণের ওপর ভিত্তি করে সীমান্ত শুল্ক আরোপ।",
                        "ডব্লিউটিও (WTO)-এর জাতীয় আচরণ (National Treatment) ও মোস্ট ফেভারড নেশন (MFN) নীতির সাথে বিরোধ।",
                        "বাংলাদেশের প্রস্তুতি: বিশ্বখ্যাত গ্রিন আরএমজি ফ্যাক্টরি (LEED Platinum সার্টিফায়েড ফ্যাক্টরির তালিকায় শীর্ষে বাংলাদেশ)।",
                        "নীতিগত সুপারিশ: নবায়নযোগ্য শক্তির ব্যবহার বৃদ্ধি, সার্কুলার ফ্যাশন ও কার্বন ফুটপ্রিন্ট সার্টিফিকেশনের সক্ষমতা অর্জন।"
                    ),
                    analyticalTip = "ভাইভাতে 'গ্রিন ওয়াশিং' (Greenwashing) এবং 'কার্বন লিকেজ' (Carbon Leakage) পরিভাষা দুটির অর্থ জানতে চাইতে পারে।"
                )
            ),
            sources = listOf(
                DailySource(
                    newspaperName = "প্রথম আলো",
                    englishName = "Prothom Alo",
                    section = "আন্তর্জাতিক সংবাদ (International)",
                    articlesCovered = listOf(
                        SourceArticle("লেবাননে জাতিসংঘ ঘাঁটিতে আঘাত, নিরাপত্তা পরিষদে ক্ষোভ", "লেবানন সীমান্তে UNIFIL ঘাঁটিতে হামলা"),
                        SourceArticle("চার বছর পর লাদাখ সীমান্তে ভারত-চীনের টহল সমঝোতা", "ভারত-চীন সীমান্ত সমঝোতা ও এলএসি"),
                        SourceArticle("দক্ষিণ কোরিয়াকে সাংবিধানিকভাবে চিরশত্রু ঘোষণা কিমের", "উত্তর কোরিয়ার সংবিধান সংশোধন")
                    ),
                    hasContent = true,
                    note = "প্রথম আলোর আন্তর্জাতিক ডেস্ক থেকে সংকলিত ও সম্পাদিত"
                ),
                DailySource(
                    newspaperName = "The Daily Star",
                    englishName = "The Daily Star",
                    section = "International / World News",
                    articlesCovered = listOf(
                        SourceArticle("UNSC demands protection of UNIFIL peacekeepers in Lebanon", "UNIFIL & UNSC Resolution 1701"),
                        SourceArticle("India, China reach pact on border patrolling along LAC ahead of BRICS", "India-China Border Agreement"),
                        SourceArticle("Red Sea attacks push freight rates higher as ships divert around Africa", "Straits of Bab-el-Mandeb & Hormuz")
                    ),
                    hasContent = true,
                    note = "The Daily Star International desk verified"
                ),
                DailySource(
                    newspaperName = "বণিক বার্তা",
                    englishName = "Bonik Barta",
                    section = "আন্তর্জাতিক বাণিজ্য ও অর্থনীতি",
                    articlesCovered = listOf(
                        SourceArticle("বিশ্ব প্রবৃদ্ধি ৩.২ শতাংশে আটকে থাকবে: আইএমএফের ডব্লিউইও পূর্বাভাস", "আইএমএফ ওয়ার্ল্ড ইকোনমিক আউটলুক"),
                        SourceArticle("ইইউর সিবিএএম কার্বন শুল্ক: উন্নয়নশীল দেশের রপ্তানিতে বাড়তি চাপ", "ইইউ কার্বন সীমান্ত সমন্বয় ব্যবস্থা (CBAM)"),
                        SourceArticle("লোহিত সাগর সংকটে জাহাজের ভাড়া বৃদ্ধি, ঝুঁকিতে পোশাক রপ্তানি", "বাব-এল-মান্দেব ও হরমুজ প্রণালী")
                    ),
                    hasContent = true,
                    note = "বণিক বার্তার আন্তর্জাতিক অর্থনীতি পাতা থেকে সংকলিত"
                ),
                DailySource(
                    newspaperName = "ঢাকা ট্রিবিউন",
                    englishName = "Dhaka Tribune",
                    section = "World / Geopolitics",
                    articlesCovered = listOf(
                        SourceArticle("North Korea formally labels South Korea as hostile state in constitution", "North Korea Constitutional Amendment"),
                        SourceArticle("UN peacekeepers remain in positions in southern Lebanon despite threats", "UNIFIL Peacekeeping Crisis"),
                        SourceArticle("Global energy markets remain on edge amid Middle East tensions", "Straits of Bab-el-Mandeb & Hormuz")
                    ),
                    hasContent = true,
                    note = "Dhaka Tribune World desk coverage analyzed"
                )
            )
        )
    }

    /**
     * Archive report for ১৫ অক্টোবর ২০২৬ (Demonstrates daily repeatability and date archives)
     */
    val report15Oct2026: DailyReport by lazy {
        DailyReport(
            id = "2026-10-15",
            banglaDate = "১৫ অক্টোবর ২০২৬",
            banglaTime = "৬:০০ PM BST",
            rawDateIso = "2026-10-15",
            heading = "BCS International Affairs — ১৫ অক্টোবর ২০২৬",
            topHeadlines = listOf(
                "সাংহাই সহযোগিতা সংস্থা (SCO) শীর্ষ সম্মেলনে ইসলামাবাদের বহুপাক্ষিক বাণিজ্য সংলাপ",
                "নোবেল শান্তি পুরস্কার ২০২৬ ঘোষণা ও আন্তর্জাতিক নিরস্ত্রীকরণ কূটনীতি",
                "মহাকাশে সামরিকীকরণ রোধে জাতিসংঘের সাধারণ পরিষদে খসড়া প্রস্তাব উত্থাপন",
                "গাজা ও রাফাহ সীমান্তে মানবিক করিডোর খোলার আন্তর্জাতিক আহ্বান"
            ),
            dayComparison = DayComparison(
                whatsNew = listOf(
                    "ইসলামাবাদে অনুষ্ঠিত এসসিও (SCO) সরকারপ্রধানদের কাউন্সিলে ভারত-পাকিস্তানের পররাষ্ট্রমন্ত্রী পর্যায়ে বিরল শুভেচ্ছা বিনিময়।",
                    "পরমাণু অস্ত্র নিষিদ্ধকরণ প্রচারণায় অবদানের জন্য আন্তর্জাতিক সংস্থাকে নোবেল শান্তি পুরস্কার সম্মাননা।"
                ),
                ongoingIssues = listOf(
                    "মধ্যপ্রাচ্যের সংঘাত ও মানবিক সহায়তা প্রবেশের জটিলতা।",
                    "ইউক্রেন যুদ্ধে ড্রোন প্রযুক্তি ও পশ্চিমা প্রতিরক্ষা সহায়তা সরবরাহ।"
                ),
                escalatedIssues = listOf(
                    "মহাকাশ প্রযুক্তির সামরিকায়ন ও স্যাটেলাইট যোগাযোগ জ্যামিং নিয়ে পরাশক্তিদের প্রযুক্তি প্রতিযোগিতা।"
                )
            ),
            topics = listOf(
                TopicAnalysis(
                    id = "t_sc_1",
                    title = "ইসলামাবাদে এসসিও (SCO) শীর্ষ সম্মেলন ও আঞ্চলিক সহযোগিতা (SCO Council of Heads of Government)",
                    category = "আঞ্চলিক সংস্থা ও কূটনীতি",
                    importanceRank = 1,
                    todaysEvent = "পাকিস্তানের রাজধানী ইসলামাবাদে সাংহাই সহযোগিতা সংস্থা (SCO)-এর ২৩তম সরকারপ্রধানদের কাউন্সিল অনুষ্ঠিত হয়েছে। সম্মেলনে আঞ্চলিক বাণিজ্য সম্প্রসারণ, নিজস্ব মুদ্রায় আন্তঃসীমান্ত লেনদেন এবং সন্ত্রাসবাদ দমনে সমন্বিত কৌশল নিয়ে আলোচনা হয়।",
                    whyImportant = "এসসিও ইউরেশীয় অঞ্চলের বৃহত্তম আঞ্চলিক নিরাপত্তা ও অর্থনৈতিক ফোরাম। দক্ষিণ এশিয়ার দুই প্রতিদ্বন্দ্বী ভারত ও পাকিস্তানের অংশগ্রহণে বহুপাক্ষিক কূটনৈতিক মঞ্চ হিসেবে এর গুরুত্ব অপরিসীম।",
                    background = "১৯৯৬ সালে প্রতিষ্ঠিত সাংহাই ফাইভ (Shanghai Five) থেকে ২০০১ সালের ১৫ জুন আনুষ্ঠানিকভাবে এসসিও আত্মপ্রকাশ করে। এর সদর দপ্তর বেইজিংয়ে অবস্থিত।",
                    importantEntities = ImportantEntities(
                        persons = listOf("শেহবাজ শরিফ (পাকিস্তানের প্রধানমন্ত্রী)", "এস জয়শঙ্কর (ভারতের পররাষ্ট্রমন্ত্রী)", "লি চিয়াং (চীনের প্রধানমন্ত্রী)"),
                        countries = listOf("চীন", "রাশিয়া", "পাকিস্তান", "ভারত", "ইরান", "বেলারুশ"),
                        organizations = listOf("SCO (সাংহাই সহযোগিতা সংস্থা)"),
                        alliances = listOf("RATS (Regional Anti-Terrorist Structure)"),
                        institutions = listOf("SCO Secretariat, Beijing")
                    ),
                    importantFacts = listOf(
                        FactItem("প্রতিষ্ঠা", "SCO", "২০০১ সালের ১৫ জুন (সাংহাই, চীন)"),
                        FactItem("সদর দপ্তর", "বেইজিং, চীন", "সচিবালয়: বেইজিং; RATS সদর দপ্তর: তাশখন্দ, উজবেকিস্তান"),
                        FactItem("সদস্য সংখ্যা", "১০টি রাষ্ট্র", "সর্বশেষ সদস্য: বেলারুশ (২০২৪); পূর্বে ইরান (২০২৩)"),
                        FactItem("ভারত ও পাকিস্তান অন্তর্ভুক্তি", "২০১৭ সালে", "আস্তানা শীর্ষ সম্মেলনে পূর্ণাঙ্গ সদস্যপদ লাভ করে")
                    ),
                    bangladeshRelevance = "বাংলাদেশ এসসিও-র ডায়ালগ পার্টনার (Dialogue Partner) হওয়ার জন্য আবেদন করেছে। মধ্য এশিয়া ও ইউরেশিয়ার উদীয়মান বাজারে বাণিজ্য প্রসারে এসসিও বাংলাদেশের জন্য অত্যন্ত গুরুত্বপূর্ণ।",
                    bcsTakeaways = listOf(
                        "SCO-এর সদর দপ্তর কোথায়? বেইজিং, চীন।",
                        "SCO-এর সন্ত্রাসবিরোধী শাখা RATS-এর সদর দপ্তর কোথায়? তাশখন্দ, উজবেকিস্তান।",
                        "SCO-এর ১০ম ও সর্বশেষ সদস্য রাষ্ট্র কোনটি? বেলারুশ (২০২৪)।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q_sc_1",
                                questionNumber = 1,
                                question = "সাংহাই সহযোগিতা সংস্থা (SCO)-এর বর্তমান সদস্য দেশ সংখ্যা কতটি?",
                                options = listOf("৮টি", "৯টি", "১০টি", "১২টি"),
                                correctIndex = 2,
                                explanation = "২০২৪ সালে বেলারুশ যুক্ত হওয়ায় এসসিও-র পূর্ণাঙ্গ সদস্য সংখ্যা দাঁড়িয়েছে ১০টিতে।"
                            )
                        ),
                        shortQuestions = listOf("SCO-এর RATS কাঠামোর মূল দায়িত্ব কী?"),
                        analyticalQuestion = "ইউরেশীয় ভূ-রাজনীতিতে পশ্চিমা আধিপত্যের বিকল্প হিসেবে এসসিও-র প্রাসঙ্গিকতা বিশ্লেষণ করুন।"
                    ),
                    reportedBy = listOf("প্রথম আলো", "The Daily Star", "বণিক বার্তা", "ঢাকা ট্রিবিউন")
                )
            ),
            crossConnections = listOf(
                CrossConnection(
                    id = "c_sc_1",
                    title = "এসসিও সম্মেলন ➔ ইউরেশীয় সংযোগ ➔ বিকল্প মুদ্রা লেনদেন",
                    connectionChain = "এসসিও শীর্ষ সম্মেলন ➔ নিজস্ব মুদ্রায় বাণিজ্য চুক্তি ➔ ডলারের ওপর নির্ভরতা হ্রাস (Dedollarization)।",
                    geopoliticalAnalysis = "আন্তর্জাতিক নিষেধাজ্ঞার মুখে থাকা দেশগুলো এসসিও-র মাধ্যমে বিকল্প আর্থিক করিডোর গড়তে চাইছে।"
                )
            ),
            bangladeshAngleSummary = "১৫ অক্টোবরের ঘটনাবলিতে এসসিও-র সম্মেলন বাংলাদেশের জন্য ইউরেশীয় বাণিজ্য সম্ভাবনার নতুন দুয়ার খুলে দিয়েছে। এছাড়া আন্তর্জাতিক নিরস্ত্রীকরণ কূটনীতিতে বাংলাদেশের সাংবিধানিক অঙ্গীকার পুনর্ব্যক্ত হয়েছে।",
            quickRevisions = listOf(
                QuickRevisionItem(1, "SCO-এর সদর দপ্তর কোথায় অবস্থিত?", "বেইজিং, চীন", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(2, "SCO-এর ১০ম সদস্য দেশ কোনটি?", "বেলারুশ (২০২৪ সালে অন্তর্ভুক্ত)", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(3, "ভারত ও পাকিস্তান কত সালে SCO-র সদস্যপদ পায়?", "২০১৭ সালে (আস্তানা শীর্ষ সম্মেলন)", "আন্তর্জাতিক সম্মেলন"),
                QuickRevisionItem(4, "RATS (Regional Anti-Terrorist Structure)-এর সদর দপ্তর কোথায়?", "তাশখন্দ, উজবেকিস্তান", "আন্তর্জাতিক সদর দপ্তর")
            ),
            mcqTest = listOf(
                McqQuestion(
                    id = "test_15_1",
                    questionNumber = 1,
                    question = "সাংহাই সহযোগিতা সংস্থা (SCO)-এর সদর দপ্তর কোন শহরে অবস্থিত?",
                    options = listOf("ক. সাংহাই", "খ. বেইজিং", "গ. মস্কো", "ঘ. আস্তানা"),
                    correctIndex = 1,
                    explanation = "সাংহাই সহযোগিতা সংস্থার সচিবালয় চীনের রাজধানী বেইজিংয়ে অবস্থিত।"
                )
            ),
            writtenVivaAngles = listOf(
                WrittenVivaItem(
                    id = "wv_sc_1",
                    issue = "এসসিও-তে বাংলাদেশের ডায়ালগ পার্টনারশিপ ও আঞ্চলিক সংযোগ",
                    examCategory = "BCS Written & Viva",
                    possibleQuestion = "মধ্য এশিয়া ও ইউরেশিয়ার সাথে বাংলাদেশের বাণিজ্যিক সংযোগে এসসিও-র গুরুত্ব আলোচনা করুন।",
                    keyPoints = listOf(
                        "জ্বালানি নিরাপত্তা ও মধ্য এশিয়ার খনিজ সম্পদের বাজারে প্রবেশাধিকার।",
                        "আন্তর্জাতিক উত্তর-দক্ষিণ পরিবহন করিডোর (INSTC)-এর সাথে সংযোগের সম্ভাবনা।"
                    ),
                    analyticalTip = "উত্তরে মধ্য এশিয়ার ৫টি 'স্তান' রাষ্ট্রের ভৌগোলিক অবস্থানের উল্লেখ করুন।"
                )
            ),
            sources = listOf(
                DailySource(
                    newspaperName = "প্রথম আলো",
                    englishName = "Prothom Alo",
                    section = "আন্তর্জাতিক",
                    articlesCovered = listOf(
                        SourceArticle("ইসলামাবাদে এসসিও শীর্ষ সম্মেলন শুরু", "এসসিও সম্মেলন")
                    )
                ),
                DailySource(
                    newspaperName = "The Daily Star",
                    englishName = "The Daily Star",
                    section = "International",
                    articlesCovered = listOf(
                        SourceArticle("SCO summit focuses on regional security and trade", "SCO Summit")
                    )
                ),
                DailySource(
                    newspaperName = "বণিক বার্তা",
                    englishName = "Bonik Barta",
                    section = "বাণিজ্য ও কূটনীতি",
                    articlesCovered = listOf(
                        SourceArticle("এসসিও দেশগুলোর নিজস্ব মুদ্রায় লেনদেন বৃদ্ধির তাগিদ", "এসসিও অর্থনীতি")
                    )
                ),
                DailySource(
                    newspaperName = "ঢাকা ট্রিবিউন",
                    englishName = "Dhaka Tribune",
                    section = "World",
                    articlesCovered = listOf(
                        SourceArticle("Jaishankar attends SCO summit in Pakistan", "SCO diplomacy")
                    )
                )
            )
        )
    }

    /**
     * Archive report for ১৪ অক্টোবর ২০২৬ (Additional archive day)
     */
    val report14Oct2026: DailyReport by lazy {
        DailyReport(
            id = "2026-10-14",
            banglaDate = "১৪ অক্টোবর ২০২৬",
            banglaTime = "৫:৪৫ PM BST",
            rawDateIso = "2026-10-14",
            heading = "BCS International Affairs — ১৪ অক্টোবর ২০২৬",
            topHeadlines = listOf(
                "আন্তর্জাতিক জ্বালানি সংস্থা (IEA)-এর বৈশ্বিক তেল বাজার প্রতিবেদন ২০২৬",
                "তাইওয়ান প্রণালীতে সামরিক মহড়া ও ওয়াশিংটন-বেইজিং উত্তাপ",
                "আসিয়ান (ASEAN) সমুদ্র নিরাপত্তা সম্মেলন ও দক্ষিণ চীন সাগর আচরণবিধি (COC)"
            ),
            dayComparison = DayComparison(
                whatsNew = listOf(
                    "আইইএ (IEA) পূর্বাভাস দিয়েছে যে ২০৩০ সালের মধ্যে নবায়নযোগ্য জ্বালানির অংশ বৈশ্বিক উৎপাদনে ৫০% ছাড়িয়ে যাবে।"
                ),
                ongoingIssues = listOf(
                    "দক্ষিণ চীন সাগরে ফিলিপাইন ও চীনের উপকূলরক্ষী বাহিনীর জলকামান নিক্ষেপ ও সংঘর্ষ।"
                ),
                escalatedIssues = listOf(
                    "তাইওয়ান প্রণালীতে সামরিক টহল বৃদ্ধি বৈশ্বিক সেমিকন্ডাক্টর সরবরাহ শৃঙ্খলে নতুন উদ্বেগ সৃষ্টি করেছে।"
                )
            ),
            topics = listOf(
                TopicAnalysis(
                    id = "t_iea_1",
                    title = "আইইএ বৈশ্বিক জ্বালানি দৃষ্টিভঙ্গি ও জীবাশ্ম জ্বালানির ভবিষ্যৎ (IEA Global Energy Outlook)",
                    category = "জ্বালানি রাজনীতি ও ভূ-অর্থনীতি",
                    importanceRank = 1,
                    todaysEvent = "আন্তর্জাতিক জ্বালানি সংস্থা (IEA) তাদের বার্ষিক প্রতিবেদনে জানিয়েছে, সৌর ও বায়ু বিদ্যুতের দ্রুত প্রসারের ফলে জীবাশ্ম জ্বালানির চাহিদা ২০৩০ সালের মধ্যেই সর্বোচ্চে (Peak Demand) পৌঁছে হ্রাস পেতে শুরু করবে।",
                    whyImportant = "বৈশ্বিক পেট্রো-ডলার ব্যবস্থা এবং মধ্যপ্রাচ্যের তেলসমৃদ্ধ দেশগুলোর ওপর নির্ভরতা কমার ঐতিহাসিক সূচনা নির্দেশ করে।",
                    background = "১৯৭৩-৭৪ সালের আরব তেল অবরোধ ও প্রথম বৈশ্বিক জ্বালানি সংকটের পর ১৯৭৪ সালে ওইসিডি (OECD)-র কাঠামোর অধীনে প্যারিসে আইইএ প্রতিষ্ঠিত হয়।",
                    importantEntities = ImportantEntities(
                        persons = listOf("ফাতিহ বিরল (Executive Director, IEA)"),
                        countries = listOf("ফ্রান্স", "যুক্তরাষ্ট্র", "সৌদি আরব"),
                        organizations = listOf("IEA (আন্তর্জাতিক জ্বালানি সংস্থা)", "OPEC (ওপেক)"),
                        alliances = listOf("OECD"),
                        institutions = listOf("IEA Secretariat, Paris")
                    ),
                    importantFacts = listOf(
                        FactItem("প্রতিষ্ঠা", "IEA", "১৯৭৪ সালে (প্রথম তেল সংকটের পর)"),
                        FactItem("সদর দপ্তর", "প্যারিস, ফ্রান্স", "OECD কাঠামোভুক্ত স্বায়ত্তশাসিত সংস্থা"),
                        FactItem("সদস্য সংখ্যা", "৩১টি সদস্য দেশ", "মূলত জ্বালানি আমদানিকারক উন্নত রাষ্ট্রসমূহ")
                    ),
                    bangladeshRelevance = "বাংলাদেশ আমদানিকৃত এলএনজি ও জ্বালানি তেলের ওপর অত্যন্ত নির্ভরশীল। বৈশ্বিক জ্বালানি রূপান্তর বাংলাদেশে পরিচ্ছন্ন প্রযুক্তির সহজলভ্যতা নিশ্চিত করতে পারে।",
                    bcsTakeaways = listOf(
                        "আন্তর্জাতিক জ্বালানি সংস্থা (IEA)-এর সদর দপ্তর কোথায়? প্যারিস, ফ্রান্স।",
                        "IEA কত সালে প্রতিষ্ঠিত হয়? ১৯৭৪ সালে।"
                    ),
                    potentialQuestions = TopicQuestions(
                        mcqs = listOf(
                            McqQuestion(
                                id = "q_iea_1",
                                questionNumber = 1,
                                question = "আন্তর্জাতিক জ্বালানি সংস্থা (IEA) কত সালে প্রতিষ্ঠিত হয়?",
                                options = listOf("১৯৬০", "১৯৭৩", "১৯৭৪", "১৯৮২"),
                                correctIndex = 2,
                                explanation = "১৯৭৩ সালের আরব তেল সংকটের প্রতিক্রিয়ায় ১৯৭৪ সালে প্যারিসে আইইএ গঠিত হয়।"
                            )
                        ),
                        shortQuestions = listOf("IEA এবং OPEC-এর মধ্যে মৌলিক পার্থক্য কী?"),
                        analyticalQuestion = "নবায়নযোগ্য জ্বালানির বিকাশ কীভাবে মধ্যপ্রাচ্যের ঐতিহ্যবাহী ভূ-রাজনৈতিক ক্ষমতা খর্ব করতে পারে?"
                    ),
                    reportedBy = listOf("বণিক বার্তা", "The Daily Star")
                )
            ),
            crossConnections = listOf(
                CrossConnection(
                    id = "c_iea_1",
                    title = "জ্বালানি রূপান্তর ➔ ওপেক প্লাস প্রতিক্রিয়া ➔ বৈশ্বিক মুদ্রাস্ফীতি",
                    connectionChain = "নবায়নযোগ্য জ্বালানি প্রবৃদ্ধি ➔ ওপেক প্লাসের তেল উৎপাদন হ্রাস ➔ তেলের দাম ধরে রাখার চেষ্টা ➔ আমদানিকারক দেশের মুদ্রা সংকট।",
                    geopoliticalAnalysis = "জ্বালানি রূপান্তরের যুগে ঐতিহ্যবাহী তেল রপ্তানিকারক দেশগুলো নিজেদের রাজস্ব বাঁচাতে উৎপাদন কাটছাঁটের নীতি ব্যবহার করছে।"
                )
            ),
            bangladeshAngleSummary = "১৪ অক্টোবরের আন্তর্জাতিক জ্বালানি প্রতিবেদন বাংলাদেশের জাতীয় বিদ্যুৎ ও জ্বালানি মহাপরিকল্পনায় নবায়নযোগ্য শক্তির হিস্যা বৃদ্ধির প্রয়োজনীয়তা তুলে ধরেছে।",
            quickRevisions = listOf(
                QuickRevisionItem(1, "IEA (International Energy Agency)-এর সদর দপ্তর কোথায়?", "প্যারিস, ফ্রান্স", "আন্তর্জাতিক সদর দপ্তর"),
                QuickRevisionItem(2, "পেট্রোলিয়াম রপ্তানিকারক দেশসমূহের সংস্থা (OPEC) কত সালে গঠিত হয়?", "১৯৬০ সালে (বাগদাদ সম্মেলন)", "আন্তর্জাতিক সংস্থা"),
                QuickRevisionItem(3, "OPEC-এর সদর দপ্তর কোন শহরে অবস্থিত?", "ভিয়েনা, অস্ট্রিয়া (যদিও অস্ট্রিয়া সদস্য নয়)", "আন্তর্জাতিক সদর দপ্তর")
            ),
            mcqTest = listOf(
                McqQuestion(
                    id = "test_14_1",
                    questionNumber = 1,
                    question = "আন্তর্জাতিক জ্বালানি সংস্থা (IEA)-এর সদর দপ্তর কোথায় অবস্থিত?",
                    options = listOf("ক. লন্ডন", "খ. ভিয়েনা", "গ. প্যারিস", "ঘ. ব্রাসেলস"),
                    correctIndex = 2,
                    explanation = "আন্তর্জাতিক জ্বালানি সংস্থা (IEA)-এর সদর দপ্তর ফ্রান্সের প্যারিসে অবস্থিত।"
                )
            ),
            writtenVivaAngles = listOf(
                WrittenVivaItem(
                    id = "wv_iea_1",
                    issue = "বৈশ্বিক জ্বালানি ট্রানজিশন ও উন্নয়নশীল দেশের সক্ষমতা",
                    examCategory = "BCS Written",
                    possibleQuestion = "কপ২৮-এ গৃহীত জীবাশ্ম জ্বালানি থেকে সরে আসার (Transitioning away from fossil fuels) অঙ্গীকার উন্নয়নশীল অর্থনীতিগুলোর জন্য কতটা বাস্তবসম্মত?",
                    keyPoints = listOf(
                        "প্রযুক্তি হস্তান্তর ও জলবায়ু তহবিলের অপর্যাপ্ততা।",
                        "গ্রিড আধুনিকায়ন এবং বিদ্যুৎ সঞ্চালন লাইনে বিপুল বিনিয়োগের প্রয়োজনীয়তা।"
                    ),
                    analyticalTip = "লস্ট অ্যান্ড ড্যামেজ ফান্ডের (Loss and Damage Fund) প্রসঙ্গ টানুন।"
                )
            ),
            sources = listOf(
                DailySource(
                    newspaperName = "বণিক বার্তা",
                    englishName = "Bonik Barta",
                    section = "জ্বালানি ও আন্তর্জাতিক বাণিজ্য",
                    articlesCovered = listOf(
                        SourceArticle("আইইএর তেল প্রতিবেদন: ২০৩০ এর মধ্যেই পিক ডিমান্ডের ইঙ্গিত", "IEA Oil Report")
                    )
                ),
                DailySource(
                    newspaperName = "The Daily Star",
                    englishName = "The Daily Star",
                    section = "Business / World",
                    articlesCovered = listOf(
                        SourceArticle("IEA sees fossil fuel demand peaking by end of decade", "Energy transition")
                    )
                )
            )
        )
    }

    /**
     * Dynamically builds today's BCS daily report based on live Bangladesh Standard Time (BST),
     * ensuring that whenever the app is opened on any device, today's actual date is immediately shown
     * with an entire daily overhaul across all 4 major sections:
     * 1. Bulletin (5 Subjective Analysis Topics with 8-part breakdown)
     * 2. Revision (20 High-yield One-liners)
     * 3. Daily MCQ Test (10-15 Exam questions)
     * 4. Written & Viva Questions (5 analytical questions)
     */
    fun generateDynamicTodayReport(
        dateLabel: String = BangladeshTimeHelper.getFormattedBstDate(),
        timeLabel: String = "০৬:০০ AM BST",
        headlines: List<String> = emptyList(),
        customSources: List<DailySource> = emptyList()
    ): DailyReport {
        val todayKey = BangladeshTimeHelper.getTodayEditionKey()
        val defaultSources = ManagedNewspaper.defaultSources
        val headlinesMap = if (headlines.isNotEmpty()) {
            mapOf("src_prothom_alo" to headlines)
        } else {
            emptyMap()
        }

        return DailyBcsOverhaulEngine.buildFullOverhaulReport(
            dateLabel = dateLabel,
            timeLabel = timeLabel,
            editionKey = todayKey,
            activeSources = defaultSources,
            headlinesMap = headlinesMap,
            geminiRawText = null
        )
    }

    /**
     * List of all available daily reports, with today's live report prioritized at index 0
     */
    fun getAllReports(): List<DailyReport> {
        val liveReport = generateDynamicTodayReport()
        return listOf(liveReport, report16Oct2026, report15Oct2026, report14Oct2026)
    }

    fun getReportById(id: String): DailyReport {
        return getAllReports().firstOrNull { it.id == id } ?: generateDynamicTodayReport()
    }
}
