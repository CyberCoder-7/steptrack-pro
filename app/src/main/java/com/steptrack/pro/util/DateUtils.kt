package com.steptrack.pro.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN, Locale.US)

    fun today(): String = LocalDate.now().format(formatter)

    fun daysAgo(days: Long): String = LocalDate.now().minusDays(days).format(formatter)

    fun format(date: LocalDate): String = date.format(formatter)

    fun parse(date: String): LocalDate = LocalDate.parse(date, formatter)

    fun startOfWeek(): String {
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.value % 7 // Sunday = 0
        return today.minusDays(dayOfWeek.toLong()).format(formatter)
    }

    fun startOfMonth(): String = LocalDate.now().withDayOfMonth(1).format(formatter)

    fun friendlyGreeting(hour: Int): String = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }

    fun humanReadableDate(date: String = today()): String {
        val parsed = parse(date)
        val fmt = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US)
        return parsed.format(fmt)
    }
}
