package com.example.data.time

import com.example.data.BcsDataSource
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility helper for managing Bangladesh Standard Time (BST, UTC+6)
 * and scheduling daily 6:00 AM automated updates for the 4 national newspapers.
 * Compatible with all Android API levels (API 24+ without desugaring requirements).
 */
object BangladeshTimeHelper {

    val BST_TIME_ZONE: TimeZone = TimeZone.getTimeZone("GMT+6")

    /**
     * Current time in Bangladesh (BST, UTC+6)
     */
    fun nowBst(): java.time.ZonedDateTime {
        return java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Dhaka"))
    }

    /**
     * Current calendar set to Bangladesh Time (BST, UTC+6)
     */
    fun nowCalendar(): Calendar {
        return Calendar.getInstance(BST_TIME_ZONE)
    }

    /**
     * Returns true if current time is past 6:00 AM BST today
     */
    fun isPastToday6AmBst(): Boolean {
        val now = nowCalendar()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        return hour >= 6
    }

    /**
     * Calculates milliseconds from now until the next 6:00 AM BST.
     * If now is 5:30 AM, delay is 30 minutes.
     * If now is 7:00 AM, delay is 23 hours (tomorrow's 6:00 AM).
     */
    fun getMillisUntilNext6AmBst(): Long {
        val now = nowCalendar()
        val nowMillis = now.timeInMillis

        val target = Calendar.getInstance(BST_TIME_ZONE).apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val diff = target.timeInMillis - nowMillis
        return diff.coerceAtLeast(1000L)
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
    fun getFormattedBstDate(calendar: Calendar = nowCalendar()): String {
        val day = BcsDataSource.convertToBanglaNumber(calendar.get(Calendar.DAY_OF_MONTH).toString())
        val year = BcsDataSource.convertToBanglaNumber(calendar.get(Calendar.YEAR).toString())
        val monthBangla = when (calendar.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "জানুয়ারি"
            Calendar.FEBRUARY -> "ফেব্রুয়ারি"
            Calendar.MARCH -> "মার্চ"
            Calendar.APRIL -> "এপ্রিল"
            Calendar.MAY -> "মে"
            Calendar.JUNE -> "জুন"
            Calendar.JULY -> "জুলাই"
            Calendar.AUGUST -> "আগস্ট"
            Calendar.SEPTEMBER -> "সেপ্টেম্বর"
            Calendar.OCTOBER -> "অক্টোবর"
            Calendar.NOVEMBER -> "নভেম্বর"
            Calendar.DECEMBER -> "ডিসেম্বর"
            else -> ""
        }
        return "$day $monthBangla $year"
    }

    /**
     * Returns formatted current time in Bangla (e.g. "০৬:০০ AM BST")
     */
    fun getFormattedBstTime(calendar: Calendar = nowCalendar()): String {
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hour24 < 12) "AM" else "PM"
        val displayHour = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }

        val hStr = String.format(Locale.US, "%02d", displayHour)
        val mStr = String.format(Locale.US, "%02d", minute)

        return "${BcsDataSource.convertToBanglaNumber(hStr)}:${BcsDataSource.convertToBanglaNumber(mStr)} $amPm BST"
    }

    /**
     * Unique key for a daily edition (e.g. "2026-10-17")
     */
    fun getTodayEditionKey(): String {
        val calendar = nowCalendar()
        // If before 6 AM, edition is still yesterday's date until 6 AM hits
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = BST_TIME_ZONE
        }
        return formatter.format(calendar.time)
    }
}
