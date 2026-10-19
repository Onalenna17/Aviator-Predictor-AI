package com.aviatorpredictorai.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aviatorpredictorai.core.PredictionEngine
import com.aviatorpredictorai.core.SimulationEngine
import com.aviatorpredictorai.data.AviatorRepository
import com.aviatorpredictorai.model.HistoricalRound
import com.aviatorpredictorai.model.PredictionAnalysis
import com.aviatorpredictorai.model.RiskProfile
import com.aviatorpredictorai.model.SimulationSummary
import com.aviatorpredictorai.model.UserSettings
import com.aviatorpredictorai.service.LiveFeedState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class AviatorViewModel(application: Application) : AndroidViewModel(application) {

    val repository = AviatorRepository(application.applicationContext)

    val rounds: StateFlow<List<HistoricalRound>> = repository.rounds
    val settings: StateFlow<UserSettings> = repository.settings
    val rejectedRecords: StateFlow<Int> = repository.rejectedRecordCount
    val liveFeedState: StateFlow<LiveFeedState> = repository.liveFeedState

    private val _predictionAnalysis = MutableStateFlow<PredictionAnalysis?>(null)
    val predictionAnalysis: StateFlow<PredictionAnalysis?> = _predictionAnalysis.asStateFlow()

    private val _simulationSummary = MutableStateFlow<SimulationSummary?>(null)
    val simulationSummary: StateFlow<SimulationSummary?> = _simulationSummary.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _isDemoStreaming = MutableStateFlow(false)
    val isDemoStreaming: StateFlow<Boolean> = _isDemoStreaming.asStateFlow()

    private val _statusMessage = MutableStateFlow("System Ready · Awaiting Algorithm Run")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _selectedRoundForDetails = MutableStateFlow<HistoricalRound?>(null)
    val selectedRoundForDetails: StateFlow<HistoricalRound?> = _selectedRoundForDetails.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterOutcome = MutableStateFlow("ALL") // "ALL", "WIN", "LOSS"
    val filterOutcome: StateFlow<String> = _filterOutcome.asStateFlow()

    private var demoStreamJob: Job? = null
    private var analysisJob: Job? = null

    init {
        // Run initial calculation when data is loaded and update dynamically when new live rounds arrive
        viewModelScope.launch {
            rounds.collectLatest { roundList ->
                if (settings.value.autoRunOnNewRound && roundList.isNotEmpty() && _predictionAnalysis.value == null) {
                    executeAlgorithmInternal(roundList, settings.value)
                }
                // Automatically update simulation if rounds change
                executeSimulationInternal(roundList, settings.value)
            }
        }

        // Monitor LiveFeed state for status banners
        viewModelScope.launch {
            liveFeedState.collectLatest { feed ->
                if (feed.isConnected) {
                    val inFlightText = if (feed.isFlightActive) " · Flight @ ${feed.currentInFlightMultiplier}x" else ""
                    _statusMessage.value = "Live Verified Stream · ${feed.latencyMs}ms · ${feed.messagesReceived} rounds$inFlightText"
                } else if (feed.isConnecting) {
                    _statusMessage.value = feed.statusText
                }
            }
        }
    }

    /**
     * Immediately calculates and reveals the next suggested/predicted multiplier for the upcoming round.
     */
    fun runAlgorithm() {
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            _isAnalyzing.value = true
            _statusMessage.value = "Calibrating empirical quantiles & Wilson interval..."
            delay(140) // Snappy 140ms feedback pulse
            val analysis = PredictionEngine.generateNextPrediction(
                rounds = rounds.value,
                analysisWindow = settings.value.analysisWindow,
                riskProfile = settings.value.riskProfile,
                baseStake = settings.value.betAmount
            )
            _predictionAnalysis.value = analysis
            _isAnalyzing.value = false
            _statusMessage.value = "Predicted Target: ${String.format(Locale.US, "%.2f", analysis.predictedNextPossibleMultiplier)}x · Prob: ${String.format(Locale.US, "%.1f%%", analysis.estimatedReachRate * 100)}"
        }
    }

    private fun executeAlgorithmInternal(roundsList: List<HistoricalRound>, currentSettings: UserSettings) {
        val analysis = PredictionEngine.generateNextPrediction(
            rounds = roundsList,
            analysisWindow = currentSettings.analysisWindow,
            riskProfile = currentSettings.riskProfile,
            baseStake = currentSettings.betAmount
        )
        _predictionAnalysis.value = analysis
    }

    fun stopActiveProcessing() {
        analysisJob?.cancel()
        analysisJob = null
        stopDemoStream()
        disconnectLiveFeed()
        _isAnalyzing.value = false
        _statusMessage.value = "Processing and feeds stopped."
    }

    fun toggleLiveMode() {
        if (liveFeedState.value.isConnected || liveFeedState.value.isConnecting) {
            disconnectLiveFeed()
        } else {
            connectLiveFeed("wss://echo.websocket.events")
        }
    }

    fun toggleDemoStream() {
        if (_isDemoStreaming.value) {
            stopDemoStream()
        } else {
            startDemoStream()
        }
    }

    fun startDemoStream() {
        if (_isDemoStreaming.value) return
        _isDemoStreaming.value = true
        _statusMessage.value = "Demo Round Generator Active"
        demoStreamJob?.cancel()
        demoStreamJob = viewModelScope.launch {
            while (isActive && _isDemoStreaming.value) {
                delay(5000)
                val newRound = repository.addNewSimulatedRound()
                _statusMessage.value = "New Round: ${newRound.roundId} @ ${newRound.crashMultiplier}x"
                if (settings.value.autoRunOnNewRound) {
                    executeAlgorithmInternal(rounds.value, settings.value)
                }
                executeSimulationInternal(rounds.value, settings.value)
            }
        }
    }

    fun stopDemoStream() {
        _isDemoStreaming.value = false
        demoStreamJob?.cancel()
        demoStreamJob = null
        _statusMessage.value = "Demo Feed Paused"
    }

    fun connectLiveFeed(url: String = "wss://echo.websocket.events") {
        stopDemoStream()
        _statusMessage.value = "Connecting to Verified Live Stream..."
        repository.connectLiveFeed(url)
    }

    fun disconnectLiveFeed() {
        repository.disconnectLiveFeed()
        _statusMessage.value = "Live Stream Disconnected"
    }

    fun setLiveEndpointUrl(url: String) {
        repository.liveFeedService.setEndpointUrl(url)
    }

    fun runSimulation() {
        executeSimulationInternal(rounds.value, settings.value)
        _statusMessage.value = "Simulation Backtest Updated"
    }

    private fun executeSimulationInternal(roundsList: List<HistoricalRound>, currentSettings: UserSettings) {
        val summary = SimulationEngine.runSimulation(
            rounds = roundsList,
            stake = currentSettings.betAmount,
            collectMultiplier = currentSettings.collectMultiplier
        )
        _simulationSummary.value = summary
    }

    fun adjustOffset(deltaSeconds: Long) {
        repository.adjustOffset(deltaSeconds)
        _statusMessage.value = "Data Offset adjusted by ${if (deltaSeconds > 0) "+$deltaSeconds" else "$deltaSeconds"}s"
    }

    fun resetOffset() {
        repository.resetOffset()
        _statusMessage.value = "Data Offset reset to 0s"
    }

    fun updateBetAmount(newAmount: Double) {
        val sanitized = newAmount.coerceIn(0.10, settings.value.spendingLimit)
        val updated = settings.value.copy(betAmount = sanitized)
        repository.saveSettings(updated)
        executeSimulationInternal(rounds.value, updated)
    }

    fun updateCollectMultiplier(newTarget: Double) {
        val sanitized = newTarget.coerceIn(1.01, 1000.0)
        val updated = settings.value.copy(collectMultiplier = sanitized)
        repository.saveSettings(updated)
        executeSimulationInternal(rounds.value, updated)
    }

    fun updateAnalysisWindow(newWindow: Int) {
        val updated = settings.value.copy(analysisWindow = newWindow)
        repository.saveSettings(updated)
        executeAlgorithmInternal(rounds.value, updated)
    }

    fun updateRiskProfile(profile: RiskProfile) {
        val updated = settings.value.copy(riskProfile = profile)
        repository.saveSettings(updated)
        executeAlgorithmInternal(rounds.value, updated)
    }

    fun cycleTheme() {
        repository.cycleTheme()
        _statusMessage.value = "Theme switched to ${settings.value.themeMode.label}"
    }

    fun saveSettings(newSettings: UserSettings) {
        repository.saveSettings(newSettings)
        executeAlgorithmInternal(rounds.value, newSettings)
    }

    fun selectRoundForDetails(round: HistoricalRound?) {
        _selectedRoundForDetails.value = round
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterOutcome(filter: String) {
        _filterOutcome.value = filter
    }

    fun importCsv(csvContent: String): Pair<Int, Int> {
        val res = repository.importCsv(csvContent)
        if (res.second > 0) {
            _statusMessage.value = "Imported ${res.second} new crash rounds from CSV"
            executeAlgorithmInternal(rounds.value, settings.value)
            executeSimulationInternal(rounds.value, settings.value)
        }
        return res
    }

    fun importJson(jsonContent: String): Pair<Int, Int> {
        val res = repository.importJson(jsonContent)
        if (res.second > 0) {
            _statusMessage.value = "Imported ${res.second} new crash rounds from JSON"
            executeAlgorithmInternal(rounds.value, settings.value)
            executeSimulationInternal(rounds.value, settings.value)
        }
        return res
    }

    fun clearAllData() {
        repository.clearAllData()
        _predictionAnalysis.value = null
        _simulationSummary.value = null
        _statusMessage.value = "Dataset Cleared · Awaiting Ingestion"
    }

    fun resetToDemoData() {
        repository.resetToDemoData()
        executeAlgorithmInternal(rounds.value, settings.value)
        executeSimulationInternal(rounds.value, settings.value)
        _statusMessage.value = "Reset to Demonstration Baseline (120 rounds)"
    }
}
