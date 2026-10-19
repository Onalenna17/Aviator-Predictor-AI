package com.example.core

import com.example.model.HistoricalRound
import com.example.model.SimulatedRound
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    /**
     * Formats historical rounds into a standard CSV string.
     */
    fun exportRoundsToCsv(rounds: List<HistoricalRound>): String {
        val sb = StringBuilder()
        sb.append("RoundId,CrashMultiplier,Timestamp,DateUtc,Status,Source\n")
        for (r in rounds) {
            val dateStr = dateFormat.format(Date(r.timestamp))
            sb.append("${r.roundId},${r.crashMultiplier},${r.timestamp},\"$dateStr\",${r.status.name},${r.source.name}\n")
        }
        return sb.toString()
    }

    /**
     * Formats simulation backtest results into CSV string.
     */
    fun exportSimulationToCsv(results: List<SimulatedRound>): String {
        val sb = StringBuilder()
        sb.append("RoundId,CrashMultiplier,Stake,TargetMultiplier,Outcome,NetResult,DateUtc\n")
        for (s in results) {
            val dateStr = dateFormat.format(Date(s.timestamp))
            val outcome = if (s.isWin) "WIN" else "LOSS"
            val net = String.format(Locale.US, "%.2f", s.netProfit)
            sb.append("${s.roundId},${s.crashMultiplier},${s.stake},${s.targetMultiplier},$outcome,$net,\"$dateStr\"\n")
        }
        return sb.toString()
    }
}
