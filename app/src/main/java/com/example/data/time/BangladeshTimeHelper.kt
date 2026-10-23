package com.example.data.time

import com.example.data.BcsDataSource
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utility helper for managing Bangladesh Standard Time (BST, UTC+6)
 * and scheduling daily 6:00 AM automated updates for the 4 national newspapers.
 */
object BangladeshTimeHelper {

    val BST_ZONE_ID: ZoneId = ZoneId.of("Asia/Dhaka")

    /**
     * Current time in Bangladesh (BST, UTC+6)
     */
    fun nowBst(): ZonedDateTime {
        return ZonedDateTime.now(BST_ZONE_ID)
    }

    /**
     * Returns true if current time is past 6:00 AM BST today
     */
    fun isPastToday6AmBst(): Boolean {
        val now = nowBst()
        val today6Am = now.withHour(6).withMinute(0).withSecond(0).withNano(0)
        return now.isAfter(today6Am)
    }

    /**
     * Calculates milliseconds from now until the next 6:00 AM BST.
     * If now is 5:30 AM, delay is 30 minutes.
     * If now is 7:00 AM, delay is 23 hours (tomorrow's 6:00 AM).
     */
    fun getMillisUntilNext6AmBst(): Long {
        val now = nowBst()
        var target6Am = now.withHour(6).withMinute(0).withSecond(0).withNano(0)
        if (!now.isBefore(target6Am)) {
            // Already past or exactly 6 AM today -> target tomorrow's 6 AM
            target6Am = target6Am.plusDays(1)
        }
        val duration = Duration.between(now, target6Am)
        return duration.toMillis().coerceAtLeast(1000L)
    }

    /**
     * Human readable countdown until next 6:00 AM BST in Bangla.
     * e.g., "৮ ঘণ্টা ২৫ মিনিট বাকি"
     */
    fun getCountdownToNext6AmBst(): String {
        val millis = getMillisUntilNext6AmBst()
        val totalMinutes = millis / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        val hoursBangla = BcsDataSource.convertToBanglaNumber(hours.toString())
        val minutesBangla = BcsDataSource.convertToBanglaNumber(minutes.toString())

        return if (hours > 0) {
            "$hoursBangla ঘণ্টা $minutesBangla মিনিট পর"
        } else {
            "$minutesBangla মিনিট পর"
        }
    }

    /**
     * Returns formatted current date in Bangla (e.g. "১৭ অক্টোবর ২০২৬")
     */
    fun getFormattedBstDate(zonedDateTime: ZonedDateTime = nowBst()): String {
        val day = BcsDataSource.convertToBanglaNumber(zonedDateTime.dayOfMonth.toString())
        val year = BcsDataSource.convertToBanglaNumber(zonedDateTime.year.toString())
        val monthBangla = when (zonedDateTime.monthValue) {
            1 -> "জানুয়ারি"
            2 -> "ফেব্রুয়ারি"
            3 -> "মার্চ"
            4 -> "এপ্রিল"
            5 -> "মে"
            6 -> "জুন"
            7 -> "জুলাই"
            8 -> "আগস্ট"
            9 -> "সেপ্টেম্বর"
            10 -> "অক্টোবর"
            11 -> "নভেম্বর"
            12 -> "ডিসেম্বর"
            else -> ""
        }
        return "$day $monthBangla $year"
    }

    /**
     * Returns formatted current time in Bangla (e.g. "০৬:০০ AM BST")
     */
    fun getFormattedBstTime(zonedDateTime: ZonedDateTime = nowBst()): String {
        val hour = zonedDateTime.hour
        val minute = zonedDateTime.minute
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

        val hStr = String.format(Locale.US, "%02d", displayHour)
        val mStr = String.format(Locale.US, "%02d", minute)

        val banglaTime = "${BcsDataSource.convertToBanglaNumber(hStr)}:${BcsDataSource.convertToBanglaNumber(mStr)} $amPm BST"
        return banglaTime
    }

    /**
     * Unique key for a daily edition (e.g. "2026-10-17_06:00")
     */
    fun getTodayEditionKey(): String {
        val now = nowBst()
        // If before 6 AM, edition is still yesterday's date until 6 AM hits
        val effectiveDate = if (now.hour < 6) now.minusDays(1) else now
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
        return effectiveDate.format(formatter)
    }
}
