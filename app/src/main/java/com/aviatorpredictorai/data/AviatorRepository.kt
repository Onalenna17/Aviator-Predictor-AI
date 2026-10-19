package com.aviatorpredictorai.data

import android.content.Context
import android.content.SharedPreferences
import com.aviatorpredictorai.core.DataValidation
import com.aviatorpredictorai.model.AppThemeMode
import com.aviatorpredictorai.model.DataMode
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.RiskProfile
import com.aviatorpredictorai.model.RoundSource
import com.aviatorpredictorai.model.RoundStatus
import com.aviatorpredictorai.model.UserSettings
import com.aviatorpredictorai.service.LiveFeedService
import com.aviatorpredictorai.service.LiveFeedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class AviatorRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aviator_predictor_prefs", Context.MODE_PRIVATE)

    private val _rounds = MutableStateFlow<List<HistoricalRound>>(SampleHistoricalData.getInitialRounds())
    val rounds: StateFlow<List<HistoricalRound>> = _rounds.asStateFlow()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _rejectedRecordCount = MutableStateFlow(0)
    val rejectedRecordCount: StateFlow<Int> = _rejectedRecordCount.asStateFlow()

    private var latestRoundSeq = 98421

    val liveFeedService = LiveFeedService { newVerifiedRound ->
        addLiveVerifiedRound(newVerifiedRound)
    }

    val liveFeedState: StateFlow<LiveFeedState> = liveFeedService.feedState

    private fun loadSettings(): UserSettings {
        val bet = prefs.getFloat("betAmount", 1.00f).toDouble()
        val collect = prefs.getFloat("collectMultiplier", 1.85f).toDouble()
        val window = prefs.getInt("analysisWindow", 50)
        val riskProfileName = prefs.getString("riskProfile", RiskProfile.BALANCED.name)
        val riskProfile = try {
            RiskProfile.valueOf(riskProfileName ?: RiskProfile.BALANCED.name)
        } catch (_: Exception) {
            RiskProfile.BALANCED
        }
        val dataModeName = prefs.getString("dataMode", DataMode.DEMO.name)
        val dataMode = try {
            DataMode.valueOf(dataModeName ?: DataMode.DEMO.name)
        } catch (_: Exception) {
            DataMode.DEMO
        }
        val offset = prefs.getLong("dataOffsetSeconds", 0L)
        val spendingLimit = prefs.getFloat("spendingLimit", 500.0f).toDouble()
        val autoRun = prefs.getBoolean("autoRunOnNewRound", true)
        val themeModeName = prefs.getString("themeMode", AppThemeMode.DARK_NAVY_GREEN.name)
        val themeMode = try {
            AppThemeMode.valueOf(themeModeName ?: AppThemeMode.DARK_NAVY_GREEN.name)
        } catch (_: Exception) {
            AppThemeMode.DARK_NAVY_GREEN
        }

        return UserSettings(
            betAmount = bet,
            collectMultiplier = collect,
            analysisWindow = window,
            riskProfile = riskProfile,
            dataMode = dataMode,
            dataOffsetSeconds = offset,
            spendingLimit = spendingLimit,
            autoRunOnNewRound = autoRun,
            themeMode = themeMode
        )
    }

    fun saveSettings(newSettings: UserSettings) {
        prefs.edit()
            .putFloat("betAmount", newSettings.betAmount.toFloat())
            .putFloat("collectMultiplier", newSettings.collectMultiplier.toFloat())
            .putInt("analysisWindow", newSettings.analysisWindow)
            .putString("riskProfile", newSettings.riskProfile.name)
            .putString("dataMode", newSettings.dataMode.name)
            .putLong("dataOffsetSeconds", newSettings.dataOffsetSeconds)
            .putFloat("spendingLimit", newSettings.spendingLimit.toFloat())
            .putBoolean("autoRunOnNewRound", newSettings.autoRunOnNewRound)
            .putString("themeMode", newSettings.themeMode.name)
            .apply()
        _settings.value = newSettings
    }

    fun cycleTheme() {
        val current = _settings.value.themeMode
        val allThemes = AppThemeMode.values()
        val currentIndex = allThemes.indexOf(current)
        val next = allThemes[(currentIndex + 1) % allThemes.size]
        saveSettings(_settings.value.copy(themeMode = next))
    }

    fun adjustOffset(deltaSeconds: Long) {
        val current = _settings.value
        val newOffset = current.dataOffsetSeconds + deltaSeconds
        saveSettings(current.copy(dataOffsetSeconds = newOffset))
    }

    fun resetOffset() {
        val current = _settings.value
        saveSettings(current.copy(dataOffsetSeconds = 0L))
    }

    fun addLiveVerifiedRound(round: HistoricalRound) {
        val current = _rounds.value.toMutableList()
        if (current.none { it.roundId == round.roundId }) {
            current.add(0, round) // newest at top
            _rounds.value = current
        }
    }

    fun addNewSimulatedRound(): HistoricalRound {
        latestRoundSeq++
        val newRound = SampleHistoricalData.generateSimulatedRound(latestRoundSeq)
        addLiveVerifiedRound(newRound)
        return newRound
    }

    fun connectLiveFeed(url: String) {
        saveSettings(_settings.value.copy(dataMode = DataMode.VERIFIED))
        liveFeedService.connect(url)
    }

    fun disconnectLiveFeed() {
        liveFeedService.disconnect()
    }

    fun importCsv(csvContent: String): Pair<Int, Int> {
        val imported = DataValidation.parseCsvRounds(csvContent, RoundSource.IMPORTED)
        if (imported.isEmpty()) {
            return Pair(0, 0)
        }
        val current = _rounds.value
        val combined = (imported + current).distinctBy { it.roundId }
        val addedCount = combined.size - current.size
        _rounds.value = combined
        return Pair(imported.size, addedCount)
    }

    fun importJson(jsonContent: String): Pair<Int, Int> {
        val imported = mutableListOf<HistoricalRound>()
        var rejected = 0
        try {
            val jsonArray = if (jsonContent.trim().startsWith("[")) {
                JSONArray(jsonContent)
            } else {
                val obj = JSONObject(jsonContent)
                obj.optJSONArray("rounds") ?: JSONArray()
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val mult = item.optDouble("crashMultiplier", item.optDouble("multiplier", -1.0))
                val roundId = item.optString("roundId", "JSON-${1000 + i}")
                val ts = item.optLong("timestamp", System.currentTimeMillis())

                if (DataValidation.isValidMultiplier(mult)) {
                    imported.add(
                        HistoricalRound(
                            roundId = roundId,
                            crashMultiplier = DataValidation.sanitizeMultiplier(mult),
                            timestamp = ts,
                            status = RoundStatus.FINAL,
                            source = RoundSource.IMPORTED,
                            receivedAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    rejected++
                }
            }
        } catch (_: Exception) {
            rejected++
        }

        _rejectedRecordCount.value += rejected
        if (imported.isNotEmpty()) {
            val current = _rounds.value
            val combined = (imported + current).distinctBy { it.roundId }
            val addedCount = combined.size - current.size
            _rounds.value = combined
            return Pair(imported.size, addedCount)
        }
        return Pair(0, 0)
    }

    fun clearAllData() {
        _rounds.value = emptyList()
    }

    fun resetToDemoData() {
        _rounds.value = SampleHistoricalData.getInitialRounds()
        latestRoundSeq = 98421
    }
}
