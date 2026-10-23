package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.data.time.BangladeshTimeHelper
import com.example.model.ManagedNewspaper
import java.util.concurrent.TimeUnit

/**
 * Android WorkManager background worker that automatically triggers
 * the real-time newspaper analysis and update every day at 6:00 AM Bangladesh Standard Time (BST).
 */
class DailyBcsUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i(TAG, "Starting daily 6:00 AM BST automated newspaper sync worker...")

        return try {
            val syncService = RealtimeNewspaperSyncService(context)
            if (syncService.syncState.value.isAutoUpdateEnabled) {
                // Fetch real-time news for the 4 papers
                val result = syncService.performDailySync(ManagedNewspaper.defaultSources)
                if (result.isSuccess) {
                    Log.i(TAG, "Daily 6:00 AM BST newspaper sync completed successfully!")
                } else {
                    Log.w(TAG, "Daily sync completed with fallback: ${result.exceptionOrNull()?.message}")
                }
            } else {
                Log.i(TAG, "Auto-update is disabled by user. Skipping background sync.")
            }

            // Schedule the next cycle for tomorrow 6:00 AM BST
            schedule(context)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Daily 6:00 AM BST update worker error: ${e.message}", e)
            // Still re-schedule next cycle so background schedule never dies
            schedule(context)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "DailyBcsUpdateWorker"
        const val WORK_NAME = "bcs_daily_6am_newspaper_sync"

        /**
         * Schedules the worker to execute at the exact next 6:00 AM Bangladesh Local Time.
         */
        fun schedule(context: Context) {
            val initialDelayMillis = BangladeshTimeHelper.getMillisUntilNext6AmBst()
            Log.d(TAG, "Scheduling next daily 6:00 AM BST sync in ${initialDelayMillis / 1000 / 60} minutes")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyBcsUpdateWorker>()
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        }

        /**
         * Cancels any scheduled background work.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
