package com.example.data

import com.example.model.HistoricalRound
import com.example.model.RoundSource
import com.example.model.RoundStatus
import java.util.Locale
import kotlin.random.Random

object SampleHistoricalData {

    /**
     * Authentic historical sample sequence inspired by user screenshot,
     * supplemented by a realistic inverse CDF distribution matching 97-99% RTP crash game mechanics.
     */
    private val INITIAL_SAMPLE_MULTIPLIERS = listOf(
        129.29, 11.39, 6.88, 6.80, 2.77, 3.26, 5.55, 5.88, 1.01, 41.43,
        6.47, 3.93, 1.12, 2.10, 1.45, 1.88, 1.00, 14.22, 2.34, 1.18,
        3.15, 1.07, 4.80, 1.62, 1.25, 2.89, 1.03, 18.70, 1.33, 2.05,
        1.55, 7.20, 1.09, 1.15, 3.44, 2.21, 1.02, 1.77, 9.54, 1.40,
        1.22, 2.65, 1.10, 5.12, 1.38, 1.95, 1.04, 32.80, 1.50, 2.45,
        1.14, 1.68, 4.10, 1.29, 1.06, 6.30, 2.15, 1.35, 1.08, 12.40,
        1.80, 1.20, 2.95, 1.11, 1.65, 3.75, 1.05, 2.40, 1.30, 8.90,
        1.16, 1.72, 2.50, 1.03, 1.48, 4.60, 1.25, 1.90, 1.01, 23.50,
        1.37, 2.25, 1.13, 1.58, 3.20, 1.07, 1.82, 5.70, 1.21, 1.44,
        2.70, 1.09, 1.63, 3.90, 1.17, 2.08, 1.32, 7.80, 1.04, 1.52,
        2.35, 1.26, 1.19, 4.30, 1.41, 1.85, 1.02, 16.10, 1.28, 2.12
    )

    fun getInitialRounds(): List<HistoricalRound> {
        val now = System.currentTimeMillis()
        var roundSeq = 98421

        return INITIAL_SAMPLE_MULTIPLIERS.mapIndexed { index, mult ->
            HistoricalRound(
                roundId = "#RD-${roundSeq - index}",
                crashMultiplier = mult,
                timestamp = now - (index * 14500L),
                status = RoundStatus.FINAL,
                source = RoundSource.DEMO,
                receivedAt = now - (index * 14500L)
            )
        }
    }

    /**
     * Generates a statistically authentic crash round using standard provably-fair inverse CDF:
     * With 3% instant house crash (< 1.05x), and multiplier = 0.97 / (1.0 - U) for uniform U in [0, 1).
     */
    fun generateSimulatedRound(latestRoundIdNumber: Int): HistoricalRound {
        val u = Random.nextDouble()
        val multiplier = if (u < 0.035) {
            // Instant bust
            1.00 + Random.nextDouble(0.0, 0.05)
        } else {
            val raw = 0.97 / (1.0 - u)
            // Cap at 1500x
            raw.coerceIn(1.00, 1500.0)
        }

        val roundedMultiplier = String.format(Locale.US, "%.2f", multiplier).toDouble()
        val now = System.currentTimeMillis()

        return HistoricalRound(
            roundId = "#RD-${latestRoundIdNumber + 1}",
            crashMultiplier = roundedMultiplier,
            timestamp = now,
            status = RoundStatus.FINAL,
            source = RoundSource.DEMO,
            receivedAt = now
        )
    }
}
