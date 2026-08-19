package com.absforge.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    fun nowEpochMs(): Long = System.currentTimeMillis()

    fun todayStartEpochMs(): Long {
        val today = LocalDate.now()
        return today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun epochToLocalDate(epochMs: Long): LocalDate {
        return Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun epochToLocalDateTime(epochMs: Long): LocalDateTime {
        return Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun localDateToEpochMs(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun formatDuration(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    fun formatDurationMinutes(seconds: Int): String {
        val min = seconds / 60
        return if (min > 0) "$min Min" else "${seconds}s"
    }

    fun formatDate(epochMs: Long): String {
        val date = epochToLocalDate(epochMs)
        return date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }

    fun formatShortDate(epochMs: Long): String {
        val date = epochToLocalDate(epochMs)
        return date.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    fun daysBetween(startMs: Long, endMs: Long): Long {
        val start = epochToLocalDate(startMs)
        val end = epochToLocalDate(endMs)
        return ChronoUnit.DAYS.between(start, end)
    }

    fun getGreeting(): String {
        val hour = LocalDateTime.now().hour
        return when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    fun getWeekStartMs(): Long {
        val today = LocalDate.now()
        val monday = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return localDateToEpochMs(monday)
    }

    fun getWeekEndMs(): Long {
        val today = LocalDate.now()
        val sunday = today.plusDays(7 - today.dayOfWeek.value.toLong())
        return localDateToEpochMs(sunday) + 86400000 - 1
    }
}
