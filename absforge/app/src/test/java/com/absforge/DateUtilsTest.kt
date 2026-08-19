package com.absforge

import com.absforge.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testFormatDuration() {
        assertEquals("00:30", DateUtils.formatDuration(30))
        assertEquals("01:15", DateUtils.formatDuration(75))
        assertEquals("10:00", DateUtils.formatDuration(600))
    }

    @Test
    fun testFormatDurationMinutes() {
        assertEquals("1 Min", DateUtils.formatDurationMinutes(60))
        assertEquals("5 Min", DateUtils.formatDurationMinutes(300))
        assertEquals("45s", DateUtils.formatDurationMinutes(45))
    }
}
