package com.aviatorpredictorai.core

import com.aviatorpredictorai.model.DescriptiveStatistics
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.MultiplierBucket
import com.aviatorpredictorai.model.ReachRate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object StatisticsEngine {

    private const val Z_95 = 1.95996398454 // 95% two-tailed standard normal quantile

    /**
     * Calculates Wilson Score Interval for binomial proportion.
     * Returns Pair(lowerBound, upperBound).
     */
    fun calculateWilsonScoreInterval(successes: Int, total: Int): Pair<Double, Double> {
        if (total <= 0) return Pair(0.0, 0.0)
        val p = successes.toDouble() / total.toDouble()
        val n = total.toDouble()
        val z = Z_95

        val denominator = 1.0 + (z * z) / n
        val center = (p + (z * z) / (2.0 * n)) / denominator
        val varianceTerm = (p * (1.0 - p)) / n + (z * z) / (4.0 * n * n)
        val margin = (z / denominator) * sqrt(max(0.0, varianceTerm))

        val lower = max(0.0, center - margin)
        val upper = min(1.0, center + margin)
        return Pair(lower, upper)
    }

    /**
     * Calculates empirical reach rate for a target multiplier m in a list of rounds.
     */
    fun calculateReachRate(rounds: List<HistoricalRound>, targetMultiplier: Double): ReachRate {
        val total = rounds.size
        if (total == 0) {
            return ReachRate(targetMultiplier, 0, 0, 0.0, 0.0, 0.0)
        }

        val reachedCount = rounds.count { it.crashMultiplier >= targetMultiplier }
        val rate = reachedCount.toDouble() / total.toDouble()
        val (ciLow, ciHigh) = calculateWilsonScoreInterval(reachedCount, total)

        return ReachRate(
            targetMultiplier = targetMultiplier,
            reachedCount = reachedCount,
            totalCount = total,
            rate = rate,
            confidenceIntervalLow = ciLow,
            confidenceIntervalHigh = ciHigh
        )
    }

    /**
     * Calculates full descriptive statistics.
     */
    fun calculateDescriptiveStatistics(rounds: List<HistoricalRound>): DescriptiveStatistics? {
        if (rounds.isEmpty()) return null

        val values = rounds.map { it.crashMultiplier }.sorted()
        val n = values.size
        val mean = values.average()
        val min = values.first()
        val max = values.last()

        val variance = values.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)

        fun percentile(p: Double): Double {
            if (n == 1) return values[0]
            val index = p * (n - 1)
            val lower = index.toInt()
            val upper = min(lower + 1, n - 1)
            val fraction = index - lower
            return values[lower] + fraction * (values[upper] - values[lower])
        }

        return DescriptiveStatistics(
            totalRounds = n,
            meanMultiplier = mean,
            medianMultiplier = percentile(0.50),
            minMultiplier = min,
            maxMultiplier = max,
            standardDeviation = stdDev,
            p25 = percentile(0.25),
            p50 = percentile(0.50),
            p75 = percentile(0.75),
            p90 = percentile(0.90),
            p99 = percentile(0.99)
        )
    }

    /**
     * Calculates multiplier frequency distribution across standard Aviator crash buckets.
     */
    fun calculateDistributionBuckets(rounds: List<HistoricalRound>): List<MultiplierBucket> {
        val total = rounds.size
        if (total == 0) return emptyList()

        val definitions = listOf(
            Triple("1.00x - 1.20x", 1.00, 1.20),
            Triple("1.21x - 1.50x", 1.20001, 1.50),
            Triple("1.51x - 2.00x", 1.50001, 2.00),
            Triple("2.01x - 3.00x", 2.00001, 3.00),
            Triple("3.01x - 5.00x", 3.00001, 5.00),
            Triple("5.01x - 10.00x", 5.00001, 10.00),
            Triple("10.00x+", 10.00001, 1000000.0)
        )

        return definitions.map { (label, minVal, maxVal) ->
            val count = rounds.count { it.crashMultiplier in minVal..maxVal }
            MultiplierBucket(
                label = label,
                min = minVal,
                max = maxVal,
                count = count,
                percentage = (count.toDouble() / total.toDouble()) * 100.0
            )
        }
    }

    /**
     * Calculates trailing rolling mean series for chart rendering.
     */
    fun calculateRollingMean(multipliers: List<Double>, window: Int = 10): List<Double> {
        if (multipliers.isEmpty()) return emptyList()
        val result = mutableListOf<Double>()
        for (i in multipliers.indices) {
            val start = max(0, i - window + 1)
            val sub = multipliers.subList(start, i + 1)
            result.add(sub.average())
        }
        return result
    }

    /**
     * Identifies how many consecutive recent crashes were under 1.50x.
     */
    fun countRecentConsecutiveLowCrashes(rounds: List<HistoricalRound>, threshold: Double = 1.50): Int {
        var count = 0
        // Newest rounds are at index 0
        for (round in rounds) {
            if (round.crashMultiplier < threshold) {
                count++
            } else {
                break
            }
        }
        return count
    }
}
