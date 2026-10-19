package com.aviatorpredictorai.core

import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.RoundSource
import com.aviatorpredictorai.model.RoundStatus
import java.util.Locale

object DataValidation {

    const val MIN_MULTIPLIER = 1.00
    const val MAX_REASONABLE_MULTIPLIER = 100000.0
    const val MIN_SAMPLE_SIZE = 10

    /**
     * Validates a single crash multiplier value.
     */
    fun isValidMultiplier(value: Double?): Boolean {
        if (value == null || value.isNaN() || value.isInfinite()) return false
        return value >= MIN_MULTIPLIER && value <= MAX_REASONABLE_MULTIPLIER
    }

    /**
     * Sanitizes and rounds a multiplier to two decimal places.
     */
    fun sanitizeMultiplier(value: Double): Double {
        if (!isValidMultiplier(value)) return 1.00
        return String.format(Locale.US, "%.2f", value).toDouble()
    }

    /**
     * Cleanses a list of historical rounds:
     * - Removes invalid multipliers (< 1.00)
     * - Deduplicates by roundId, keeping the latest received record
     * - Sorts chronologically or preserves sequence
     */
    fun cleanseRounds(rounds: List<HistoricalRound>): List<HistoricalRound> {
        val seen = mutableSetOf<String>()
        val cleansed = mutableListOf<HistoricalRound>()

        for (round in rounds) {
            if (round.roundId.isNotBlank() && !seen.contains(round.roundId)) {
                if (isValidMultiplier(round.crashMultiplier)) {
                    seen.add(round.roundId)
                    cleansed.add(
                        round.copy(
                            crashMultiplier = sanitizeMultiplier(round.crashMultiplier)
                        )
                    )
                }
            }
        }
        return cleansed
    }

    /**
     * Parses and validates raw CSV content.
     * Accepted headers: roundId, crashMultiplier, timestamp (optional), status (optional)
     */
    fun parseCsvRounds(csvContent: String, source: RoundSource = RoundSource.IMPORTED): List<HistoricalRound> {
        val lines = csvContent.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (lines.isEmpty()) return emptyList()

        val results = mutableListOf<HistoricalRound>()
        val startIndex = if (lines[0].lowercase().contains("round") || lines[0].lowercase().contains("multiplier")) 1 else 0

        for (i in startIndex until lines.size) {
            val line = lines[i]
            val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
            if (parts.isEmpty()) continue

            try {
                val roundId: String
                val multiplier: Double
                var timestamp = System.currentTimeMillis() - (lines.size - i) * 15000L

                if (parts.size == 1) {
                    // Just multiplier value per line
                    multiplier = parts[0].replace("x", "", ignoreCase = true).toDouble()
                    roundId = "IMP-${10000 + i}"
                } else {
                    roundId = parts[0].ifBlank { "IMP-${10000 + i}" }
                    multiplier = parts[1].replace("x", "", ignoreCase = true).toDouble()
                    if (parts.size >= 3) {
                        parts[2].toLongOrNull()?.let { timestamp = it }
                    }
                }

                if (isValidMultiplier(multiplier)) {
                    results.add(
                        HistoricalRound(
                            roundId = roundId,
                            crashMultiplier = sanitizeMultiplier(multiplier),
                            timestamp = timestamp,
                            status = RoundStatus.FINAL,
                            source = source,
                            receivedAt = System.currentTimeMillis()
                        )
                    )
                }
            } catch (_: Exception) {
                // Ignore malformed rows
            }
        }

        return cleanseRounds(results)
    }
}
