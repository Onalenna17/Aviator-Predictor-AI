package com.example

import com.example.core.PredictionEngine
import com.example.model.HistoricalRound
import com.example.model.RiskProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PredictionEngineTest {

    @Test
    fun testInsufficientDataHandling() {
        val smallList = listOf(
            HistoricalRound("#1", 1.50),
            HistoricalRound("#2", 2.00)
        )

        val result = PredictionEngine.runAlgorithm(
            rounds = smallList,
            analysisWindow = 50,
            riskProfile = RiskProfile.BALANCED,
            baseStake = 1.0
        )

        assertEquals("insufficient_data", result.status)
        assertTrue(result.limitations.contains("Insufficient sample size"))
        assertEquals(2, result.sampleSize)
    }

    @Test
    fun testAlgorithmExecutionAndSuggestedTarget() {
        // Create 30 valid rounds
        val multipliers = listOf(
            1.20, 1.45, 1.88, 2.10, 1.15, 3.20, 1.05, 1.95, 2.40, 1.30,
            1.50, 1.10, 4.50, 1.70, 1.25, 2.80, 1.02, 1.65, 2.00, 1.35,
            1.18, 5.20, 1.40, 1.90, 1.08, 2.20, 1.55, 1.12, 3.00, 1.60
        )
        val rounds = multipliers.mapIndexed { idx, m -> HistoricalRound("#RD-$idx", m) }

        val conservative = PredictionEngine.runAlgorithm(rounds, 30, RiskProfile.CONSERVATIVE, 1.0)
        assertEquals("success", conservative.status)
        assertTrue(conservative.suggestedCollectMultiplier >= 1.10)
        assertTrue(conservative.estimatedReachRate >= 0.70)
        assertNotNull(conservative.descriptiveStats)

        val balanced = PredictionEngine.runAlgorithm(rounds, 30, RiskProfile.BALANCED, 1.0)
        assertEquals("success", balanced.status)
        assertTrue(balanced.suggestedCollectMultiplier >= conservative.suggestedCollectMultiplier)

        val aggressive = PredictionEngine.runAlgorithm(rounds, 30, RiskProfile.AGGRESSIVE, 1.0)
        assertEquals("success", aggressive.status)
        assertTrue(aggressive.suggestedCollectMultiplier >= balanced.suggestedCollectMultiplier)
    }
}
