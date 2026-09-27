package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScreenerBlock
import com.example.data.model.StockPick
import com.example.data.repository.ChartinkRepository
import com.example.ui.components.TrendScoreBadge
import com.example.ui.components.formatPrice
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

enum class ScreenerSort(val title: String) {
    TREND_SCORE("Highest Trend Score"),
    CHANGE_DESC("Highest % Gain"),
    CHANGE_ASC("Pullback / Dips"),
    TURNOVER_DESC("Highest Turnover"),
    PRICE_DESC("Price: High to Low"),
    PRICE_ASC("Price: Low to High")
}

@Composable
fun ScreenerScreen(
    stocks: List<StockPick>,
    currentBlock: ScreenerBlock,
    isLoading: Boolean,
    watchlistSymbols: Set<String>,
    onBlockSelected: (ScreenerBlock) -> Unit,
    onRefresh: () -> Unit,
    onWatchlistToggle: (StockPick) -> Unit,
    onStockSelect: (StockPick) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf(ScreenerSort.TREND_SCORE) }
    var priceFilter by remember { mutableStateOf<String?>(null) } // null = All, "<200", "200-1000", "1000-5000", ">5000"
    var showSortMenu by remember { mutableStateOf(false) }

    val filteredAndSorted = remember(stocks, searchQuery, selectedSort, priceFilter) {
        stocks
            .filter { stock ->
                if (searchQuery.isBlank()) true
                else stock.symbol.contains(searchQuery.trim(), ignoreCase = true) ||
                        stock.companyName.contains(searchQuery.trim(), ignoreCase = true)
            }
            .filter { stock ->
                when (priceFilter) {
                    "<200" -> stock.closePrice < 200.0
                    "200-1000" -> stock.closePrice in 200.0..1000.0
                    "1000-5000" -> stock.closePrice in 1000.0..5000.0
                    ">5000" -> stock.closePrice > 5000.0
                    else -> true
                }
            }
            .let { list ->
                when (selectedSort) {
                    ScreenerSort.TREND_SCORE -> list.sortedByDescending { it.trendScore }
                    ScreenerSort.CHANGE_DESC -> list.sortedByDescending { it.changePercent }
                    ScreenerSort.CHANGE_ASC -> list.sortedBy { it.changePercent }
                    ScreenerSort.TURNOVER_DESC -> list.sortedByDescending { it.turnoverCr }
                    ScreenerSort.PRICE_DESC -> list.sortedByDescending { it.closePrice }
                    ScreenerSort.PRICE_ASC -> list.sortedBy { it.closePrice }
                }
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("screener_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header & Link banner
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chartink Screener Feed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            IconButton(
                                onClick = onRefresh,
                                enabled = !isLoading,
                                modifier = Modifier.size(32.dp).testTag("refresh_screener_btn")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sync",
                                        tint = EmeraldLight
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Momentum + 52-Week High Trend Template",
                            fontSize = 12.sp,
                            color = EmeraldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Filter: 1-Month (>25%) OR 1-Quarter (≥35%) OR Quality near 52W High with Price > 50 & 200 DMA",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ChartinkRepository.SCREENER_URL)))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = CyanAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Open on Chartink.com",
                                    fontSize = 11.sp,
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Screener Strategy Sub-Blocks TabRow
            item {
                Column {
                    Text(
                        text = "Strategy Block Filter",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScreenerBlock.values().forEach { block ->
                            FilterChip(
                                selected = currentBlock == block,
                                onClick = { onBlockSelected(block) },
                                label = { Text(block.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = EmeraldLight
                                ),
                                modifier = Modifier.testTag("block_${block.name}")
                            )
                        }
                    }
                }
            }

            // Search Bar & Sort
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by symbol (e.g. TRENT, BEL)...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("screener_search_input")
                    )

                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("screener_sort_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = CyanAccent
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            ScreenerSort.values().forEach { sort ->
                                DropdownMenuItem(
                                    text = { Text(sort.title) },
                                    onClick = {
                                        selectedSort = sort
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Price quick filters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = priceFilter == null,
                        onClick = { priceFilter = null },
                        label = { Text("All Prices", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = priceFilter == "<200",
                        onClick = { priceFilter = if (priceFilter == "<200") null else "<200" },
                        label = { Text("< ₹200", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = priceFilter == "200-1000",
                        onClick = { priceFilter = if (priceFilter == "200-1000") null else "200-1000" },
                        label = { Text("₹200 - ₹1K", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = priceFilter == "1000-5000",
                        onClick = { priceFilter = if (priceFilter == "1000-5000") null else "1000-5000" },
                        label = { Text("₹1K - ₹5K", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = priceFilter == ">5000",
                        onClick = { priceFilter = if (priceFilter == ">5000") null else ">5000" },
                        label = { Text("> ₹5,000", fontSize = 11.sp) }
                    )
                }
            }

            // Results count & sort indicator
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered Results (${filteredAndSorted.size} stocks)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedSort.title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stock Items List
            if (filteredAndSorted.isEmpty() && !isLoading) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No stocks match '${searchQuery}' with the selected filters.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(filteredAndSorted, key = { _, stock -> stock.symbol }) { index, stock ->
                    ScreenerStockListItem(
                        stock = stock,
                        rank = index + 1,
                        isInWatchlist = watchlistSymbols.contains(stock.symbol),
                        onWatchlistToggle = { onWatchlistToggle(stock) },
                        onClick = { onStockSelect(stock) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ScreenerStockListItem(
    stock: StockPick,
    rank: Int,
    isInWatchlist: Boolean,
    onWatchlistToggle: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("screener_item_${stock.symbol}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank pill
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$rank",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Symbol & Name & Turnover
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TrendScoreBadge(score = stock.trendScore)
                }
                Text(
                    text = stock.companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "Vol: ${String.format("%,d", stock.volume)} • ₹${String.format("%.1f", stock.turnoverCr)} Cr",
                    fontSize = 10.sp,
                    color = CyanAccent
                )
            }

            // Price & % Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${formatPrice(stock.closePrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val isPos = stock.changePercent >= 0
                Text(
                    text = "${if (isPos) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                    color = if (isPos) BullGreen else BearRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Star Watchlist button
            IconButton(
                onClick = onWatchlistToggle,
                modifier = Modifier.size(32.dp).testTag("screener_star_${stock.symbol}")
            ) {
                Icon(
                    imageVector = if (isInWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Watchlist",
                    tint = if (isInWatchlist) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
