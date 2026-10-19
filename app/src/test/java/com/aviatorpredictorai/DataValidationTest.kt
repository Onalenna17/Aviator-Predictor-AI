package com.aviatorpredictorai

import com.aviatorpredictorai.core.DataValidation
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.RoundSource
import com.aviatorpredictorai.model.RoundStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataValidationTest {

    @Test
    fun testValidAndInvalidMultipliers() {
        assertTrue(DataValidation.isValidMultiplier(1.00))
        assertTrue(DataValidation.isValidMultiplier(1.85))
        assertTrue(DataValidation.isValidMultiplier(129.29))
        assertTrue(DataValidation.isValidMultiplier(1000.0))

        assertFalse(DataValidation.isValidMultiplier(0.99))
        assertFalse(DataValidation.isValidMultiplier(-5.0))
        assertFalse(DataValidation.isValidMultiplier(Double.NaN))
        assertFalse(DataValidation.isValidMultiplier(Double.POSITIVE_INFINITY))
        assertFalse(DataValidation.isValidMultiplier(null))
    }

    @Test
    fun testDeduplicationAndCleansing() {
        val rounds = listOf(
            HistoricalRound("#RD-1", 2.00),
            HistoricalRound("#RD-2", 1.50),
            HistoricalRound("#RD-1", 3.00), // duplicate ID
            HistoricalRound("#RD-3", 0.50), // invalid multiplier < 1.00
            HistoricalRound("#RD-4", 1.85)
        )

        val cleansed = DataValidation.cleanseRounds(rounds)
        assertEquals(3, cleansed.size)
        assertEquals("#RD-1", cleansed[0].roundId)
        assertEquals(2.00, cleansed[0].crashMultiplier, 0.001)
        assertEquals("#RD-2", cleansed[1].roundId)
        assertEquals("#RD-4", cleansed[2].roundId)
    }

    @Test
    fun testCsvParsingWithMalformedData() {
        val csv = """
            RoundId,CrashMultiplier
            #RD-101,2.50
            #RD-102,invalid_text
            #RD-103,1.01
            #RD-104,-2.00
            #RD-105,41.43
        """.trimIndent()

        val parsed = DataValidation.parseCsvRounds(csv, RoundSource.IMPORTED)
        assertEquals(3, parsed.size)
        assertEquals("#RD-101", parsed[0].roundId)
        assertEquals(2.50, parsed[0].crashMultiplier, 0.001)
        assertEquals("#RD-103", parsed[1].roundId)
        assertEquals("#RD-105", parsed[2].roundId)
    }

    @Test
    fun testEmptyDatasetHandling() {
        val cleansed = DataValidation.cleanseRounds(emptyList())
        assertTrue(cleansed.isEmpty())

        val parsed = DataValidation.parseCsvRounds("")
        assertTrue(parsed.isEmpty())
    }
}
