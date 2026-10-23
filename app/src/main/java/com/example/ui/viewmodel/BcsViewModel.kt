package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BcsDataSource
import com.example.data.api.ChatMessage
import com.example.data.api.GeminiManager
import com.example.data.local.BcsDatabase
import com.example.data.local.BookmarkedMcq
import com.example.data.local.QuizAttempt
import com.example.data.local.SavedTopicEntity
import com.example.data.repository.SavedTopicRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import android.app.Activity
import com.example.data.auth.FirebaseAuthManager
import com.example.data.repository.FirestoreNewspaperRepository
import com.example.data.sync.DailyBcsUpdateWorker
import com.example.data.sync.DailySyncState
import com.example.data.sync.RealtimeNewspaperSyncService
import com.example.data.time.BangladeshTimeHelper
import com.google.firebase.auth.FirebaseUser

enum class ExamMode {
    REAL_EXAM, PRACTICE
}

class BcsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BcsDatabase.getDatabase(application)
    private val dao = db.bcsDao()
    private val savedTopicRepository = SavedTopicRepository(dao)

    // Daily 6:00 AM BST Real-time Newspaper Sync Service
    val syncService = RealtimeNewspaperSyncService(application)
    val syncState: StateFlow<DailySyncState> = syncService.syncState

    // Firebase Auth
    val authManager = FirebaseAuthManager(application)
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    val isAuthLoading: StateFlow<Boolean> = authManager.isLoading
    val authError: StateFlow<String?> = authManager.authError

    // Firestore Newspaper Management
    private val firestoreNewspaperRepo = FirestoreNewspaperRepository(application)
    private val _managedNewspapers = MutableStateFlow<List<ManagedNewspaper>>(ManagedNewspaper.defaultSources)
    val managedNewspapers: StateFlow<List<ManagedNewspaper>> = _managedNewspapers.asStateFlow()

    private val _allReports = MutableStateFlow(BcsDataSource.getAllReports())
    val allReports: StateFlow<List<DailyReport>> = _allReports.asStateFlow()

    private val _currentReport = MutableStateFlow(BcsDataSource.report16Oct2026)
    val currentReport: StateFlow<DailyReport> = _currentReport.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    // Topic Filter & Expansion
    private val _selectedCategory = MutableStateFlow("সব")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _expandedTopicIds = MutableStateFlow<Set<String>>(setOf("t1", "t2"))
    val expandedTopicIds: StateFlow<Set<String>> = _expandedTopicIds.asStateFlow()

    // Saved Topics for Offline Reading (Room Database Entity)
    val savedTopics: StateFlow<List<SavedTopicEntity>> = savedTopicRepository.allSavedTopics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedTopicIds: StateFlow<Set<String>> = savedTopics
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Quick Revision
    private val _revisionSearchQuery = MutableStateFlow("")
    val revisionSearchQuery: StateFlow<String> = _revisionSearchQuery.asStateFlow()

    private val _revisionFlashcardMode = MutableStateFlow(false)
    val revisionFlashcardMode: StateFlow<Boolean> = _revisionFlashcardMode.asStateFlow()

    private val _revealedFlashcardIndices = MutableStateFlow<Set<Int>>(emptySet())
    val revealedFlashcardIndices: StateFlow<Set<Int>> = _revealedFlashcardIndices.asStateFlow()

    // Daily MCQ Exam
    private val _examMode = MutableStateFlow(ExamMode.REAL_EXAM)
    val examMode: StateFlow<ExamMode> = _examMode.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    private val _isExamSubmitted = MutableStateFlow(false)
    val isExamSubmitted: StateFlow<Boolean> = _isExamSubmitted.asStateFlow()

    private val _scoreSummary = MutableStateFlow<QuizAttempt?>(null)
    val scoreSummary: StateFlow<QuizAttempt?> = _scoreSummary.asStateFlow()

    // Bookmarks (MCQs)
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    // AI Generation
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStatus = MutableStateFlow("")
    val generationStatus: StateFlow<String> = _generationStatus.asStateFlow()

    private val _generatedRawOutput = MutableStateFlow<String?>(null)
    val generatedRawOutput: StateFlow<String?> = _generatedRawOutput.asStateFlow()

    // Multi-turn Gemini Chatbot with Google Search data
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = "model",
                text = "স্বাগতম! আমি আপনার BCS আন্তর্জাতিক বিষয়াবলি মেন্টর ও প্রশ্ন বিশ্লেষক। আজকের আন্তর্জাতিক ভূ-রাজনীতি, ৪টি সংবাদপত্রের কভারেজ, গুরুত্বপূর্ণ চুক্তি, প্রণালী বা লিখিত/ভাইভা প্রস্তুতি সম্পর্কিত যেকোনো প্রশ্ন জিজ্ঞেস করতে পারেন।"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    init {
        loadBookmarks()
        authManager.attemptAutoSignIn(viewModelScope)
        observeAuthAndSyncNewspapers()
        initDailyUpdateScheduler()
    }

    private fun initDailyUpdateScheduler() {
        if (syncService.syncState.value.isAutoUpdateEnabled) {
            DailyBcsUpdateWorker.schedule(getApplication())
        }
        // Auto-check if today's 6:00 AM BST edition is due
        if (syncService.isSyncDueToday()) {
            triggerDailySync()
        }
    }

    private var lastManualSyncTime = 0L

    fun triggerDailySync(customDateLabel: String? = null) {
        val now = System.currentTimeMillis()
        if (syncService.syncState.value.isSyncing) return
        if (now - lastManualSyncTime < 10_000L) return
        lastManualSyncTime = now

        viewModelScope.launch {
            val activeSources = _managedNewspapers.value.filter { it.isActive }
            val result = syncService.performDailySync(activeSources, customDateLabel)
            result.onSuccess { newReport ->
                _currentReport.value = newReport
                val existing = _allReports.value.filterNot { it.id == newReport.id }
                _allReports.value = listOf(newReport) + existing
            }
        }
    }

    fun toggleAutoUpdate(enabled: Boolean) {
        syncService.setAutoUpdateEnabled(enabled)
        if (enabled) {
            DailyBcsUpdateWorker.schedule(getApplication())
        } else {
            DailyBcsUpdateWorker.cancel(getApplication())
        }
    }

    fun refreshSyncCountdown() {
        syncService.refreshCountdown()
    }

    private fun observeAuthAndSyncNewspapers() {
        viewModelScope.launch {
            currentUser.collectLatest { user ->
                if (user != null) {
                    firestoreNewspaperRepo.seedDefaultsIfEmpty()
                    firestoreNewspaperRepo.observeUserNewspapers().collect { firestoreSources ->
                        if (firestoreSources.isNotEmpty()) {
                            _managedNewspapers.value = firestoreSources
                        }
                    }
                }
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        authManager.signInWithGoogle(
            activity = activity,
            scope = viewModelScope,
            onSuccess = {
                viewModelScope.launch {
                    firestoreNewspaperRepo.seedDefaultsIfEmpty()
                }
            }
        )
    }

    fun signOut() {
        authManager.signOut(viewModelScope)
    }

    fun addCustomNewspaper(
        name: String,
        englishName: String,
        section: String,
        webUrl: String,
        rssUrl: String = "",
        category: String = "আন্তর্জাতিক"
    ) {
        val newId = "src_custom_" + System.currentTimeMillis()
        val item = ManagedNewspaper(
            id = newId,
            name = name.trim(),
            englishName = englishName.trim(),
            section = section.trim(),
            webUrl = webUrl.trim(),
            rssUrl = rssUrl.trim(),
            category = category.trim(),
            isActive = true,
            isDefault = false
        )
        _managedNewspapers.value = _managedNewspapers.value + item
        viewModelScope.launch {
            firestoreNewspaperRepo.saveNewspaper(item)
        }
    }

    fun updateNewspaper(newspaper: ManagedNewspaper) {
        _managedNewspapers.value = _managedNewspapers.value.map {
            if (it.id == newspaper.id) newspaper else it
        }
        viewModelScope.launch {
            firestoreNewspaperRepo.saveNewspaper(newspaper)
        }
    }

    fun deleteNewspaper(newspaperId: String) {
        _managedNewspapers.value = _managedNewspapers.value.filter { it.id != newspaperId }
        viewModelScope.launch {
            firestoreNewspaperRepo.deleteNewspaper(newspaperId)
        }
    }

    fun toggleNewspaperActive(newspaperId: String) {
        val target = _managedNewspapers.value.firstOrNull { it.id == newspaperId } ?: return
        val updated = target.copy(isActive = !target.isActive)
        _managedNewspapers.value = _managedNewspapers.value.map {
            if (it.id == newspaperId) updated else it
        }
        viewModelScope.launch {
            firestoreNewspaperRepo.toggleActive(newspaperId, target.isActive)
        }
    }

    fun resetNewspapersToDefault() {
        _managedNewspapers.value = ManagedNewspaper.defaultSources
        viewModelScope.launch {
            ManagedNewspaper.defaultSources.forEach { source ->
                firestoreNewspaperRepo.saveNewspaper(source)
            }
        }
    }

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun selectReport(reportId: String) {
        val found = _allReports.value.firstOrNull { it.id == reportId }
        if (found != null) {
            _currentReport.value = found
            resetExam()
            _expandedTopicIds.value = setOf(found.topics.firstOrNull()?.id ?: "")
        }
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun toggleTopicExpansion(topicId: String) {
        val current = _expandedTopicIds.value.toMutableSet()
        if (current.contains(topicId)) {
            current.remove(topicId)
        } else {
            current.add(topicId)
        }
        _expandedTopicIds.value = current
    }

    // Saved Topics for Offline Reading (Room)
    fun toggleSaveTopic(topic: TopicAnalysis, dateLabel: String) {
        viewModelScope.launch {
            val isAlreadySaved = savedTopicRepository.isTopicSaved(topic.id)
            if (isAlreadySaved) {
                savedTopicRepository.removeTopic(topic.id)
            } else {
                val entity = SavedTopicEntity.fromTopicAnalysis(topic, dateLabel)
                savedTopicRepository.saveTopic(entity)
            }
        }
    }

    fun removeSavedTopic(topicId: String) {
        viewModelScope.launch {
            savedTopicRepository.removeTopic(topicId)
        }
    }

    fun updateSavedTopicNote(topicId: String, note: String) {
        viewModelScope.launch {
            savedTopicRepository.updateNote(topicId, note)
        }
    }

    // Quick Revision
    fun setRevisionSearch(query: String) {
        _revisionSearchQuery.value = query
    }

    fun toggleFlashcardMode() {
        _revisionFlashcardMode.value = !_revisionFlashcardMode.value
        _revealedFlashcardIndices.value = emptySet()
    }

    fun toggleFlashcardReveal(serial: Int) {
        val current = _revealedFlashcardIndices.value.toMutableSet()
        if (current.contains(serial)) {
            current.remove(serial)
        } else {
            current.add(serial)
        }
        _revealedFlashcardIndices.value = current
    }

    // Exam functions
    fun setExamMode(mode: ExamMode) {
        _examMode.value = mode
        resetExam()
    }

    fun selectAnswer(questionNumber: Int, optionIndex: Int) {
        if (_isExamSubmitted.value && _examMode.value == ExamMode.REAL_EXAM) return
        val current = _userAnswers.value.toMutableMap()
        current[questionNumber] = optionIndex
        _userAnswers.value = current
    }

    fun submitExam() {
        val testQuestions = _currentReport.value.mcqTest
        var correct = 0
        var wrong = 0
        var skipped = 0

        testQuestions.forEach { q ->
            val chosen = _userAnswers.value[q.questionNumber]
            when {
                chosen == null -> skipped++
                chosen == q.correctIndex -> correct++
                else -> wrong++
            }
        }

        // BCS standard marking: 1 mark per correct, 0.5 deduction per wrong answer
        val score = (correct * 1.0f) - (wrong * 0.5f)
        val attempt = QuizAttempt(
            reportId = _currentReport.value.id,
            dateLabel = _currentReport.value.banglaDate,
            totalQuestions = testQuestions.size,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            calculatedScore = score
        )
        _scoreSummary.value = attempt
        _isExamSubmitted.value = true

        viewModelScope.launch {
            dao.insertQuizAttempt(attempt)
        }
    }

    fun resetExam() {
        _userAnswers.value = emptyMap()
        _isExamSubmitted.value = false
        _scoreSummary.value = null
    }

    // Bookmarking (MCQs)
    private fun loadBookmarks() {
        viewModelScope.launch {
            val bookmarks = dao.getAllBookmarks()
            _bookmarkedIds.value = bookmarks.map { it.id }.toSet()
        }
    }

    fun toggleBookmark(mcq: McqQuestion) {
        viewModelScope.launch {
            if (_bookmarkedIds.value.contains(mcq.id)) {
                dao.deleteBookmark(mcq.id)
                _bookmarkedIds.value = _bookmarkedIds.value - mcq.id
            } else {
                val entity = BookmarkedMcq(
                    id = mcq.id,
                    questionNumber = mcq.questionNumber,
                    question = mcq.question,
                    optionsJson = mcq.options.joinToString("|||"),
                    correctIndex = mcq.correctIndex,
                    explanation = mcq.explanation,
                    topicReference = mcq.topicReference.ifEmpty { _currentReport.value.banglaDate }
                )
                dao.insertBookmark(entity)
                _bookmarkedIds.value = _bookmarkedIds.value + mcq.id
            }
        }
    }

    // Multi-turn Chatbot with Google Search
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentHistory = _chatMessages.value
        val newUserMessage = ChatMessage(role = "user", text = userText)
        _chatMessages.value = currentHistory + newUserMessage
        _isChatLoading.value = true
        _chatError.value = null

        viewModelScope.launch {
            val result = GeminiManager.sendChatMessage(
                history = currentHistory,
                userMessage = userText,
                enableSearchGrounding = true
            )
            result.fold(
                onSuccess = { replyMsg ->
                    _chatMessages.value = _chatMessages.value + replyMsg
                    _isChatLoading.value = false
                },
                onFailure = { error ->
                    _chatError.value = error.message
                    // Fallback friendly message if API key is unconfigured
                    val fallbackMsg = ChatMessage(
                        role = "model",
                        text = "অনলাইন কানেকশন বার্তা: ${error.message ?: "সংযোগ করা যায়নি।"}\n\nপরামর্শ: আপনি অফলাইনেও ৪টি পত্রিকার আজকের সমস্ত বিষয়, ৮ স্তরের বিশ্লেষণ, কুইক রিভিশন এবং সংরক্ষিত বিষয়াবলী সম্পূর্ণ ব্যবহার করতে পারবেন।"
                    )
                    _chatMessages.value = _chatMessages.value + fallbackMsg
                    _isChatLoading.value = false
                }
            )
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                role = "model",
                text = "চ্যাট থ্রেড নতুন করে শুরু করা হয়েছে। BCS আন্তর্জাতিক বিষয়াবলি সংক্রান্ত যেকোনো প্রশ্ন করুন।"
            )
        )
    }

    private var lastEngineCallTime = 0L

    // Run Daily Analysis Engine (Repeatable daily workflow based on user's active newspaper sources)
    fun runDailyAnalysisEngine(dateLabel: String = "১৬ অক্টোবর ২০২৬") {
        val now = System.currentTimeMillis()
        if (_isGenerating.value) return
        if (now - lastEngineCallTime < 10_000L) {
            _generationStatus.value = "অনুগ্রহ করে কিছুক্ষণ পর পুনরায় অনুরোধ করুন (রেট লিমিট সুরক্ষা)।"
            return
        }
        lastEngineCallTime = now

        viewModelScope.launch {
            _isGenerating.value = true
            val activeSources = _managedNewspapers.value.filter { it.isActive }
            val count = activeSources.size
            _generationStatus.value = "$count টি সক্রিয় সংবাদপত্রের আন্তর্জাতিক পাতা ও উৎস বিশ্লেষণ করা হচ্ছে..."

            val sourcesListText = if (activeSources.isNotEmpty()) {
                activeSources.mapIndexed { idx, s ->
                    "${idx + 1}. ${s.name} (${s.englishName}) — ${s.section} [${s.webUrl}]${if (s.rssUrl.isNotBlank()) " (RSS Feed: ${s.rssUrl})" else ""}"
                }.joinToString("\n")
            } else {
                "১. প্রথম আলো — আন্তর্জাতিক\n২. The Daily Star — International\n৩. বণিক বার্তা — আন্তর্জাতিক\n৪. ঢাকা ট্রিবিউন — World"
            }

            val prompt = """
                তারিখ: $dateLabel
                সময়: ৫:৩০ PM BST
                
                অনুগ্রহ করে নিচের ব্যবহারকারী-নির্ধারিত সক্রিয় সংবাদপত্র ও রিসোর্স উৎসসমূহের আজকের আন্তর্জাতিক পাতা ও সর্বশেষ খবর বিশ্লেষণ করে সম্পূর্ণ BCS উপযোগী দৈনিক প্রতিবেদন তৈরি করুন:
                $sourcesListText
                
                প্রয়োজনীয় কাঠামো:
                - তারিখ ও সময় (BST, UTC+6)
                - আজকের শীর্ষ খবর (Top Headlines)
                - ৫-১০টি গুরুত্বপূর্ণ টপিক (১. আজকের ঘটনা, ২. কেন গুরুত্বপূর্ণ?, ৩. পটভূমি, ৪. গুরুত্বপূর্ণ ব্যক্তি ও দেশ, ৫. গুরুত্বপূর্ণ তথ্য, ৬. বাংলাদেশের জন্য প্রাসঙ্গিকতা, ৭. BCS-এর জন্য কী পড়বেন?, ৮. সম্ভাব্য BCS প্রশ্ন)
                - আন্তঃসংযোগ (Cross-topic connections)
                - আজকের BCS Quick Revision (১৫-২৫টি ওয়ান-লাইনার)
                - আজকের MCQ টেস্ট (১০-১৫টি প্রশ্ন + উত্তরমালা ও ব্যাখ্যা)
                - লিখিত ও ভাইভা দৃষ্টিকোণ (Written & Viva angle)
                - ব্যবহৃত উৎস ও স্বচ্ছতা (Newspaper sources transparency)
            """.trimIndent()

            val result = GeminiManager.generateDailyBcsAnalysis(prompt)
            result.fold(
                onSuccess = { text ->
                    _generationStatus.value = "সফলভাবে দৈনিক প্রতিবেদন প্রস্তুত হয়েছে!"
                    _generatedRawOutput.value = text
                    _isGenerating.value = false
                },
                onFailure = { error ->
                    _generationStatus.value = "অনলাইন ইঞ্জিনের বার্তা: ${error.message ?: "সংযোগ ব্যর্থ"}\n(অফলাইন সংস্করণ ও সংরক্ষিত প্রতিবেদন প্রস্তুত রয়েছে)"
                    _isGenerating.value = false
                }
            )
        }
    }

    fun dismissGeneratedOutput() {
        _generatedRawOutput.value = null
        _generationStatus.value = ""
    }
}
