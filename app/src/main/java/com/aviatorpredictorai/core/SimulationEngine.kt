package com.aviatorpredictorai.core

import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.SimulatedRound
import com.aviatorpredictorai.model.SimulationSummary
import kotlin.math.max

object SimulationEngine {

    /**
     * Executes backtest simulation on historical rounds chronologically without leakage.
     * Model assumptions:
     * - Fixed stake per round b.
     * - Successful collect at m yields gross payout b * m, net profit b * (m - 1).
     * - Crash below m loses entire stake -b.
     * - Assumes zero broker fees or instant execution slippage.
     */
    fun runSimulation(
        rounds: List<HistoricalRound>,
        stake: Double,
        collectMultiplier: Double
    ): SimulationSummary {
        val cleansed = DataValidation.cleanseRounds(rounds)
        if (cleansed.isEmpty()) {
            return SimulationSummary(
                totalRounds = 0,
                wins = 0,
                losses = 0,
                winRate = 0.0,
                totalStaked = 0.0,
                totalGrossPayout = 0.0,
                netProfit = 0.0,
                returnOnStakePercentage = 0.0,
                maxWinStreak = 0,
                maxLossStreak = 0,
                roundDetails = emptyList()
            )
        }

        // Chronological order (oldest to newest) for streak and drawdown analysis
        val chronological = cleansed.reversed()

        val simulatedRounds = mutableListOf<SimulatedRound>()
        var wins = 0
        var losses = 0
        var totalStaked = 0.0
        var totalGrossPayout = 0.0
        var netProfit = 0.0

        var currentWinStreak = 0
        var maxWinStreak = 0
        var currentLossStreak = 0
        var maxLossStreak = 0

        for (round in chronological) {
            val isWin = round.crashMultiplier >= collectMultiplier
            val roundNet = if (isWin) {
                stake * (collectMultiplier - 1.0)
            } else {
                -stake
            }
            val roundGross = if (isWin) stake * collectMultiplier else 0.0

            if (isWin) {
                wins++
                currentWinStreak++
                currentLossStreak = 0
                maxWinStreak = max(maxWinStreak, currentWinStreak)
            } else {
                losses++
                currentLossStreak++
                currentWinStreak = 0
                maxLossStreak = max(maxLossStreak, currentLossStreak)
            }

            totalStaked += stake
            totalGrossPayout += roundGross
            netProfit += roundNet

            simulatedRounds.add(
                SimulatedRound(
                    roundId = round.roundId,
                    crashMultiplier = round.crashMultiplier,
                    stake = stake,
                    targetMultiplier = collectMultiplier,
                    isWin = isWin,
                    netProfit = roundNet,
                    timestamp = round.timestamp
                )
            )
        }

        val totalRounds = cleansed.size
        val winRate = if (totalRounds > 0) (wins.toDouble() / totalRounds.toDouble()) * 100.0 else 0.0
        val roi = if (totalStaked > 0) (netProfit / totalStaked) * 100.0 else 0.0

        return SimulationSummary(
            totalRounds = totalRounds,
            wins = wins,
            losses = losses,
            winRate = winRate,
            totalStaked = totalStaked,
            totalGrossPayout = totalGrossPayout,
            netProfit = netProfit,
            returnOnStakePercentage = roi,
            maxWinStreak = maxWinStreak,
            maxLossStreak = maxLossStreak,
            roundDetails = simulatedRounds.reversed() // return newest first for UI display
        )
    }
}
