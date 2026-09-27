package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ScreenerScreen
import com.example.ui.screens.StockDetailDialog
import com.example.ui.screens.StockPicksScreen
import com.example.ui.screens.TrendTemplateGuideScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    PICKS("Picks", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_picks"),
    SCREENER("Screener", Icons.Filled.ViewList, Icons.Outlined.ViewList, "tab_screener"),
    WATCHLIST("Watchlist", Icons.Filled.Star, Icons.Outlined.StarBorder, "tab_watchlist"),
    GUIDE("Strategy", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_guide")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentTab = remember { mutableStateOf(AppTab.PICKS) }
                val snackbarHostState = remember { SnackbarHostState() }

                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                val currentBlock by viewModel.currentBlock.collectAsStateWithLifecycle()
                val scannedStocks by viewModel.scannedStocks.collectAsStateWithLifecycle()
                val stockPicks by viewModel.stockPicks.collectAsStateWithLifecycle()
                val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
                val selectedStockDetail by viewModel.selectedStockDetail.collectAsStateWithLifecycle()
                val lastSyncedTime by viewModel.lastSyncedTime.collectAsStateWithLifecycle()
                val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

                val watchlistSymbols = remember(watchlist) {
                    watchlist.map { it.symbol }.toSet()
                }

                // Handle back press: if not on Picks tab, return to Picks
                BackHandler(enabled = currentTab.value != AppTab.PICKS) {
                    currentTab.value = AppTab.PICKS
                }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearError()
                    }
                }

                // Detail Dialog
                selectedStockDetail?.let { pick ->
                    val isFav = watchlistSymbols.contains(pick.symbol)
                    StockDetailDialog(
                        pick = pick,
                        isInWatchlist = isFav,
                        onWatchlistToggle = { notes ->
                            viewModel.toggleWatchlist(pick, notes)
                        },
                        onDismiss = { viewModel.selectStock(null) }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Trend Template",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = EmeraldPrimary.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "CHARTINK",
                                                color = EmeraldLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(BullGreen, CircleShape)
                                        )
                                        Text(
                                            text = "52-Week High Momentum • Synced $lastSyncedTime",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.loadScreener() },
                                    enabled = !isLoading,
                                    modifier = Modifier.testTag("appbar_sync_btn")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = EmeraldPrimary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Sync",
                                            tint = EmeraldLight
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            AppTab.values().forEach { tab ->
                                val selected = currentTab.value == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab.value = tab },
                                    icon = {
                                        if (tab == AppTab.WATCHLIST && watchlist.isNotEmpty()) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(containerColor = AmberAccent) {
                                                        Text("${watchlist.size}", fontSize = 10.sp)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.title
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldLight,
                                        selectedTextColor = EmeraldLight,
                                        indicatorColor = EmeraldPrimary.copy(alpha = 0.2f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentTab.value,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { targetTab ->
                        when (targetTab) {
                            AppTab.PICKS -> {
                                StockPicksScreen(
                                    picks = stockPicks,
                                    totalScannedCount = scannedStocks.size,
                                    isLoading = isLoading,
                                    watchlistSymbols = watchlistSymbols,
                                    onRefresh = { viewModel.loadScreener() },
                                    onWatchlistToggle = { pick -> viewModel.toggleWatchlist(pick) },
                                    onStockSelect = { pick -> viewModel.selectStock(pick) }
                                )
                            }
                            AppTab.SCREENER -> {
                                ScreenerScreen(
                                    stocks = scannedStocks,
                                    currentBlock = currentBlock,
                                    isLoading = isLoading,
                                    watchlistSymbols = watchlistSymbols,
                                    onBlockSelected = { block -> viewModel.loadScreener(block) },
                                    onRefresh = { viewModel.loadScreener(currentBlock) },
                                    onWatchlistToggle = { stock -> viewModel.toggleWatchlist(stock) },
                                    onStockSelect = { stock -> viewModel.selectStock(stock) }
                                )
                            }
                            AppTab.WATCHLIST -> {
                                WatchlistScreen(
                                    watchlist = watchlist,
                                    onRemoveStock = { symbol -> viewModel.removeFromWatchlist(symbol) },
                                    onUpdateNotes = { sym, notes -> viewModel.updateWatchlistNotes(sym, notes) },
                                    onStockClick = { sym -> viewModel.selectStockBySymbol(sym) }
                                )
                            }
                            AppTab.GUIDE -> {
                                TrendTemplateGuideScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
