package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.local.WatchlistEntity
import com.example.ui.components.TrendScoreBadge
import com.example.ui.components.formatPrice
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun WatchlistScreen(
    watchlist: List<WatchlistEntity>,
    onRemoveStock: (String) -> Unit,
    onUpdateNotes: (String, String) -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editNotesTarget by remember { mutableStateOf<WatchlistEntity?>(null) }
    var notesText by remember { mutableStateOf("") }

    if (editNotesTarget != null) {
        AlertDialog(
            onDismissRequest = { editNotesTarget = null },
            title = { Text("Edit Notes: ${editNotesTarget?.symbol}") },
            text = {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Trading Notes") },
                    placeholder = { Text("Entry target, stop loss, quantity...") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_notes_field")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        editNotesTarget?.let { onUpdateNotes(it.symbol, notesText) }
                        editNotesTarget = null
                    }
                ) {
                    Text("Save Notes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editNotesTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (watchlist.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Watchlist is Empty",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Star stocks from 'Stock Picks' or 'Live Screener' to track them here with your entry price, stop-loss, and personal trading notes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("watchlist_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary card
                item {
                    val gainersCount = watchlist.count { it.changePercent >= 0 }
                    val avgScore = watchlist.map { it.trendScore }.average().toInt()

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Watchlist Portfolio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${watchlist.size} saved stocks ($gainersCount advancing today)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TrendScoreBadge(score = avgScore)
                        }
                    }
                }

                items(watchlist, key = { it.symbol }) { item ->
                    WatchlistItemCard(
                        item = item,
                        onClick = { onStockClick(item.symbol) },
                        onEditNotes = {
                            notesText = item.notes
                            editNotesTarget = item
                        },
                        onDelete = { onRemoveStock(item.symbol) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun WatchlistItemCard(
    item: WatchlistEntity,
    onClick: () -> Unit,
    onEditNotes: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("watchlist_card_${item.symbol}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Symbol, Trend Score, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TrendScoreBadge(score = item.trendScore)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_${item.symbol}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = BearRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = item.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price & Targets row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CMP: ₹${formatPrice(item.currentPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val isPos = item.changePercent >= 0
                    Text(
                        text = "${if (isPos) "+" else ""}${String.format("%.2f", item.changePercent)}%",
                        color = if (isPos) BullGreen else BearRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SL: ₹${formatPrice(item.stopLoss)}",
                        fontSize = 11.sp,
                        color = BearRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Target: ₹${formatPrice(item.targetPrice)}",
                        fontSize = 11.sp,
                        color = BullGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Notes Section
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.notes.isNotBlank()) "Note: ${item.notes}" else "No notes added yet",
                    fontSize = 11.sp,
                    color = if (item.notes.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                IconButton(
                    onClick = onEditNotes,
                    modifier = Modifier.size(28.dp).testTag("edit_note_${item.symbol}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit note",
                        tint = CyanAccent,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
