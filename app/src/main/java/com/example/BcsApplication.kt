package com.example

import android.app.Application
import android.util.Log
import com.example.data.sync.DailyBcsUpdateWorker

class BcsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "BcsApplication started. Initializing automated 6:00 AM BST daily sync...")
        try {
            // Guarantee that WorkManager background job is scheduled as soon as app is installed/launched
            DailyBcsUpdateWorker.schedule(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling DailyBcsUpdateWorker on startup: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "BcsApplication"
    }
}
