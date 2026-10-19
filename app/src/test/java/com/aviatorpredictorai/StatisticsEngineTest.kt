package com.aviatorpredictorai

import com.aviatorpredictorai.core.StatisticsEngine
import com.aviatorpredictorai.model.HistoricalRound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatisticsEngineTest {

    @Test
    fun testDescriptiveStatistics() {
        val multipliers = listOf(1.10, 1.20, 1.50, 2.00, 3.00, 5.00, 10.00)
        val rounds = multipliers.mapIndexed { idx, m -> HistoricalRound("#RD-$idx", m) }

        val stats = StatisticsEngine.calculateDescriptiveStatistics(rounds)
        assertNotNull(stats)
        assertEquals(7, stats!!.totalRounds)
        assertEquals(1.10, stats.minMultiplier, 0.001)
        assertEquals(10.00, stats.maxMultiplier, 0.001)
        assertEquals(2.00, stats.medianMultiplier, 0.001)
        assertTrue(stats.meanMultiplier > 2.0)
        assertTrue(stats.standardDeviation > 0.0)
    }

    @Test
    fun testWilsonConfidenceInterval() {
        // 70 successes out of 100
        val (lower, upper) = StatisticsEngine.calculateWilsonScoreInterval(70, 100)
        assertTrue("Lower bound must be < 0.70", lower < 0.70)
        assertTrue("Upper bound must be > 0.70", upper > 0.70)
        assertTrue("Lower bound should be reasonable (> 0.58)", lower > 0.58)
        assertTrue("Upper bound should be reasonable (< 0.80)", upper < 0.80)

        // 0 out of 100
        val (zeroLow, zeroHigh) = StatisticsEngine.calculateWilsonScoreInterval(0, 100)
        assertEquals(0.0, zeroLow, 0.0001)
        assertTrue(zeroHigh > 0.0)

        // 100 out of 100
        val (oneLow, oneHigh) = StatisticsEngine.calculateWilsonScoreInterval(100, 100)
        assertTrue(oneLow < 1.0)
        assertEquals(1.0, oneHigh, 0.0001)
    }

    @Test
    fun testEmpiricalReachRate() {
        val rounds = listOf(
            HistoricalRound("#1", 1.20),
            HistoricalRound("#2", 1.50),
            HistoricalRound("#3", 1.85),
            HistoricalRound("#4", 2.10),
            HistoricalRound("#5", 1.05)
        )

        // Target 1.50: rounds #2, #3, #4 reached (3/5 = 60%)
        val reachRate = StatisticsEngine.calculateReachRate(rounds, 1.50)
        assertEquals(3, reachRate.reachedCount)
        assertEquals(5, reachRate.totalCount)
        assertEquals(0.60, reachRate.rate, 0.001)
        assertTrue(reachRate.confidenceIntervalLow < 0.60)
        assertTrue(reachRate.confidenceIntervalHigh > 0.60)
    }

    @Test
    fun testRollingMean() {
        val values = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val rolling = StatisticsEngine.calculateRollingMean(values, 3)
        assertEquals(5, rolling.size)
        assertEquals(1.0, rolling[0], 0.001)
        assertEquals(1.5, rolling[1], 0.001)
        assertEquals(2.0, rolling[2], 0.001) // (1+2+3)/3 = 2.0
        assertEquals(3.0, rolling[3], 0.001) // (2+3+4)/3 = 3.0
        assertEquals(4.0, rolling[4], 0.001) // (3+4+5)/3 = 4.0
    }
}
