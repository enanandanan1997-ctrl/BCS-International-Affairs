package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String, // "user" or "model"
    val text: String,
    val searchSources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class QuotaExceededException(message: String) : Exception(message)

/**
 * Handles communication with Gemini API with multi-model fallback,
 * Google Search grounding, and resilient HTTP 429 (RESOURCE_EXHAUSTED) rate-limit handling.
 */
object GeminiManager {
    private const val TAG = "GeminiManager"

    // Supported modern models according to gemini-api skill
    private val CANDIDATE_MODELS = listOf(
        "gemini-2.5-flash",
        "gemini-3.5-flash",
        "gemini-flash-latest"
    )

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (_: Throwable) {
            ""
        }
    }

    /**
     * Multi-turn chat with fallback models and search grounding
     */
    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        userMessage: String,
        enableSearchGrounding: Boolean = true
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API Key সেট করা নেই। AI Studio-এর Secrets প্যানেলে GEMINI_API_KEY যুক্ত করুন।")
            )
        }

        val systemInstruction = """
            You are 'BCS International Mentor' (বিসিএস আন্তর্জাতিক মেন্টর ও প্রশ্ন বিশ্লেষক) — an elite academic mentor for the Bangladesh Civil Service examination candidates.
            Your role:
            1. Teach International Affairs concepts strictly aligned with the BCS Preliminary and Written syllabus.
            2. Language: Bengali (Bangla) as primary, with international technical terms, names, and treaties in English in parentheses (e.g. উত্তর আটলান্টিক চুক্তি সংস্থা (NATO)).
            3. Always synthesize factual information, dates, headquarters, capitals, treaties, and relevance to Bangladesh.
            4. Provide MCQ practice, written bullet points, and viva tips whenever explaining topics.
            5. Deliver verified factual developments from world affairs and Bangladeshi national newspapers (Prothom Alo, The Daily Star, Bonik Barta, Dhaka Tribune).
        """.trimIndent()

        // Try candidate models with and without search grounding if 429 quota occurs
        var lastException: Exception? = null

        for (model in CANDIDATE_MODELS) {
            // First try with search grounding (if requested), then without search grounding if 429 is encountered
            val groundingAttempts = if (enableSearchGrounding) listOf(true, false) else listOf(false)

            for (useGrounding in groundingAttempts) {
                try {
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                    val jsonBody = buildChatRequestBody(history, userMessage, systemInstruction, useGrounding)

                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val requestBody = jsonBody.toString().toRequestBody(mediaType)
                    val request = Request.Builder().url(url).post(requestBody).build()

                    val response = okHttpClient.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""

                    if (response.code == 429) {
                        Log.w(TAG, "Model $model (grounding=$useGrounding) hit 429 Quota Exceeded. Trying fallback...")
                        lastException = QuotaExceededException("কোটা সীমা সাময়িকভাবে অতিক্রান্ত (429 Rate Limit/Quota Exceeded)")
                        delay(600) // Brief backoff
                        continue
                    }

                    if (!response.isSuccessful) {
                        Log.w(TAG, "Model $model returned error code: ${response.code}")
                        lastException = Exception("Gemini অনুরোধ ব্যর্থ হয়েছে (${response.code})")
                        continue
                    }

                    val jsonResponse = JSONObject(responseBody)
                    val reply = parseResponseCandidate(jsonResponse)
                    if (reply != null) {
                        return@withContext Result.success(reply)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Chat attempt on $model failed: ${e.message}")
                    lastException = e
                }
            }
        }

        // If all attempts failed due to 429 or network, return friendly fallback
        if (lastException is QuotaExceededException) {
            return@withContext Result.success(
                ChatMessage(
                    role = "model",
                    text = "অনলাইন এআই কোটা সীমা সাময়িকভাবে অতিক্রান্ত হয়েছে (429 Rate Limit)।\n\nপরামর্শ: কিছু মুহূর্ত পর পুনরায় চ্যাট করুন। এর মধ্যে আপনি অফলাইনেও ৪টি পত্রিকার আজকের সমস্ত বিষয়, ৮ স্তরের বিশ্লেষণ, কুইক রিভিশন এবং সংরক্ষিত বিষয়াবলী সম্পূর্ণ ব্যবহার করতে পারবেন।"
                )
            )
        }

        Result.failure(lastException ?: Exception("রেসপন্স পাওয়া যায়নি।"))
    }

    /**
     * Daily BCS intelligence report generation with model fallback and search grounding resilience
     */
    suspend fun generateDailyBcsAnalysis(customPrompt: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API Key কনফিগার করা হয়নি। AI Studio-এর Secrets প্যানেলে GEMINI_API_KEY যুক্ত করুন।")
            )
        }

        val systemInstruction = """
            You are an expert BCS International Affairs Professor, Senior Geopolitical Analyst, and Question Setter for the Bangladesh Civil Service examination.
            Language: Bengali (Bangla) as primary, with international proper names/treaties in English in parentheses.
            Synthesize international affairs news from the 4 newspapers: Prothom Alo, The Daily Star, Bonik Barta, and Dhaka Tribune.
            Produce the full 13-part structured report with 5-10 high-value topics, 8-part topic breakdown, cross-connections, quick revision, and MCQs.
        """.trimIndent()

        var lastException: Exception? = null

        for (model in CANDIDATE_MODELS) {
            // First try with search grounding, then without search grounding if 429
            val groundingAttempts = listOf(true, false)

            for (useGrounding in groundingAttempts) {
                try {
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                    val jsonBody = buildDailyReportRequestBody(customPrompt, systemInstruction, useGrounding)

                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val requestBody = jsonBody.toString().toRequestBody(mediaType)
                    val request = Request.Builder().url(url).post(requestBody).build()

                    val response = okHttpClient.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""

                    if (response.code == 429) {
                        Log.w(TAG, "Analysis with $model (grounding=$useGrounding) hit 429 Quota Exceeded. Trying fallback...")
                        lastException = QuotaExceededException("জেমিনি এআই কোটা লিমিট (429 Rate Limit / Quota Exceeded)")
                        delay(600)
                        continue
                    }

                    if (!response.isSuccessful) {
                        Log.w(TAG, "Model $model returned error code: ${response.code}")
                        lastException = Exception("Gemini API অনুরোধ ব্যর্থ হয়েছে (${response.code})")
                        continue
                    }

                    val jsonResponse = JSONObject(responseBody)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val generatedText = parts.getJSONObject(0).optString("text", "")
                            if (generatedText.isNotBlank()) {
                                return@withContext Result.success(generatedText)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Daily analysis attempt on $model failed: ${e.message}")
                    lastException = e
                }
            }
        }

        Result.failure(lastException ?: Exception("সঠিক রেসপন্স টেক্সট পাওয়া যায়নি।"))
    }

    private fun buildChatRequestBody(
        history: List<ChatMessage>,
        userMessage: String,
        systemInstruction: String,
        enableSearchGrounding: Boolean
    ): JSONObject {
        return JSONObject().apply {
            val contentsArray = JSONArray().apply {
                history.forEach { msg ->
                    val turn = JSONObject().apply {
                        put("role", msg.role)
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", msg.text) })
                        }
                        put("parts", parts)
                    }
                    put(turn)
                }
                val currentTurn = JSONObject().apply {
                    put("role", "user")
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", userMessage) })
                    }
                    put("parts", parts)
                }
                put(currentTurn)
            }
            put("contents", contentsArray)

            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstruction) })
                })
            })

            if (enableSearchGrounding) {
                put("tools", JSONArray().apply {
                    put(JSONObject().apply { put("googleSearch", JSONObject()) })
                })
            }

            put("generationConfig", JSONObject().apply {
                put("temperature", 0.6)
                put("topP", 0.95)
                put("topK", 40)
            })
        }
    }

    private fun buildDailyReportRequestBody(
        prompt: String,
        systemInstruction: String,
        enableSearchGrounding: Boolean
    ): JSONObject {
        return JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })

            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstruction) })
                })
            })

            if (enableSearchGrounding) {
                put("tools", JSONArray().apply {
                    put(JSONObject().apply { put("googleSearch", JSONObject()) })
                })
            }

            put("generationConfig", JSONObject().apply {
                put("temperature", 0.6)
                put("topP", 0.95)
                put("topK", 40)
            })
        }
    }

    private fun parseResponseCandidate(jsonResponse: JSONObject): ChatMessage? {
        val candidates = jsonResponse.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null

        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")

        var replyText = ""
        if (parts != null && parts.length() > 0) {
            replyText = parts.getJSONObject(0).optString("text", "")
        }

        val searchSources = mutableListOf<String>()
        val groundingMetadata = candidate.optJSONObject("groundingMetadata")
        if (groundingMetadata != null) {
            val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
            if (searchChunks != null) {
                for (i in 0 until searchChunks.length()) {
                    val chunk = searchChunks.optJSONObject(i)
                    val web = chunk?.optJSONObject("web")
                    val title = web?.optString("title")
                    val uri = web?.optString("uri")
                    if (!title.isNullOrBlank() && !uri.isNullOrBlank()) {
                        searchSources.add("$title: $uri")
                    }
                }
            }
        }

        return if (replyText.isNotBlank()) {
            ChatMessage(
                role = "model",
                text = replyText,
                searchSources = searchSources
            )
        } else null
    }
}
