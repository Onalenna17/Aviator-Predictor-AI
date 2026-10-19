package com.aviatorpredictorai.model

enum class RoundStatus {
    PENDING,
    FINAL,
    UNKNOWN
}

enum class RoundSource {
    VERIFIED,
    IMPORTED,
    DEMO
}

enum class AppThemeMode(val label: String, val description: String) {
    DARK_NAVY_GREEN("Navy & Green Rims", "Deep cyber navy with radiant neon green glowing rims (Default)"),
    PRISTINE_LIGHT("Pristine Light & Emerald", "Crisp high-contrast light theme with slate cards & vivid emerald"),
    CYBER_GREEN("Matrix Emerald", "Deep obsidian green terminal with radiant lime & emerald glows"),
    DARK_NAVY_CYAN("Cyber Navy & Cyan", "Deep cyber navy with bright cyan accents and golden ring"),
    OBSIDIAN_GOLD("Obsidian & Gold", "Stealth black chassis with luminous gold neon accents"),
    CRIMSON_TERMINAL("Crimson & Red", "Deep ruby dark terminal with fiery red & coral trajectory"),
    ROYAL_AMETHYST("Royal Amethyst", "Deep midnight violet with radiant purple & electric lilac rims"),
    SLATE_LIGHT("Slate Crisp Light", "Minimalist crisp slate paper theme with deep navy text and cobalt rims")
}

enum class RiskProfile(val label: String, val targetReachRate: Double, val description: String) {
    CONSERVATIVE("Conservative", 0.80, "Targets ~80% historical reach rate (lower multipliers: 1.15x–1.35x) with minimal drawdowns"),
    BALANCED("Balanced", 0.60, "Targets ~60% historical reach rate (medium multipliers: 1.50x–1.95x) balancing win rate & return"),
    AGGRESSIVE("Aggressive", 0.38, "Optimizes empirical Expected Value (EV) with higher multiplier targets (2.00x–3.50x)")
}

enum class DataMode(val label: String) {
    DEMO("Demo Mode"),
    IMPORTED("Imported Historical"),
    VERIFIED("Verified Live Feed")
}

data class HistoricalRound(
    val roundId: String,
    val crashMultiplier: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: RoundStatus = RoundStatus.FINAL,
    val source: RoundSource = RoundSource.DEMO,
    val receivedAt: Long = System.currentTimeMillis()
)

data class DescriptiveStatistics(
    val totalRounds: Int,
    val meanMultiplier: Double,
    val medianMultiplier: Double,
    val minMultiplier: Double,
    val maxMultiplier: Double,
    val standardDeviation: Double,
    val p25: Double,
    val p50: Double,
    val p75: Double,
    val p90: Double,
    val p99: Double
)

data class ReachRate(
    val targetMultiplier: Double,
    val reachedCount: Int,
    val totalCount: Int,
    val rate: Double,
    val confidenceIntervalLow: Double,
    val confidenceIntervalHigh: Double
)

data class MultiplierBucket(
    val label: String,
    val min: Double,
    val max: Double,
    val count: Int,
    val percentage: Double
)

data class PredictionAnalysis(
    val status: String, // "success", "insufficient_data", "calculating"
    val suggestedCollectMultiplier: Double,
    val estimatedReachRate: Double,
    val confidenceIntervalLow: Double,
    val confidenceIntervalHigh: Double,
    val sampleSize: Int,
    val method: String,
    val generatedAt: Long = System.currentTimeMillis(),
    val limitations: String,
    val expectedValuePerUnitStake: Double,
    val descriptiveStats: DescriptiveStatistics?,
    val reachRates: List<ReachRate>,
    val distributionBuckets: List<MultiplierBucket>,
    val consecutiveLowCrashes: Int,
    val calculationTimeMs: Long = 0L,
    val predictedNextPossibleMultiplier: Double = 1.85,
    val predictedConfidenceScore: Double = 78.5,
    val predictedRangeLow: Double = 1.45,
    val predictedRangeHigh: Double = 2.40
)

data class SimulatedRound(
    val roundId: String,
    val crashMultiplier: Double,
    val stake: Double,
    val targetMultiplier: Double,
    val isWin: Boolean,
    val netProfit: Double,
    val timestamp: Long
)

data class SimulationSummary(
    val totalRounds: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val totalStaked: Double,
    val totalGrossPayout: Double,
    val netProfit: Double,
    val returnOnStakePercentage: Double,
    val maxWinStreak: Int,
    val maxLossStreak: Int,
    val roundDetails: List<SimulatedRound>
)

data class UserSettings(
    val betAmount: Double = 1.00,
    val collectMultiplier: Double = 1.85,
    val analysisWindow: Int = 50,
    val riskProfile: RiskProfile = RiskProfile.BALANCED,
    val dataMode: DataMode = DataMode.DEMO,
    val dataOffsetSeconds: Long = 0L,
    val spendingLimit: Double = 500.0,
    val autoRunOnNewRound: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.DARK_NAVY_GREEN
)
