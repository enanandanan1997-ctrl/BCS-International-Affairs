package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.QuizScoreRecord
import com.example.data.local.SavedNewsTopic
import com.example.model.QuizQuestion
import com.example.model.TopicAnalysis
import kotlinx.coroutines.flow.Flow

class BcsRepository(private val database: AppDatabase) {

    val savedNews: Flow<List<SavedNewsTopic>> = database.savedNewsDao().getAllSavedNews()
    val quizHistory: Flow<List<QuizScoreRecord>> = database.quizScoreDao().getAllScores()
    val totalQuizzesTaken: Flow<Int> = database.quizScoreDao().getTotalQuizzesTaken()
    val averagePercentage: Flow<Double?> = database.quizScoreDao().getAveragePercentage()

    suspend fun saveTopic(topic: TopicAnalysis) {
        val entity = SavedNewsTopic(
            title = topic.title,
            content = buildString {
                appendLine("মূল ঘটনা: ${topic.event}")
                appendLine("\nগুরুত্ব: ${topic.significance}")
                appendLine("\nপ্রেক্ষাপট: ${topic.historicalBackground}")
                appendLine("\nগুরুত্বপূর্ণ তথ্য:")
                topic.keyTermsAndData.forEach { appendLine("• $it") }
                appendLine("\nবিসিএস প্রশ্নাবলী:")
                topic.bcsQuestions.forEach { appendLine("Q: $it") }
            },
            date = topic.date,
            source = topic.source,
            offlineReading = true
        )
        database.savedNewsDao().insertTopic(entity)
    }

    suspend fun deleteSavedTopic(title: String) {
        database.savedNewsDao().deleteByTitle(title)
    }

    suspend fun deleteSavedEntity(topic: SavedNewsTopic) {
        database.savedNewsDao().deleteTopic(topic)
    }

    fun isTopicSaved(title: String): Flow<Boolean> {
        return database.savedNewsDao().isTopicSavedFlow(title)
    }

    suspend fun recordQuizScore(topicTitle: String, score: Int, total: Int) {
        val percentage = if (total > 0) ((score.toDouble() / total.toDouble()) * 100).toInt() else 0
        val record = QuizScoreRecord(
            topicTitle = topicTitle,
            score = score,
            totalQuestions = total,
            percentage = percentage
        )
        database.quizScoreDao().insertScore(record)
    }

    fun getDailyTopics(): List<TopicAnalysis> {
        return sampleDailyTopics
    }

    fun getAllQuizQuestions(): List<QuizQuestion> {
        return sampleDailyTopics.flatMap { it.quizQuestions }
    }

    companion object {
        val sampleDailyTopics = listOf(
            TopicAnalysis(
                id = "topic_1",
                title = "বঙ্গবন্ধু টানেল ও কর্ণফুলী রিভার করিডোর: বাণিজ্যিক নতুন দিগন্ত",
                category = "বাংলাদেশ বিষয়াবলী (অবকাঠামো ও অর্থনীতি)",
                date = "১৭ অক্টোবর ২০২৬",
                source = "দৈনিক ইত্তেফাক ও পরিকল্পনা কমিশন",
                summary = "দক্ষিণ এশিয়ার প্রথম নদীর তলদেশ দিয়ে নির্মিত বঙ্গবন্ধু শেখ মুজিবুর রহমান টানেল দেশের অর্থনীতি ও মাতারবাড়ি গভীর সমুদ্রবন্দরের ট্রানজিট হাবে নতুন মাত্রা যোগ করেছে।",
                event = "কর্ণফুলী নদীর তলদেশ দিয়ে নির্মিত বঙ্গবন্ধু টানেলে দৈনিক গড়ে ২০ হাজার যানবাহনের চলাচল এবং চট্টগ্রাম-কক্সবাজারের মধ্যে অর্থনৈতিক করিডোর পূর্ণাঙ্গ রূপ ধারণ করেছে।",
                significance = "মাতারবাড়ি গভীর সমুদ্র বন্দর এবং মিরসরাই বঙ্গবন্ধু শিল্পনগরের সাথে দ্রুত যোগাযোগ নিশ্চিত করায় বাংলাদেশের জিডিপিতে প্রায় ০.১৬% বার্ষিক প্রবৃদ্ধি যুক্ত হচ্ছে। এটি ট্রান্স-এশিয়ান রেলওয়ে ও হাইওয়ের সাথেও সংযুক্ত।",
                historicalBackground = "২০১৬ সালে চীনের রাষ্ট্রপতি শি জিনপিং এবং বাংলাদেশের প্রধানমন্ত্রীর উপস্থিতিতে চুক্তি স্বাক্ষরিত হয়। ২০১৯ সালের ফেব্রুয়ারিতে খননকাজ শুরু হয়ে ২০২৩ সালের অক্টোবরে প্রথম উদ্বোধন হয়। এটি চায়না কমিউনিকেশন কনস্ট্রাকশন কোম্পানি (CCCC) কর্তৃক নির্মিত হয়।",
                keyTermsAndData = listOf(
                    "টানেলের মোট দৈর্ঘ্য: ৩.৩২ কিলোমিটার (মূল টিউব ২.৪৫ কিমি)",
                    "টিউব সংখ্যা: ২টি (প্রতিটিতে ২টি করে মোট ৪ লেন)",
                    "অর্থায়ন: বাংলাদেশ সরকার ও চীনের এক্সিম ব্যাংক",
                    "নদীর পানির তলদেশ থেকে গভীরতা: ৩১ মিটার"
                ),
                bcsQuestions = listOf(
                    "দক্ষিণ এশিয়ার প্রথম আন্ডারওয়াটার টানেল কোন দেশে অবস্থিত? (উত্তর: বাংলাদেশ)",
                    "বঙ্গবন্ধু টানেল কোন দুটি এলাকাকে সংযুক্ত করেছে? (উত্তর: পতেঙ্গা ও আনোয়ারাকে)",
                    "টানেলের অর্থায়নে কোন বিদেশী সংস্থা অংশ নিয়েছিল? (উত্তর: এক্সিম ব্যাংক অব চায়না)"
                ),
                keyPersonalities = listOf(
                    "চায়না কমিউনিকেশন কনস্ট্রাকশন কোম্পানি (CCCC)",
                    "বাংলাদেশ সেতু কর্তৃপক্ষ (BBA)",
                    "সড়ক পরিবহন ও সেতু মন্ত্রণালয়"
                ),
                dataCapsule = mapOf(
                    "নদী" to "কর্ণফুলী",
                    "মূল দৈর্ঘ্য" to "৩.৩২ কিমি",
                    "সংযোগ" to "পতেঙ্গা — আনোয়ারা",
                    "মডেল" to "Shield TBM ড্রিলিং"
                ),
                examTips = "লিখিত পরীক্ষার জন্য চট্টগ্রাম কেন্দ্রিক ওয়ান সিটি টু টাউন মডেল এবং চীনের সাথে দ্বিপাক্ষিক সম্পর্কের ক্ষেত্রে এই টানেলের উপযোগিতা উদাহরণ হিসেবে উল্লেখ করবেন।",
                quizQuestions = listOf(
                    QuizQuestion(
                        id = "q1_1",
                        topicTitle = "বঙ্গবন্ধু টানেল ও কর্ণফুলী রিভার করিডোর",
                        question = "বঙ্গবন্ধু শেখ মুজিবুর রহমান টানেল কোন দুটি স্থানকে সরাসরি সংযুক্ত করেছে?",
                        options = listOf(
                            "পতেঙ্গা এবং আনোয়ারা",
                            "হালিশহর এবং বোয়ালখালী",
                            "সীতাকুণ্ড এবং সন্দ্বীপ",
                            "পতেঙ্গা এবং বাঁশখালী"
                        ),
                        correctIndex = 0,
                        explanation = "বঙ্গবন্ধু টানেল চট্টগ্রামের পতেঙ্গা থেকে কর্ণফুলী নদীর তলদেশ দিয়ে আনোয়ারার সাথে সরাসরি সংযুক্ত করেছে।",
                        category = "বাংলাদেশ বিষয়াবলী"
                    ),
                    QuizQuestion(
                        id = "q1_2",
                        topicTitle = "বঙ্গবন্ধু টানেল ও কর্ণফুলী রিভার করিডোর",
                        question = "বঙ্গবন্ধু টানেলের মূল দৈর্ঘ্য কত?",
                        options = listOf(
                            "২.৪৫ কিলোমিটার",
                            "৩.৩২ কিলোমিটার",
                            "৪.১৫ কিলোমিটার",
                            "৫.১০ কিলোমিটার"
                        ),
                        correctIndex = 1,
                        explanation = "অ্যাপ্রোচ রোডসহ মোট প্রকল্পের দৈর্ঘ্য ৯.৩৯ কিমি এবং টানেলের মূল কাঠামোর দৈর্ঘ্য ৩.৩২ কিলোমিটার (মূল টিউব ২.৪৫ কিমি)।",
                        category = "বাংলাদেশ বিষয়াবলী"
                    ),
                    QuizQuestion(
                        id = "q1_3",
                        topicTitle = "বঙ্গবন্ধু টানেল ও কর্ণফুলী রিভার করিডোর",
                        question = "কর্ণফুলী টানেল নির্মাণকারী আন্তর্জাতিক ঠিকাদারি প্রতিষ্ঠান কোনটি?",
                        options = listOf(
                            "জাপান ইন্টারন্যাশনাল কো-অপারেশন (JICA)",
                            "চায়না কমিউনিকেশনস কনস্ট্রাকশন কোম্পানি (CCCC)",
                            "হুন্দাই কনস্ট্রাকশন সাউথ কোরিয়া",
                            "লারসেন অ্যান্ড তুব্রো (L&T)"
                        ),
                        correctIndex = 1,
                        explanation = "চীনের রাষ্ট্রায়ত্ত প্রতিষ্ঠান China Communications Construction Company (CCCC) এই টানেলের মূল নির্মাণকাজ সম্পন্ন করে।",
                        category = "বাংলাদেশ বিষয়াবলী"
                    )
                )
            ),
            TopicAnalysis(
                id = "topic_2",
                title = "ব্রিকস (BRICS) সম্প্রসারণ ও বৈশ্বিক ডি-ডলারাইজেশন রূপরেখা",
                category = "আন্তর্জাতিক বিষয়াবলী (ভূ-রাজনীতি ও অর্থনীতি)",
                date = "১৭ অক্টোবর ২০২৬",
                source = "রয়টার্স ও ফরেন পলিসি জার্নাল",
                summary = "ব্রিকস ব্লকের নতুন সদস্য অন্তর্ভুক্তি এবং স্থানীয় মুদ্রায় দ্বিপাক্ষিক বাণিজ্যের বিস্তার ডলারের একাধিপত্য ও বৈশ্বিক অর্থনৈতিক ভারসাম্যকে পুনর্গঠন করছে।",
                event = "ব্রিকস সম্মেলনে সদস্য রাষ্ট্রগুলো আন্তঃসীমান্ত বাণিজ্যে 'ব্রিকস পে' (BRICS Pay) ডিজিটাল সেটেলমেন্ট সিস্টেম চালুর ঘোষণা দিয়েছে।",
                significance = "আন্তর্জাতিক বাণিজ্যে সুইফট (SWIFT) নির্ভরতা হ্রাস এবং গ্লোবাল সাউথের দেশগুলোর মধ্যে স্বনির্ভর আর্থিক প্রবাহ সৃষ্টিতে এটি যুগান্তকারী পদক্ষেপ। বৈশ্বিক তেলের প্রায় ৪২% উৎপাদন এখন ব্রিকসভুক্ত দেশগুলোর নিয়ন্ত্রণে।",
                historicalBackground = "২০০৬ সালে গোল্ডম্যান স্যাকসের অর্থনীতিবিদ জিম ও'নিল প্রথম 'BRIC' পরিভাষা প্রস্তাব করেন। ২০০৯ সালে প্রথম সম্মেলন হয় রাশিয়ায়। ২০১০ সালে দক্ষিণ আফ্রিকা যোগ দিয়ে BRICS হয়। ২০২৪ সালে মিশর, ইথিওপিয়া, ইরান, সংযুক্ত আরব আমিরাত অন্তর্ভুক্ত হয়।",
                keyTermsAndData = listOf(
                    "নিউ ডেভেলপমেন্ট ব্যাংক (NDB): সদর দপ্তর সাংহাই, চীন",
                    "ডি-ডলারাইজেশন (De-dollarization): রিজার্ভে ডলার ব্যতিরেকে বিকল্প মুদ্রার প্রচলন",
                    "BRICS Pay: ব্লকচেইন ভিত্তিক বিকেন্দ্রীকৃত আর্থিক লেনদেন নেটওয়ার্ক"
                ),
                bcsQuestions = listOf(
                    "ব্রিকস (BRICS) এর সদর দপ্তর বা ব্যাংকের নাম কি? (উত্তর: নিউ ডেভেলপমেন্ট ব্যাংক - NDB, সাংহাই)",
                    "কোন অর্থনীতিবিদ সর্বপ্রথম 'BRIC' ধারণার প্রবর্তন করেন? (উত্তর: জিম ও'নিল)",
                    "২০২৪ সালে আনুষ্ঠানিকভাবে ব্রিকসের সদস্যপদ লাভ করে কোন দেশগুলো? (উত্তর: মিশর, ইথিওপিয়া, ইরান, ইউএই)"
                ),
                keyPersonalities = listOf(
                    "জিম ও'নিল (অর্থনীতিবিদ)",
                    "দিলমা রুসেফ (প্রেসিডেন্ট, নিউ ডেভেলপমেন্ট ব্যাংক)"
                ),
                dataCapsule = mapOf(
                    "ব্যাংক" to "New Development Bank (NDB)",
                    "সদর দপ্তর" to "সাংহাই, চীন",
                    "প্রথম শীর্ষ সম্মেলন" to "২০০৯ (ইয়েকাতেরিনবুর্গ, রাশিয়া)",
                    "বর্তমান সদস্য" to "৯+ দেশ"
                ),
                examTips = "বিসিএস লিখিত পরীক্ষায় 'বহুমেধা বিশ্বব্যবস্থা (Multipolar World Order)' এবং 'ডলার বনাম বিকল্প মুদ্রা' প্রশ্নে ব্রিকসের এনডিবিকে কার্যকর উদাহরণ বানাবেন।",
                quizQuestions = listOf(
                    QuizQuestion(
                        id = "q2_1",
                        topicTitle = "ব্রিকস সম্প্রসারণ ও ডি-ডলারাইজেশন",
                        question = "ব্রিকস প্রতিষ্ঠিত নিউ ডেভেলপমেন্ট ব্যাংক (NDB)-এর সদর দপ্তর কোথায় অবস্থিত?",
                        options = listOf(
                            "বেইজিং, চীন",
                            "সাংহাই, চীন",
                            "মস্কো, রাশিয়া",
                            "জোহানেসবার্গ, দক্ষিণ আফ্রিকা"
                        ),
                        correctIndex = 1,
                        explanation = "নিউ ডেভেলপমেন্ট ব্যাংক (NDB)-এর সদর দপ্তর চীনের সাংহাই শহরে অবস্থিত।",
                        category = "আন্তর্জাতিক বিষয়াবলী"
                    ),
                    QuizQuestion(
                        id = "q2_2",
                        topicTitle = "ব্রিকস সম্প্রসারণ ও ডি-ডলারাইজেশন",
                        question = "'BRIC' শব্দটি ২০০১ সালে প্রথম কোন অর্থনীতিবিদ ব্যবহার করেন?",
                        options = listOf(
                            "অমর্ত্য সেন",
                            "জিম ও'নিল (Jim O'Neill)",
                            "পল ক্রুগম্যান",
                            "জোসেফ স্টিগলিটজ"
                        ),
                        correctIndex = 1,
                        explanation = "গোল্ডম্যান স্যাকসের প্রধান অর্থনীতিবিদ জিম ও'নিল ২০০১ সালে উদীয়মান চার অর্থনীতি নিয়ে BRIC পরিভাষা তৈরি করেন।",
                        category = "আন্তর্জাতিক বিষয়াবলী"
                    ),
                    QuizQuestion(
                        id = "q2_3",
                        topicTitle = "ব্রিকস সম্প্রসারণ ও ডি-ডলারাইজেশন",
                        question = "নিচের কোন দেশ ২০২৪ সালে ব্রিকসের নতুন সদস্যপদ গ্রহণ করে?",
                        options = listOf(
                            "আর্জেন্টিনা",
                            "মিশর ও সংযুক্ত আরব আমিরাত",
                            "তুরস্ক ও নাইজেরিয়া",
                            "ইন্দোনেশিয়া ও মালয়েশিয়া"
                        ),
                        correctIndex = 1,
                        explanation = "২০২৪ সালের ১ জানুয়ারি মিশর, ইথিওপিয়া, ইরান এবং সংযুক্ত আরব আমিরাত আনুষ্ঠানিকভাবে ব্রিকসের পূর্ণাঙ্গ সদস্যপদ পায়।",
                        category = "আন্তর্জাতিক বিষয়াবলী"
                    )
                )
            ),
            TopicAnalysis(
                id = "topic_3",
                title = "কপ-২৯ (COP-29) এবং লস অ্যান্ড ড্যামেজ ফান্ড কার্যকরীকরণ",
                category = "ভূগোল, পরিবেশ ও দুর্যোগ ব্যবস্থাপনা",
                date = "১৬ অক্টোবর ২০২৬",
                source = "UNFCCC ও বাংলাদেশ ক্লাইমেট ট্রাস্ট",
                summary = "জলবায়ু পরিবর্তনের ফলে ক্ষতিগ্রস্ত উন্নয়নশীল দেশগুলোর জন্য ঘোষিত 'Loss and Damage Fund'-এ অনুদান বিতরণ কাঠামো চূড়ান্ত হয়েছে।",
                event = "বাকু, আজারবাইজানে অনুষ্ঠিত জলবায়ু সম্মেলনে ক্ষতিগ্রস্ত দ্বীপরাষ্ট্র ও উপকূলীয় দেশগুলোর জন্য ১০০ বিলিয়ন ডলার তহবিল মোবিলাইজেশন চুক্তি স্বাক্ষরিত হয়।",
                significance = "বাংলাদেশ ক্লাইমেট ভালনারেবল ফোরামের (CVF) অন্যতম নেতৃত্বস্থানীয় দেশ হওয়ায় উপকূলীয় নিম্নাঞ্চল ও সুন্দরবন সুরক্ষায় উল্লেখযোগ্য আর্থিক ও কারিগরি সহায়তা পাবে।",
                historicalBackground = "১৯৯২ সালের রিও ধরিত্রী সম্মেলনের ধারাবাহিকতায় ১৯৯৫ সালে প্রথম বার্লিনে কপ-১ অনুষ্ঠিত হয়। ২০১৫ সালের প্যারিস চুক্তির (COP-21) মাধ্যমে বৈশ্বিক উষ্ণতা ১.৫ ডিগ্রি সেলসিয়াসের মধ্যে সীমাবদ্ধ রাখার লক্ষ্য নির্ধারণ করা হয়।",
                keyTermsAndData = listOf(
                    "Loss and Damage Fund: কপ-২৭ (শার্ম আল-শেখ) এ প্রথম নীতিগত সম্মতি",
                    "Host Country of COP-29: বাকু, আজারবাইজান",
                    "প্যারিস চুক্তি: ২০১৫ (কপ-২১)",
                    "CVF (Climate Vulnerable Forum): গঠিত ২০০৯ সালে মালদ্বীপের মালেতে"
                ),
                bcsQuestions = listOf(
                    "কপ (COP) এর পূর্ণরূপ কি? (উত্তর: Conference of the Parties)",
                    "লস অ্যান্ড ড্যামেজ ফান্ড গঠনের চূড়ান্ত সিদ্ধান্ত কোন কপে গৃহীত হয়? (উত্তর: COP-27, মিশর)",
                    "জাতিসংঘ জলবায়ু পরিবর্তন ফ্রেমওয়ার্ক কনভেনশন (UNFCCC) কবে স্বাক্ষরিত হয়? (উত্তর: ১৯৯২ সালে রিও ডি জেনিরোতে)"
                ),
                keyPersonalities = listOf(
                    "সাইমন স্টিয়েল (নির্বাহী সচিব, UNFCCC)",
                    "মুখতার বাবায়েভ (সভাপতি, কপ-২৯)"
                ),
                dataCapsule = mapOf(
                    "UNFCCC সদর দপ্তর" to "বন, জার্মানি",
                    "প্যারিস চুক্তি কার্যকর" to "৪ নভেম্বর ২০১৬",
                    "কপ-২৯ ভেন্যু" to "বাকু, আজারবাইজান",
                    "১.৫°C লক্ষ্যমাত্রা" to "প্যারিস জলবায়ু চুক্তি"
                ),
                examTips = "বিসিএস লিখিত সাধারণ বিজ্ঞান ও ভূগোল অংশে 'জলবায়ু পরিবর্তন ও বাংলাদেশের অভিঘাত' সম্পর্কিত ১০ নম্বরের প্রশ্নে কপ-২৯ এর লস অ্যান্ড ড্যামেজ ফান্ডের বণ্টন পদ্ধতি উল্লেখ করবেন।",
                quizQuestions = listOf(
                    QuizQuestion(
                        id = "q3_1",
                        topicTitle = "কপ-২৯ ও লস অ্যান্ড ড্যামেজ ফান্ড",
                        question = "জাতিসংঘ পরিবেশ বিষয়ক UNFCCC-এর পূর্ণরূপ কোনটি?",
                        options = listOf(
                            "United Nations Forum for Carbon Reduction Control",
                            "United Nations Framework Convention on Climate Change",
                            "Universal Nations Foundation on Climate Cooperation",
                            "United Network for Conservation and Forest Care"
                        ),
                        correctIndex = 1,
                        explanation = "UNFCCC এর পূর্ণরূপ হলো United Nations Framework Convention on Climate Change (১৯৯২)।",
                        category = "ভূগোল ও পরিবেশ"
                    ),
                    QuizQuestion(
                        id = "q3_2",
                        topicTitle = "কপ-২৯ ও লস অ্যান্ড ড্যামেজ ফান্ড",
                        question = "ঐতিহাসিক প্যারিস জলবায়ু চুক্তি (Paris Agreement) কোন COP সম্মেলনে স্বাক্ষরিত হয়?",
                        options = listOf(
                            "COP-15 (কোপেনহেগেন)",
                            "COP-21 (প্যারিস, ২০১৫)",
                            "COP-26 (গ্লাসগো)",
                            "COP-28 (দুবাই)"
                        ),
                        correctIndex = 1,
                        explanation = "২০১৫ সালে প্যারিসে অনুষ্ঠিত COP-21 সম্মেলনে বিশ্বনেতারা ঐতিহাসিক প্যারিস জলবায়ু চুক্তি স্বাক্ষর করেন।",
                        category = "ভূগোল ও পরিবেশ"
                    ),
                    QuizQuestion(
                        id = "q3_3",
                        topicTitle = "কপ-২৯ ও লস অ্যান্ড ড্যামেজ ফান্ড",
                        question = "জলবায়ু পরিবর্তনের ক্ষতিপূরণ হিসেবে 'Loss and Damage Fund' আনুষ্ঠানিকভাবে চালুর নীতিগত সিদ্ধান্ত কোন সম্মেলনে হয়?",
                        options = listOf(
                            "COP-26 (যুক্তরাজ্য)",
                            "COP-27 (শার্ম আল-শেখ, মিশর)",
                            "COP-25 (মাদ্রিদ)",
                            "COP-22 (মরক্কো)"
                        ),
                        correctIndex = 1,
                        explanation = "২০২২ সালে মিশরের শার্ম আল-শেখে অনুষ্ঠিত COP-27 সম্মেলনে প্রথমবারের মতো ঐতিহাসিক Loss and Damage Fund নীতিগতভাবে গৃহীত হয়।",
                        category = "ভূগোল ও পরিবেশ"
                    )
                )
            ),
            TopicAnalysis(
                id = "topic_4",
                title = "কৃত্রিম বুদ্ধিমত্তা ও সাইবার নিরাপত্তা আইন: বাংলাদেশের জাতীয় প্রস্তুতি",
                category = "কম্পিউটার ও তথ্যপ্রযুক্তি",
                date = "১৫ অক্টোবর ২০২৬",
                source = "তথ্য ও যোগাযোগ প্রযুক্তি বিভাগ (ICT Division)",
                summary = "জাতীয় এআই কৌশল ২০২৬ ও সাইবার সুরক্ষা অধ্যাদেশ অনুসারে সরকারি ও আর্থিক খাতে ডিপফেক প্রতিরোধে সিকিউরিটি প্রোটোকল বাস্তবায়ন শুরু।",
                event = "বাংলাদেশ কম্পিউটার কাউন্সিল (BCC) এবং সার্ট (BGD e-GOV CIRT) যৌথভাবে এআই চালিত ফ্রড সনাক্তকরণ জাতীয় প্ল্যাটফর্ম উন্মোচন করেছে।",
                significance = "স্মার্ট বাংলাদেশ ২০৪১ রূপকল্পের আওতায় সরকারি ডিজিটাল সেবা, জাতীয় পরিচয়পত্র সার্ভার এবং বাংলাদেশ ব্যাংকের সুইফট নিরাপত্তা নিশ্চিতকরণে আধুনিক এআই অ্যালগরিদম ব্যবহৃত হবে।",
                historicalBackground = "১৯৫৬ সালে জন ম্যাককার্থি সর্বপ্রথম 'Artificial Intelligence' পরিভাষাটি উদ্ভাবন করেন। অ্যালান টুরিং ১৯৫০ সালে 'টুরিং টেস্ট' প্রস্তাব করেন। বাংলাদেশে ২০২১ সালে প্রথম জাতীয় কৃত্রিম বুদ্ধিমত্তা কৌশল প্রণয়ন করা হয়।",
                keyTermsAndData = listOf(
                    "জন ম্যাককার্থি: Father of Artificial Intelligence",
                    "LLM (Large Language Model): বিশাল টেক্সট ডেটার ওপর প্রশিক্ষিত নিউরাল নেটওয়ার্ক",
                    "CIRT: Computer Incident Response Team",
                    "Zero Trust Architecture: 'Never trust, always verify' সাইবার নিরাপত্তা দর্শন"
                ),
                bcsQuestions = listOf(
                    "কৃত্রিম বুদ্ধিমত্তার জনক (Father of AI) কাকে বলা হয়? (উত্তর: জন ম্যাককার্থি)",
                    "কম্পিউটার বিজ্ঞানে 'টুরিং টেস্ট' কিসের সাথে সম্পর্কিত? (উত্তর: মেশিনের বুদ্ধিমত্তা যাচাই)",
                    "বাংলাদেশে সাইবার নিরাপত্তা সংক্রান্ত নোডাল এজেন্সি কোনটি? (উত্তর: BGD e-GOV CIRT)"
                ),
                keyPersonalities = listOf(
                    "অ্যালান টুরিং (আধুনিক কম্পিউটিং ও টুরিং টেস্টের জনক)",
                    "জন ম্যাককার্থি (এআই পরিভাষার প্রবর্তক)"
                ),
                dataCapsule = mapOf(
                    "AI উদ্ভাবন" to "১৯৫৬ (ডার্টমাউথ সম্মেলন)",
                    "বাংলাদেশে জাতীয় CIRT" to "BGD e-GOV CIRT",
                    "মূল নিরাপত্তা মডেল" to "Zero Trust Security",
                    "মডেল টাইপ" to "Generative Transformers"
                ),
                examTips = "বিসিএস প্রিলিতে তথ্যপ্রযুক্তির ১৫ নম্বরের জন্য এআই, ক্লাউড কম্পিউটিং এবং ক্রিপ্টোগ্রাফি থেকে প্রশ্ন কমন থাকে। পরিভাষাগুলো নির্ভুল মুখস্থ রাখবেন।",
                quizQuestions = listOf(
                    QuizQuestion(
                        id = "q4_1",
                        topicTitle = "কৃত্রিম বুদ্ধিমত্তা ও সাইবার নিরাপত্তা",
                        question = "কাকে কৃত্রিম বুদ্ধিমত্তার (Father of Artificial Intelligence) জনক বলা হয়?",
                        options = listOf(
                            "চার্লস ব্যাবেজ",
                            "জন ম্যাককার্থি (John McCarthy)",
                            "অ্যালান টুরিং",
                            "টিম বার্নার্স লি"
                        ),
                        correctIndex = 1,
                        explanation = "১৯৫৬ সালে ডার্টমাউথ কনফারেন্সে জন ম্যাককার্থি সর্বপ্রথম 'Artificial Intelligence' শব্দটি ব্যবহার করেন এবং এআই গবেষণার ভিত্তি স্থাপন করেন।",
                        category = "বিজ্ঞান ও প্রযুক্তি"
                    ),
                    QuizQuestion(
                        id = "q4_2",
                        topicTitle = "কৃত্রিম বুদ্ধিমত্তা ও সাইবার নিরাপত্তা",
                        question = "মেশিন মানুষের মতো চিন্তা বা আচরণ করতে পারে কিনা তা পরীক্ষার জন্য ১৯৫০ সালে প্রস্তাবিত টেস্ট কোনটি?",
                        options = listOf(
                            "বেনচমার্ক টেস্ট",
                            "টুরিং টেস্ট (Turing Test)",
                            "ভন নিউম্যান টেস্ট",
                            "বুলিয়ান লজিক টেস্ট"
                        ),
                        correctIndex = 1,
                        explanation = "ব্রিটিশ গণিতবিদ অ্যালান টুরিং ১৯৫০ সালে মেশিনের মানুষের মতো বুদ্ধিমান আচরণ পরিমাপের জন্য 'টুরিং টেস্ট' উদ্ভাবন করেন।",
                        category = "বিজ্ঞান ও প্রযুক্তি"
                    ),
                    QuizQuestion(
                        id = "q4_3",
                        topicTitle = "কৃত্রিম বুদ্ধিমত্তা ও সাইবার নিরাপত্তা",
                        question = "আধুনিক সাইবার নিরাপত্তার 'Zero Trust' মডেলের মূল নীতি কোনটি?",
                        options = listOf(
                            "Always trust internal network",
                            "Never trust, always verify (কখনই অন্ধভাবে বিশ্বাস নয়, সবসময় যাচাই)",
                            "Password only authentication",
                            "Only firewall based security"
                        ),
                        correctIndex = 1,
                        explanation = "জিরো ট্রাস্ট (Zero Trust) আর্কিটেকচারের মূলমন্ত্র হলো 'Never trust, always verify', অর্থাৎ অভ্যন্তরীণ বা বহিরাগত কোনো ব্যবহারকারীকেই যাচাই ছাড়া অ্যাক্সেস না দেওয়া।",
                        category = "বিজ্ঞান ও প্রযুক্তি"
                    )
                )
            )
        )
    }
}
