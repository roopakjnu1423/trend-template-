package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockPick
import com.example.ui.components.TrendScoreBadge
import com.example.ui.components.formatPrice
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun StockDetailDialog(
    pick: StockPick,
    isInWatchlist: Boolean,
    onWatchlistToggle: (notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var notesInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(vertical = 16.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = pick.symbol,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        TrendScoreBadge(score = pick.trendScore)
                    }
                    Text(
                        text = pick.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp).testTag("close_stock_detail")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Price & Intraday Stats
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
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
                                text = "Current Market Price",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${formatPrice(pick.closePrice)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val isPos = pick.changePercent >= 0
                            Surface(
                                color = if (isPos) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${if (isPos) "+" else ""}${String.format("%.2f", pick.changePercent)}%",
                                    color = if (isPos) BullGreen else BearRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Vol: ${String.format("%,d", pick.volume)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Actionable Trade Plan Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SWING TRADE SETUP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }

                        DetailMetricRow(
                            label = "Recommended Buy Range:",
                            value = "₹${formatPrice(pick.entryPriceMin)} - ₹${formatPrice(pick.entryPriceMax)}",
                            valueColor = EmeraldLight
                        )
                        DetailMetricRow(
                            label = "Stop Loss (Minervini Cutoff):",
                            value = "₹${formatPrice(pick.stopLoss)} (-${String.format("%.1f", pick.maxRiskPercent)}%)",
                            valueColor = BearRed
                        )
                        DetailMetricRow(
                            label = "Target 1 (1:2 Risk/Reward):",
                            value = "₹${formatPrice(pick.target1)} (+15.0%)",
                            valueColor = BullGreen
                        )
                        DetailMetricRow(
                            label = "Target 2 (Trend Runner):",
                            value = "₹${formatPrice(pick.target2)} (+30.0%)",
                            valueColor = CyanAccent
                        )
                        DetailMetricRow(
                            label = "Risk / Reward Ratio:",
                            value = "1 : ${String.format("%.1f", pick.riskRewardRatio)}",
                            valueColor = AmberAccent
                        )
                    }
                }

                // Analysis Thesis
                Column {
                    Text(
                        text = "Technical Setup Thesis",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = pick.thesis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }

                // Stage 2 Trend Template Criteria Checklist
                Column {
                    Text(
                        text = "Mark Minervini Stage 2 Checklist",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    pick.checklist.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (item.passed) BullGreen else BearRed,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.details,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Technical Estimates: 50 DMA, 200 DMA, 52W High
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Technical Level Estimates",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        DetailMetricRow(
                            label = "Estimated 52-Week High:",
                            value = "₹${formatPrice(pick.estimated52wHigh)} (${String.format("%.1f", pick.distanceTo52wHighPercent)}% away)",
                            valueColor = MaterialTheme.colorScheme.onSurface
                        )
                        DetailMetricRow(
                            label = "Estimated 50 DMA Support:",
                            value = "₹${formatPrice(pick.dma50Estimate)}",
                            valueColor = CyanAccent
                        )
                        DetailMetricRow(
                            label = "Estimated 200 DMA Support:",
                            value = "₹${formatPrice(pick.dma200Estimate)}",
                            valueColor = AmberAccent
                        )
                        DetailMetricRow(
                            label = "20-Day Avg Turnover:",
                            value = "₹${String.format("%.2f", pick.turnoverCr)} Cr",
                            valueColor = BullGreen
                        )
                    }
                }

                // Direct External Research Links
                Column {
                    Text(
                        text = "External Technical Charts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val chartinkUrl = "https://chartink.com/stocks/${pick.symbol.lowercase()}.html"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(chartinkUrl)))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Chartink", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val tvUrl = "https://in.tradingview.com/chart/?symbol=NSE%3A${pick.symbol}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tvUrl)))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "TradingView", fontSize = 11.sp)
                        }
                    }
                }

                // Add to Watchlist & Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Personal Trading Notes / Target") },
                    placeholder = { Text("e.g. Bought 25 shares @ ₹${formatPrice(pick.closePrice)}, SL ₹${formatPrice(pick.stopLoss)}") },
                    modifier = Modifier.fillMaxWidth().testTag("detail_notes_input"),
                    singleLine = false,
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onWatchlistToggle(notesInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInWatchlist) BearRed.copy(alpha = 0.8f) else EmeraldPrimary
                ),
                modifier = Modifier.testTag("detail_watchlist_action_btn")
            ) {
                Icon(
                    imageVector = if (isInWatchlist) Icons.Default.Close else Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isInWatchlist) "Remove from Watchlist" else "Save to Watchlist")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailMetricRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
