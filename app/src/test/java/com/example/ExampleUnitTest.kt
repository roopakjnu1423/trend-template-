package com.example

import com.example.data.model.ChartinkStockItem
import com.example.data.model.PickCategory
import com.example.data.model.ScreenerBlock
import com.example.engine.StockPickerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStockPickerEngine_identifiesTopPickAndCalculatesTradePlan() {
        val sampleItems = listOf(
            ChartinkStockItem(1, "TRENT", "Trent Limited", "500251", 7240.0, 3.82, 1482000),
            ChartinkStockItem(2, "BEL", "Bharat Electronics Ltd", "500049", 312.45, 2.75, 18450000),
            ChartinkStockItem(3, "BOSCHLTD", "Bosch Limited", "500530", 48250.0, 1.05, 18852)
        )

        val rankedPicks = StockPickerEngine.processAndRankStocks(sampleItems, ScreenerBlock.ALL)

        assertEquals(3, rankedPicks.size)

        val topPick = rankedPicks.first()
        assertEquals(PickCategory.TOP_PICK, topPick.pickCategory)
        assertTrue(topPick.trendScore >= 70)
        assertTrue(topPick.stopLoss < topPick.closePrice)
        assertTrue(topPick.target1 > topPick.closePrice)
        assertTrue(topPick.target2 > topPick.target1)
        assertTrue(topPick.riskRewardRatio > 1.5)
        assertTrue(topPick.checklist.isNotEmpty())
    }

    @Test
    fun testStockPickerEngine_emptyInputHandledSafely() {
        val emptyPicks = StockPickerEngine.processAndRankStocks(emptyList())
        assertTrue(emptyPicks.isEmpty())
    }
}
