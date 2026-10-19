package com.aviatorpredictorai

import com.aviatorpredictorai.core.SimulationEngine
import com.aviatorpredictorai.model.HistoricalRound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationEngineTest {

    @Test
    fun testSimulationWinsAndLosses() {
        val rounds = listOf(
            HistoricalRound("#1", 2.00), // Win at 1.85 (profit: 1.0 * 0.85 = +0.85)
            HistoricalRound("#2", 1.50), // Loss at 1.85 (net: -1.0)
            HistoricalRound("#3", 3.00), // Win at 1.85 (+0.85)
            HistoricalRound("#4", 1.85), // Exact meet = Win (+0.85)
            HistoricalRound("#5", 1.10)  // Loss (-1.0)
        )

        val summary = SimulationEngine.runSimulation(
            rounds = rounds,
            stake = 1.0,
            collectMultiplier = 1.85
        )

        assertEquals(5, summary.totalRounds)
        assertEquals(3, summary.wins)
        assertEquals(2, summary.losses)
        assertEquals(60.0, summary.winRate, 0.001)
        assertEquals(5.0, summary.totalStaked, 0.001)

        // Net: 3 * 0.85 - 2 * 1.0 = 2.55 - 2.0 = +0.55
        assertEquals(0.55, summary.netProfit, 0.01)
        assertEquals(11.0, summary.returnOnStakePercentage, 0.1) // 0.55 / 5.0 * 100% = 11%
    }

    @Test
    fun testStreakTracking() {
        val rounds = listOf(
            HistoricalRound("#1", 2.00), // W
            HistoricalRound("#2", 2.50), // W
            HistoricalRound("#3", 3.00), // W
            HistoricalRound("#4", 1.20), // L
            HistoricalRound("#5", 1.10)  // L
        )

        val summary = SimulationEngine.runSimulation(rounds, 1.0, 1.80)
        assertEquals(3, summary.maxWinStreak)
        assertEquals(2, summary.maxLossStreak)
    }

    @Test
    fun testEmptySimulation() {
        val summary = SimulationEngine.runSimulation(emptyList(), 1.0, 1.50)
        assertEquals(0, summary.totalRounds)
        assertEquals(0.0, summary.netProfit, 0.001)
    }
}
