package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PickCategory
import com.example.data.model.StockPick
import com.example.ui.components.HeroPickCard
import com.example.ui.components.PositionSizeCalculatorDialog
import com.example.ui.components.StockPickCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun StockPicksScreen(
    picks: List<StockPick>,
    totalScannedCount: Int,
    isLoading: Boolean,
    watchlistSymbols: Set<String>,
    onRefresh: () -> Unit,
    onWatchlistToggle: (StockPick) -> Unit,
    onStockSelect: (StockPick) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<PickCategory?>(null) }
    var calcStockPick by remember { mutableStateOf<StockPick?>(null) }

    val filteredPicks = remember(picks, selectedCategory) {
        if (selectedCategory == null) {
            picks
        } else {
            picks.filter { it.pickCategory == selectedCategory }
        }
    }

    val heroPick = picks.firstOrNull()

    // Position size calculator popup
    calcStockPick?.let { pick ->
        PositionSizeCalculatorDialog(
            pick = pick,
            onDismiss = { calcStockPick = null }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("stock_picks_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Stats & Live status
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "High-Conviction Picks",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Filtered from Chartink ($totalScannedCount stocks scanned)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("refresh_picks_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = EmeraldLight
                            )
                        }
                    }
                }
            }

            // Featured Hero Pick of the Day
            if (heroPick != null && selectedCategory == null) {
                item {
                    HeroPickCard(
                        pick = heroPick,
                        isInWatchlist = watchlistSymbols.contains(heroPick.symbol),
                        onWatchlistToggle = { onWatchlistToggle(heroPick) },
                        onClick = { onStockSelect(heroPick) },
                        onCalculatePosition = { calcStockPick = heroPick }
                    )
                }
            }

            // Strategy Category Filters
            item {
                Column {
                    Text(
                        text = "Pick Strategy Categories",
                        style = MaterialTheme.typography.labelMedium,
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
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All Picks (${picks.size})") },
                            leadingIcon = if (selectedCategory == null) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = EmeraldLight
                            ),
                            modifier = Modifier.testTag("filter_all_picks")
                        )

                        PickCategory.values().forEach { cat ->
                            val count = picks.count { it.pickCategory == cat }
                            if (count > 0) {
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = {
                                        selectedCategory = if (selectedCategory == cat) null else cat
                                    },
                                    label = { Text("${cat.displayName} ($count)") },
                                    leadingIcon = if (selectedCategory == cat) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                        selectedLabelColor = EmeraldLight
                                    ),
                                    modifier = Modifier.testTag("filter_${cat.name}")
                                )
                            }
                        }
                    }
                }
            }

            // Picks List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory != null) "${selectedCategory?.displayName} (${filteredPicks.size})" else "Ranked Momentum Candidates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Score & Trade Plan",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )
                }
            }

            // List of Stock Pick Cards
            if (filteredPicks.isEmpty() && !isLoading) {
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
                                text = "No stocks match this specific category right now.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredPicks, key = { it.symbol }) { pick ->
                    StockPickCard(
                        pick = pick,
                        isInWatchlist = watchlistSymbols.contains(pick.symbol),
                        onWatchlistToggle = { onWatchlistToggle(pick) },
                        onClick = { onStockSelect(pick) },
                        onCalculatePosition = { calcStockPick = pick }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
