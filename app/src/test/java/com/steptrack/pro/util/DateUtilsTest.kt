package com.steptrack.pro.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun `today matches system LocalDate formatted as yyyy-MM-dd`() {
        val expected = LocalDate.now().toString()
        assertEquals(expected, DateUtils.today())
    }

    @Test
    fun `daysAgo subtracts correctly`() {
        val expected = LocalDate.now().minusDays(7).toString()
        assertEquals(expected, DateUtils.daysAgo(7))
    }

    @Test
    fun `parse and format round-trip`() {
        val date = LocalDate.of(2026, 3, 15)
        val formatted = DateUtils.format(date)
        assertEquals("2026-03-15", formatted)
        assertEquals(date, DateUtils.parse(formatted))
    }

    @Test
    fun `friendlyGreeting returns expected bucket for each hour`() {
        assertEquals("Good morning", DateUtils.friendlyGreeting(8))
        assertEquals("Good afternoon", DateUtils.friendlyGreeting(14))
        assertEquals("Good evening", DateUtils.friendlyGreeting(18))
        assertEquals("Good night", DateUtils.friendlyGreeting(2))
    }
}
