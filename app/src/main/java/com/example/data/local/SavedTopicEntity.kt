package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.FactItem
import com.example.model.ImportantEntities
import com.example.model.TopicAnalysis
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "saved_topics")
data class SavedTopicEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val importanceRank: Int,
    val todaysEvent: String,
    val whyImportant: String,
    val background: String,
    val entitiesJson: String,
    val factsJson: String,
    val bangladeshRelevance: String,
    val bcsTakeaways: String, // Delimited by |||
    val reportedBy: String,
    val sourceDateLabel: String,
    val savedAtTimestamp: Long = System.currentTimeMillis(),
    val userNote: String = ""
) {
    fun parseTakeaways(): List<String> {
        return if (bcsTakeaways.isBlank()) emptyList() else bcsTakeaways.split("|||")
    }

    fun parseFacts(): List<FactItem> {
        val list = mutableListOf<FactItem>()
        try {
            val jsonArray = JSONArray(factsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    FactItem(
                        category = obj.optString("category"),
                        title = obj.optString("title"),
                        detail = obj.optString("detail")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun parseEntities(): ImportantEntities {
        try {
            val obj = JSONObject(entitiesJson)
            fun parseList(key: String): List<String> {
                val arr = obj.optJSONArray(key) ?: return emptyList()
                val l = mutableListOf<String>()
                for (i in 0 until arr.length()) l.add(arr.getString(i))
                return l
            }
            return ImportantEntities(
                persons = parseList("persons"),
                countries = parseList("countries"),
                organizations = parseList("organizations"),
                alliances = parseList("alliances"),
                institutions = parseList("institutions")
            )
        } catch (_: Exception) {
            return ImportantEntities(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    companion object {
        fun fromTopicAnalysis(topic: TopicAnalysis, dateLabel: String): SavedTopicEntity {
            val entitiesObj = JSONObject().apply {
                put("persons", JSONArray(topic.importantEntities.persons))
                put("countries", JSONArray(topic.importantEntities.countries))
                put("organizations", JSONArray(topic.importantEntities.organizations))
                put("alliances", JSONArray(topic.importantEntities.alliances))
                put("institutions", JSONArray(topic.importantEntities.institutions))
            }

            val factsArray = JSONArray().apply {
                topic.importantFacts.forEach { fact ->
                    put(JSONObject().apply {
                        put("category", fact.category)
                        put("title", fact.title)
                        put("detail", fact.detail)
                    })
                }
            }

            return SavedTopicEntity(
                id = topic.id,
                title = topic.title,
                category = topic.category,
                importanceRank = topic.importanceRank,
                todaysEvent = topic.todaysEvent,
                whyImportant = topic.whyImportant,
                background = topic.background,
                entitiesJson = entitiesObj.toString(),
                factsJson = factsArray.toString(),
                bangladeshRelevance = topic.bangladeshRelevance,
                bcsTakeaways = topic.bcsTakeaways.joinToString("|||"),
                reportedBy = topic.reportedBy.joinToString(", "),
                sourceDateLabel = dateLabel
            )
        }
    }
}
