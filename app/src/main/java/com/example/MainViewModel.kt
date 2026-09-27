package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.WatchlistEntity
import com.example.data.model.ScreenerBlock
import com.example.data.model.StockPick
import com.example.data.repository.ChartinkRepository
import com.example.data.repository.WatchlistRepository
import com.example.engine.StockPickerEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val chartinkRepo = ChartinkRepository()
    private val database = AppDatabase.getInstance(application)
    private val watchlistRepo = WatchlistRepository(database.watchlistDao())

    val watchlist: StateFlow<List<WatchlistEntity>> = watchlistRepo.allWatchlist
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentBlock = MutableStateFlow(ScreenerBlock.ALL)
    val currentBlock: StateFlow<ScreenerBlock> = _currentBlock.asStateFlow()

    private val _scannedStocks = MutableStateFlow<List<StockPick>>(emptyList())
    val scannedStocks: StateFlow<List<StockPick>> = _scannedStocks.asStateFlow()

    private val _stockPicks = MutableStateFlow<List<StockPick>>(emptyList())
    val stockPicks: StateFlow<List<StockPick>> = _stockPicks.asStateFlow()

    private val _selectedStockDetail = MutableStateFlow<StockPick?>(null)
    val selectedStockDetail: StateFlow<StockPick?> = _selectedStockDetail.asStateFlow()

    private val _lastSyncedTime = MutableStateFlow("Never")
    val lastSyncedTime: StateFlow<String> = _lastSyncedTime.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadScreener(ScreenerBlock.ALL)
    }

    fun loadScreener(block: ScreenerBlock = _currentBlock.value) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentBlock.value = block
            _errorMessage.value = null

            val result = chartinkRepo.fetchScreenerStocks(block)
            result.onSuccess { rawItems ->
                val processed = StockPickerEngine.processAndRankStocks(rawItems, block)
                _scannedStocks.value = processed

                // Top picks: take top ranked candidates with trendScore >= 75
                val highConvictionPicks = processed.filter { it.trendScore >= 75 }
                    .ifEmpty { processed.take(15) }
                _stockPicks.value = highConvictionPicks

                val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                _lastSyncedTime.value = timeFmt.format(Date())
            }.onFailure { err ->
                _errorMessage.value = err.message ?: "Failed to load stocks from Chartink"
            }

            _isLoading.value = false
        }
    }

    fun selectStock(stock: StockPick?) {
        _selectedStockDetail.value = stock
    }

    fun selectStockBySymbol(symbol: String) {
        val found = _scannedStocks.value.find { it.symbol.equals(symbol, ignoreCase = true) }
            ?: _stockPicks.value.find { it.symbol.equals(symbol, ignoreCase = true) }
        _selectedStockDetail.value = found
    }

    fun toggleWatchlist(pick: StockPick, customNotes: String = "") {
        viewModelScope.launch {
            val exists = watchlist.value.any { it.symbol.equals(pick.symbol, ignoreCase = true) }
            if (exists) {
                watchlistRepo.removeFromWatchlist(pick.symbol)
            } else {
                watchlistRepo.addToWatchlist(
                    symbol = pick.symbol,
                    companyName = pick.companyName,
                    price = pick.closePrice,
                    changePercent = pick.changePercent,
                    stopLoss = pick.stopLoss,
                    targetPrice = pick.target1,
                    trendScore = pick.trendScore,
                    notes = customNotes
                )
            }
        }
    }

    fun removeFromWatchlist(symbol: String) {
        viewModelScope.launch {
            watchlistRepo.removeFromWatchlist(symbol)
        }
    }

    fun updateWatchlistNotes(symbol: String, notes: String) {
        viewModelScope.launch {
            watchlistRepo.updateNotes(symbol, notes)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
