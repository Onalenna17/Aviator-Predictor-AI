package com.example

import com.example.model.HistoricalRound
import com.example.model.RoundSource
import com.example.service.LiveFeedService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveFeedServiceTest {

    @Test
    fun testLiveFeedInjectionAndState() {
        var receivedRound: HistoricalRound? = null
        val service = LiveFeedService { round ->
            receivedRound = round
        }

        val testRound = HistoricalRound(
            roundId = "#LIVE-99500",
            crashMultiplier = 3.45,
            source = RoundSource.VERIFIED
        )

        service.injectTestRound(testRound)

        assertNotNull(receivedRound)
        assertEquals("#LIVE-99500", receivedRound!!.roundId)
        assertEquals(3.45, receivedRound!!.crashMultiplier, 0.001)
        assertEquals(RoundSource.VERIFIED, receivedRound!!.source)

        val logs = service.feedState.value.eventLogs
        assertTrue(logs.isNotEmpty())
        assertTrue(logs.last().contains("#LIVE-99500"))
    }

    @Test
    fun testEndpointUrlConfiguration() {
        val service = LiveFeedService {}
        service.setEndpointUrl("wss://test.aviator.stream/feed")
        assertEquals("wss://test.aviator.stream/feed", service.feedState.value.endpointUrl)
    }
}
