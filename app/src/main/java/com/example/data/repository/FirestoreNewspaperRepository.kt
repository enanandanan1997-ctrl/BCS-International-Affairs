package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.ManagedNewspaper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Repository to persist and synchronize user-managed newspapers in Firestore
 * under /users/{userId}/newspapers/{newspaperId}.
 */
class FirestoreNewspaperRepository(
    private val db: FirebaseFirestore
) {
    // Convenience constructor resolving the named database ID from firebase_applet_config.xml
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun currentUserId(): String? = auth.currentUser?.uid

    /**
     * Observes real-time list of newspapers for the authenticated user.
     */
    fun observeUserNewspapers(): Flow<List<ManagedNewspaper>> {
        val uid = currentUserId() ?: return emptyFlow()
        val colRef = db.collection("users").document(uid).collection("newspapers")
        return colRef.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val name = doc.getString("name") ?: return@mapNotNull null
                val englishName = doc.getString("englishName") ?: ""
                val section = doc.getString("section") ?: ""
                val webUrl = doc.getString("webUrl") ?: ""
                val rssUrl = doc.getString("rssUrl") ?: ""
                val category = doc.getString("category") ?: "আন্তর্জাতিক"
                val isActive = doc.getBoolean("isActive") ?: true
                val isDefault = doc.getBoolean("isDefault") ?: false
                ManagedNewspaper(
                    id = id,
                    name = name,
                    englishName = englishName,
                    section = section,
                    webUrl = webUrl,
                    rssUrl = rssUrl,
                    category = category,
                    isActive = isActive,
                    isDefault = isDefault
                )
            }
        }
    }

    /**
     * Saves or updates a newspaper source in Firestore.
     */
    suspend fun saveNewspaper(newspaper: ManagedNewspaper): Boolean {
        val uid = currentUserId() ?: return false
        val docRef = db.collection("users").document(uid).collection("newspapers").document(newspaper.id)
        return try {
            val exists = docRef.get().await().exists()
            val data = mutableMapOf<String, Any>(
                "name" to newspaper.name,
                "englishName" to newspaper.englishName,
                "section" to newspaper.section,
                "webUrl" to newspaper.webUrl,
                "category" to newspaper.category,
                "isActive" to newspaper.isActive,
                "isDefault" to newspaper.isDefault,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (newspaper.rssUrl.isNotBlank()) {
                data["rssUrl"] = newspaper.rssUrl
            }
            if (!exists) {
                data["createdAt"] = FieldValue.serverTimestamp()
            }
            docRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save newspaper ${newspaper.id}", e)
            false
        }
    }

    /**
     * Deletes a user-added custom newspaper.
     */
    suspend fun deleteNewspaper(newspaperId: String): Boolean {
        val uid = currentUserId() ?: return false
        return try {
            db.collection("users").document(uid)
                .collection("newspapers")
                .document(newspaperId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete newspaper $newspaperId", e)
            false
        }
    }

    /**
     * Toggles active state of a newspaper in Firestore.
     */
    suspend fun toggleActive(newspaperId: String, currentActive: Boolean): Boolean {
        val uid = currentUserId() ?: return false
        val docRef = db.collection("users").document(uid).collection("newspapers").document(newspaperId)
        return try {
            docRef.update(
                mapOf(
                    "isActive" to !currentActive,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle active state for $newspaperId", e)
            false
        }
    }

    /**
     * Seeds initial default newspapers for the user if collection is empty.
     */
    suspend fun seedDefaultsIfEmpty() {
        val uid = currentUserId() ?: return
        val colRef = db.collection("users").document(uid).collection("newspapers")
        try {
            val snapshot = colRef.get().await()
            if (snapshot.isEmpty) {
                ManagedNewspaper.defaultSources.forEach { source ->
                    saveNewspaper(source)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not check/seed defaults: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "FirestoreNewspaperRepo"
    }
}
