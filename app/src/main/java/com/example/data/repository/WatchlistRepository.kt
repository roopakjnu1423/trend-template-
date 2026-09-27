package com.example.data.repository

import com.example.data.local.WatchlistDao
import com.example.data.local.WatchlistEntity
import kotlinx.coroutines.flow.Flow

class WatchlistRepository(private val dao: WatchlistDao) {

    val allWatchlist: Flow<List<WatchlistEntity>> = dao.getAllWatchlist()

    fun isInWatchlist(symbol: String): Flow<Boolean> = dao.isInWatchlist(symbol)

    suspend fun addToWatchlist(
        symbol: String,
        companyName: String,
        price: Double,
        changePercent: Double,
        stopLoss: Double,
        targetPrice: Double,
        trendScore: Int,
        notes: String = ""
    ) {
        val entity = WatchlistEntity(
            symbol = symbol,
            companyName = companyName,
            addedPrice = price,
            currentPrice = price,
            changePercent = changePercent,
            stopLoss = stopLoss,
            targetPrice = targetPrice,
            trendScore = trendScore,
            notes = notes
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun removeFromWatchlist(symbol: String) {
        dao.deleteBySymbol(symbol)
    }

    suspend fun updateNotes(symbol: String, notes: String) {
        val existing = dao.getBySymbol(symbol)
        if (existing != null) {
            dao.update(existing.copy(notes = notes))
        }
    }
}
