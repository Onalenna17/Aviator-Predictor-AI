package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.PredictionEngine
import com.example.core.SimulationEngine
import com.example.data.AviatorRepository
import com.example.model.HistoricalRound
import com.example.model.PredictionAnalysis
import com.example.model.RiskProfile
import com.example.model.SimulationSummary
import com.example.model.UserSettings
import com.example.service.LiveFeedState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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
                    _statusMessage.value = "Live WebSocket Connected · ${feed.latencyMs}ms latency · ${feed.messagesReceived} frames"
                } else if (feed.isConnecting) {
                    _statusMessage.value = feed.statusText
                }
            }
        }
    }

    fun runAlgorithm() {
        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            _isAnalyzing.value = true
            _statusMessage.value = "Validating sample window & calculating Wilson intervals..."
            delay(350) // visual feedback for computation
            executeAlgorithmInternal(rounds.value, settings.value)
            _isAnalyzing.value = false
            _statusMessage.value = "Analysis Completed · Optimal Strategy Calibrated"
        }
    }

    private fun executeAlgorithmInternal(roundsList: List<HistoricalRound>, currentSettings: UserSettings) {
        val analysis = PredictionEngine.runAlgorithm(
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
        _statusMessage.value = "Analysis and feed stopped by user."
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
        _statusMessage.value = "Live Demo Feed Active (Generating Realistic Crash Rounds)"
        demoStreamJob?.cancel()
        demoStreamJob = viewModelScope.launch {
            while (isActive && _isDemoStreaming.value) {
                delay(7000) // simulated round every 7 seconds
                val newRound = repository.addNewSimulatedRound()
                _statusMessage.value = "New Confirmed Round: ${newRound.roundId} @ ${newRound.crashMultiplier}x"
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
        _statusMessage.value = "Live Demo Feed Paused"
    }

    fun connectLiveFeed(url: String) {
        stopDemoStream()
        _statusMessage.value = "Connecting to Live WebSocket Feed..."
        repository.connectLiveFeed(url)
    }

    fun disconnectLiveFeed() {
        repository.disconnectLiveFeed()
        _statusMessage.value = "Live WebSocket Disconnected"
    }

    fun setLiveEndpointUrl(url: String) {
        repository.liveFeedService.setEndpointUrl(url)
    }

    fun runSimulation() {
        executeSimulationInternal(rounds.value, settings.value)
        _statusMessage.value = "Simulation Updated for Target ${settings.value.collectMultiplier}x"
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
        executeSimulationInternal(rounds.value, newSettings)
        _statusMessage.value = "Settings Saved Successfully"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterOutcome(filter: String) {
        _filterOutcome.value = filter
    }

    fun selectRoundForDetails(round: HistoricalRound?) {
        _selectedRoundForDetails.value = round
    }

    fun importCsv(csvContent: String): Pair<Int, Int> {
        val res = repository.importCsv(csvContent)
        _statusMessage.value = "Imported ${res.second} new historical rounds from CSV"
        executeAlgorithmInternal(rounds.value, settings.value)
        executeSimulationInternal(rounds.value, settings.value)
        return res
    }

    fun importJson(jsonContent: String): Pair<Int, Int> {
        val res = repository.importJson(jsonContent)
        _statusMessage.value = "Imported ${res.second} new historical rounds from JSON"
        executeAlgorithmInternal(rounds.value, settings.value)
        executeSimulationInternal(rounds.value, settings.value)
        return res
    }

    fun resetToDemoData() {
        repository.resetToDemoData()
        _statusMessage.value = "Reset to verified demo dataset"
        executeAlgorithmInternal(rounds.value, settings.value)
        executeSimulationInternal(rounds.value, settings.value)
    }

    fun clearAllData() {
        repository.clearAllData()
        _statusMessage.value = "Cleared all local records"
        _predictionAnalysis.value = null
        _simulationSummary.value = null
    }

    override fun onCleared() {
        super.onCleared()
        demoStreamJob?.cancel()
        analysisJob?.cancel()
        repository.disconnectLiveFeed()
    }
}
