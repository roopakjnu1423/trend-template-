package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist_stocks ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist_stocks WHERE symbol = :symbol LIMIT 1")
    suspend fun getBySymbol(symbol: String): WatchlistEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_stocks WHERE symbol = :symbol)")
    fun isInWatchlist(symbol: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stock: WatchlistEntity)

    @Update
    suspend fun update(stock: WatchlistEntity)

    @Query("DELETE FROM watchlist_stocks WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)

    @Query("DELETE FROM watchlist_stocks")
    suspend fun clearAll()
}
