package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_stocks")
data class WatchlistEntity(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val addedPrice: Double,
    val currentPrice: Double,
    val changePercent: Double,
    val stopLoss: Double,
    val targetPrice: Double,
    val trendScore: Int,
    val notes: String = "",
    val category: String = "Watchlist",
    val addedAt: Long = System.currentTimeMillis()
)
