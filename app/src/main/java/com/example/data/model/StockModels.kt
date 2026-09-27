package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChartinkRawResponse(
    @Json(name = "draw") val draw: Int? = null,
    @Json(name = "recordsTotal") val recordsTotal: Int? = null,
    @Json(name = "recordsFiltered") val recordsFiltered: Int? = null,
    @Json(name = "data") val data: List<ChartinkStockItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChartinkStockItem(
    @Json(name = "sr") val sr: Int = 0,
    @Json(name = "nsecode") val nsecode: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "bsecode") val bsecode: String? = null,
    @Json(name = "close") val close: Double = 0.0,
    @Json(name = "per_chg") val per_chg: Double = 0.0,
    @Json(name = "volume") val volume: Long = 0L
)

enum class ScreenerBlock(val title: String, val shortDesc: String, val days: Int) {
    ALL(
        title = "Full Trend Template",
        shortDesc = "All 3 Momentum & 52-Week High blocks combined",
        days = 252
    ),
    ONE_MONTH_MOMENTUM(
        title = "1-Month Momentum",
        shortDesc = ">25% gain in 22 sessions, MCap >500Cr, Vol >1Cr, >200 DMA",
        days = 22
    ),
    ONE_QUARTER_MOMENTUM(
        title = "1-Quarter Momentum",
        shortDesc = "≥35% gain in 66 sessions, Price ≥₹20, MCap >500Cr, >200 DMA",
        days = 66
    ),
    QUALITY_NEAR_52W_HIGH(
        title = "Quality Near 52W High",
        shortDesc = "Within 25% of 52W High, MCap ≥1000Cr, >50 & >200 DMA",
        days = 252
    )
}

enum class PickCategory(val displayName: String, val tag: String) {
    TOP_PICK("Pick of the Day", "Highest Conviction"),
    HIGH_MOMENTUM("Momentum Leader", "Strong Velocity"),
    LOW_RISK_PULLBACK("Low Risk Pullback", "Tight Stop Loss"),
    VOLUME_BREAKOUT("Volume Surge", "Institutional Accumulation"),
    QUALITY_LEADER("Quality Compounder", "Large/Mid Cap Leader"),
    TREND_RUNNER("Trend Runner", "Stage 2 Uptrend")
}

enum class PickVerdict(val label: String, val colorCode: Long) {
    STRONG_BUY("Strong Momentum Buy", 0xFF10B981),
    ACCUMULATE_PULLBACK("Accumulate on Dip", 0xFF06B6D4),
    BREAKOUT_WATCH("Breakout Confirmation", 0xFFF59E0B)
}

data class Stage2CheckItem(
    val title: String,
    val passed: Boolean,
    val details: String
)

data class StockPick(
    val sr: Int,
    val symbol: String,
    val companyName: String,
    val bseCode: String?,
    val closePrice: Double,
    val changePercent: Double,
    val volume: Long,
    val turnoverCr: Double,
    val trendScore: Int, // 0 - 100
    val pickCategory: PickCategory,
    val verdict: PickVerdict,
    val confidence: Int, // percentage 75 - 99%
    val qualifyingBlocks: List<ScreenerBlock>,
    val entryPriceMin: Double,
    val entryPriceMax: Double,
    val stopLoss: Double,
    val target1: Double,
    val target2: Double,
    val riskRewardRatio: Double,
    val maxRiskPercent: Double,
    val estimated52wHigh: Double,
    val distanceTo52wHighPercent: Double,
    val dma50Estimate: Double,
    val dma200Estimate: Double,
    val checklist: List<Stage2CheckItem>,
    val thesis: String
)
