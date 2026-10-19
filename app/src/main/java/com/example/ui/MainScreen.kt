package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.CsvExporter
import com.example.model.HistoricalRound
import com.example.model.RoundSource
import com.example.model.RoundStatus
import com.example.ui.components.AIInsightsPanel
import com.example.ui.components.ActionButtons
import com.example.ui.components.AppHeader
import com.example.ui.components.FloatingBottomNav
import com.example.ui.components.ImportExportDialog
import com.example.ui.components.LiveFeedConsolePanel
import com.example.ui.components.LiveRoundsTable
import com.example.ui.components.MultiplierAnalysisChart
import com.example.ui.components.OffsetControls
import com.example.ui.components.QuickSettingsPanel
import com.example.ui.components.RecentMultiplierGrid
import com.example.ui.components.ResultRingDisplay
import com.example.ui.components.RoundDetailDialog
import com.example.ui.components.SimulationPanel
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.NeonYellowRing
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun MainScreen(
    viewModel: AviatorViewModel,
    modifier: Modifier = Modifier
) {
    val rounds by viewModel.rounds.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val predictionAnalysis by viewModel.predictionAnalysis.collectAsStateWithLifecycle()
    val simulationSummary by viewModel.simulationSummary.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val isStreaming by viewModel.isDemoStreaming.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val selectedRound by viewModel.selectedRoundForDetails.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterOutcome by viewModel.filterOutcome.collectAsStateWithLifecycle()
    val liveFeedState by viewModel.liveFeedState.collectAsStateWithLifecycle()

    var activeNavIndex by remember { mutableIntStateOf(0) }
    var showImportExportDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val latestRound = remember(rounds) { rounds.firstOrNull() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppHeader(
                dataMode = settings.dataMode,
                isStreaming = isStreaming,
                isLiveWsConnected = liveFeedState.isConnected,
                latencyMs = liveFeedState.latencyMs,
                currentTheme = settings.themeMode,
                onCycleTheme = { viewModel.cycleTheme() },
                onToggleStreaming = { viewModel.toggleDemoStream() },
                onOpenImportExport = { showImportExportDialog = true },
                onOpenSettings = { activeNavIndex = 5 },
                onOpenLiveFeed = { activeNavIndex = 1 }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        // Box container enables the Floating Overlay Bottom Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (activeNavIndex) {
                    0 -> {
                        // 0: TERMINAL DASHBOARD
                        item {
                            // Offset Controls at top of prediction area
                            OffsetControls(
                                currentOffsetSeconds = settings.dataOffsetSeconds,
                                onAdjustOffset = { delta -> viewModel.adjustOffset(delta) },
                                onResetOffset = { viewModel.resetOffset() }
                            )
                        }

                        item {
                            // Central Glowing Result Ring Display
                            ResultRingDisplay(
                                latestRound = latestRound,
                                analysisWindow = settings.analysisWindow,
                                predictionAnalysis = predictionAnalysis,
                                riskProfile = settings.riskProfile,
                                isAnalyzing = isAnalyzing
                            )
                        }

                        item {
                            // Prominent RUN ALGORITHM & STOP buttons
                            ActionButtons(
                                isAnalyzing = isAnalyzing,
                                onRunAlgorithm = { viewModel.runAlgorithm() },
                                onStop = { viewModel.stopActiveProcessing() }
                            )
                        }

                        item {
                            // Live Status Banner
                            StatusBanner(
                                statusMessage = statusMessage,
                                isAnalyzing = isAnalyzing,
                                isLiveWs = liveFeedState.isConnected
                            )
                        }

                        item {
                            // Live WebSocket Quick Status Pill
                            LiveFeedQuickBar(
                                isConnected = liveFeedState.isConnected,
                                latencyMs = liveFeedState.latencyMs,
                                packets = liveFeedState.messagesReceived,
                                onOpenLiveConsole = { activeNavIndex = 1 }
                            )
                        }

                        item {
                            // Recent Crash Multipliers Grid
                            RecentMultiplierGrid(
                                rounds = rounds,
                                onSelectRound = { viewModel.selectRoundForDetails(it) }
                            )
                        }

                        item {
                            // Trajectory & Histogram Chart Preview
                            MultiplierAnalysisChart(
                                rounds = rounds,
                                targetMultiplier = settings.collectMultiplier
                            )
                        }
                    }

                    1 -> {
                        // 1: DEDICATED LIVE MODE & WEBSOCKET FEED CONSOLE
                        item {
                            LiveFeedConsolePanel(
                                feedState = liveFeedState,
                                onConnect = { url -> viewModel.connectLiveFeed(url) },
                                onDisconnect = { viewModel.disconnectLiveFeed() },
                                onTriggerTestRound = {
                                    val roundNum = 99200 + Random.nextInt(1000)
                                    val simulatedMult = String.format(java.util.Locale.US, "%.2f", 1.05 + Random.nextDouble() * 3.5).toDouble()
                                    viewModel.repository.liveFeedService.injectTestRound(
                                        HistoricalRound(
                                            roundId = "#LIVE-$roundNum",
                                            crashMultiplier = simulatedMult,
                                            timestamp = System.currentTimeMillis(),
                                            status = RoundStatus.FINAL,
                                            source = RoundSource.VERIFIED,
                                            receivedAt = System.currentTimeMillis()
                                        )
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Injected test telemetry packet: #LIVE-$roundNum @ ${simulatedMult}x")
                                    }
                                }
                            )
                        }

                        item {
                            // Live Rounds Table
                            LiveRoundsTable(
                                rounds = rounds,
                                betAmount = settings.betAmount,
                                collectMultiplier = settings.collectMultiplier,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                filterOutcome = filterOutcome,
                                onFilterOutcomeChange = { viewModel.setFilterOutcome(it) },
                                onSelectRound = { viewModel.selectRoundForDetails(it) }
                            )
                        }
                    }

                    2 -> {
                        // 2: LIVE ROUNDS TABLE
                        item {
                            LiveRoundsTable(
                                rounds = rounds,
                                betAmount = settings.betAmount,
                                collectMultiplier = settings.collectMultiplier,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                filterOutcome = filterOutcome,
                                onFilterOutcomeChange = { viewModel.setFilterOutcome(it) },
                                onSelectRound = { viewModel.selectRoundForDetails(it) }
                            )
                        }
                    }

                    3 -> {
                        // 3: ANALYTICS & CHARTS
                        item {
                            MultiplierAnalysisChart(
                                rounds = rounds,
                                targetMultiplier = settings.collectMultiplier
                            )
                        }

                        item {
                            AIInsightsPanel(analysis = predictionAnalysis)
                        }
                    }

                    4 -> {
                        // 4: STRATEGY SIMULATOR
                        item {
                            SimulationPanel(
                                summary = simulationSummary,
                                stake = settings.betAmount,
                                targetMultiplier = settings.collectMultiplier,
                                onRerunSimulation = { viewModel.runSimulation() },
                                onExportCsv = {
                                    simulationSummary?.let { sum ->
                                        val csv = CsvExporter.exportSimulationToCsv(sum.roundDetails)
                                        clipboardManager.setText(AnnotatedString(csv))
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Simulation CSV copied to clipboard!")
                                        }
                                    }
                                }
                            )
                        }
                    }

                    5 -> {
                        // 5: QUICK SETTINGS & DATA INGESTION
                        item {
                            QuickSettingsPanel(
                                settings = settings,
                                onSaveSettings = { updated ->
                                    viewModel.saveSettings(updated)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Settings applied successfully.")
                                    }
                                }
                            )
                        }

                        item {
                            LiveFeedConsolePanel(
                                feedState = liveFeedState,
                                onConnect = { url -> viewModel.connectLiveFeed(url) },
                                onDisconnect = { viewModel.disconnectLiveFeed() },
                                onTriggerTestRound = {
                                    val roundNum = 99300 + Random.nextInt(500)
                                    val simulatedMult = String.format(java.util.Locale.US, "%.2f", 1.10 + Random.nextDouble() * 2.8).toDouble()
                                    viewModel.repository.liveFeedService.injectTestRound(
                                        HistoricalRound(
                                            roundId = "#LIVE-$roundNum",
                                            crashMultiplier = simulatedMult,
                                            timestamp = System.currentTimeMillis(),
                                            status = RoundStatus.FINAL,
                                            source = RoundSource.VERIFIED,
                                            receivedAt = System.currentTimeMillis()
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                // Responsible gaming footer
                item {
                    ResponsibleGamingFooter()
                }
            }

            // FLOATING BOTTOM NAVIGATION OVERLAY
            FloatingBottomNav(
                selectedIndex = activeNavIndex,
                onSelectIndex = { activeNavIndex = it },
                isLiveWsActive = liveFeedState.isConnected,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // Dialogs
    if (showImportExportDialog) {
        ImportExportDialog(
            rounds = rounds,
            onDismiss = { showImportExportDialog = false },
            onImportCsv = { viewModel.importCsv(it) },
            onImportJson = { viewModel.importJson(it) },
            onResetToDemo = { viewModel.resetToDemoData() },
            onClearAll = { viewModel.clearAllData() }
        )
    }

    selectedRound?.let { round ->
        RoundDetailDialog(
            round = round,
            betAmount = settings.betAmount,
            collectMultiplier = settings.collectMultiplier,
            onDismiss = { viewModel.selectRoundForDetails(null) }
        )
    }
}

@Composable
private fun LiveFeedQuickBar(
    isConnected: Boolean,
    latencyMs: Long,
    packets: Long,
    onOpenLiveConsole: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF091322))
            .border(0.8.dp, if (isConnected) WinGreen.copy(alpha = 0.5f) else CyanBorder, RoundedCornerShape(10.dp))
            .clickable { onOpenLiveConsole() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = null,
                tint = if (isConnected) WinGreen else CyanAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) "LIVE FEED: CONNECTED (${latencyMs}ms)" else "LIVE MODE: TAP TO CONFIGURE WS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (isConnected) WinGreen else CyanPrimary
                )
            )
        }

        Text(
            text = if (isConnected) "$packets PACKETS" else "OFFLINE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                color = if (isConnected) TextPrimary else TextMuted
            )
        )
    }
}

@Composable
private fun StatusBanner(statusMessage: String, isAnalyzing: Boolean, isLiveWs: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0B1424))
            .border(0.8.dp, if (isAnalyzing) CyanPrimary else if (isLiveWs) WinGreen.copy(alpha = 0.5f) else SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isAnalyzing) CyanPrimary else if (isLiveWs) WinGreen else NeonYellowRing)
            )
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    color = if (isAnalyzing) CyanPrimary else if (isLiveWs) WinGreen else TextSecondary
                )
            )
        }
    }
}

@Composable
private fun ResponsibleGamingFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DISCLAIMER & RESPONSIBLE GAMING",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Aviator Predictor AI Analyst provides empirical mathematical observations on historical datasets. In cryptographic crash games, individual round outcomes are generated by independent seeds. Past performance does not guarantee future results. Never risk capital you cannot afford to lose.",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 9.5.sp,
                color = TextMuted,
                lineHeight = 13.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
