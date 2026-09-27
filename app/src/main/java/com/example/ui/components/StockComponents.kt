package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PickCategory
import com.example.data.model.StockPick
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import kotlin.math.floor
import kotlin.math.max

@Composable
fun TrendScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when {
        score >= 85 -> Pair(BullGreen.copy(alpha = 0.15f), BullGreen)
        score >= 70 -> Pair(CyanAccent.copy(alpha = 0.15f), CyanAccent)
        else -> Pair(AmberAccent.copy(alpha = 0.15f), AmberAccent)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Trend Score",
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = "Score $score",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun HeroPickCard(
    pick: StockPick,
    isInWatchlist: Boolean,
    onWatchlistToggle: () -> Unit,
    onClick: () -> Unit,
    onCalculatePosition: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(EmeraldPrimary, CyanAccent.copy(alpha = 0.6f))
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("hero_pick_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header badge & Watchlist star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Top Pick",
                            tint = AmberAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TOP PICK OF THE DAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldLight
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TrendScoreBadge(score = pick.trendScore)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onWatchlistToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("hero_watchlist_btn")
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Watchlist",
                            tint = if (isInWatchlist) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Symbol, Name & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pick.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = pick.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${formatPrice(pick.closePrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    val isPositive = pick.changePercent >= 0
                    Surface(
                        color = if (isPositive) BullGreen.copy(alpha = 0.15f) else BearRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.2f", pick.changePercent)}%",
                            color = if (isPositive) BullGreen else BearRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(12.dp))

            // Trade Setup Quick View: Entry, Stop Loss, Target 1, R:R
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TradeParamItem(
                    label = "Buy Zone",
                    value = "₹${formatPrice(pick.entryPriceMin)}-${formatPrice(pick.entryPriceMax)}",
                    accentColor = EmeraldLight
                )
                TradeParamItem(
                    label = "Stop Loss (-${String.format("%.1f", pick.maxRiskPercent)}%)",
                    value = "₹${formatPrice(pick.stopLoss)}",
                    accentColor = BearRed
                )
                TradeParamItem(
                    label = "Target (+15%)",
                    value = "₹${formatPrice(pick.target1)}",
                    accentColor = BullGreen
                )
                TradeParamItem(
                    label = "R:R",
                    value = "1:${String.format("%.1f", pick.riskRewardRatio)}",
                    accentColor = CyanAccent
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thesis snippet
            Text(
                text = pick.thesis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCalculatePosition,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("calc_position_hero_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Position Size", fontSize = 12.sp)
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("view_details_hero_btn")
                ) {
                    Text(text = "View Trade Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StockPickCard(
    pick: StockPick,
    isInWatchlist: Boolean,
    onWatchlistToggle: () -> Unit,
    onClick: () -> Unit,
    onCalculatePosition: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("pick_card_${pick.symbol}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Category badge & Watchlist toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (pick.pickCategory) {
                        PickCategory.HIGH_MOMENTUM -> BullGreen.copy(alpha = 0.12f)
                        PickCategory.LOW_RISK_PULLBACK -> CyanAccent.copy(alpha = 0.12f)
                        PickCategory.VOLUME_BREAKOUT -> AmberAccent.copy(alpha = 0.12f)
                        PickCategory.QUALITY_LEADER -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = pick.pickCategory.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (pick.pickCategory) {
                            PickCategory.HIGH_MOMENTUM -> BullGreen
                            PickCategory.LOW_RISK_PULLBACK -> CyanAccent
                            PickCategory.VOLUME_BREAKOUT -> AmberAccent
                            PickCategory.QUALITY_LEADER -> EmeraldLight
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TrendScoreBadge(score = pick.trendScore)
                    IconButton(
                        onClick = onWatchlistToggle,
                        modifier = Modifier.size(32.dp).testTag("star_${pick.symbol}")
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

            Spacer(modifier = Modifier.height(8.dp))

            // Symbol & Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pick.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pick.companyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${formatPrice(pick.closePrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val isPositive = pick.changePercent >= 0
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format("%.2f", pick.changePercent)}%",
                        color = if (isPositive) BullGreen else BearRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trade Setup Bar: SL | Target 1 | R:R
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SL: ₹${formatPrice(pick.stopLoss)} (-${String.format("%.1f", pick.maxRiskPercent)}%)",
                        fontSize = 11.sp,
                        color = BearRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "T1: ₹${formatPrice(pick.target1)} (+15%)",
                        fontSize = 11.sp,
                        color = BullGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "R:R 1:${String.format("%.1f", pick.riskRewardRatio)}",
                        fontSize = 11.sp,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Turnover: ₹${String.format("%.1f", pick.turnoverCr)} Cr",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = onCalculatePosition,
                        modifier = Modifier.height(32.dp).testTag("calc_${pick.symbol}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = CyanAccent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Size Calc", fontSize = 11.sp, color = CyanAccent)
                    }

                    TextButton(
                        onClick = onClick,
                        modifier = Modifier.height(32.dp).testTag("plan_${pick.symbol}")
                    ) {
                        Text(text = "Plan", fontSize = 11.sp, color = EmeraldLight)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = EmeraldLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TradeParamItem(
    label: String,
    value: String,
    accentColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}

@Composable
fun PositionSizeCalculatorDialog(
    pick: StockPick,
    onDismiss: () -> Unit
) {
    var capitalInput by remember { mutableStateOf("100000") }
    var riskPercentInput by remember { mutableStateOf("2.0") }

    val capital = capitalInput.toDoubleOrNull() ?: 100000.0
    val riskPercent = riskPercentInput.toDoubleOrNull() ?: 2.0

    val maxRiskAmount = capital * (riskPercent / 100.0)
    val riskPerShare = max(1.0, pick.closePrice - pick.stopLoss)
    val sharesQuantity = floor(maxRiskAmount / riskPerShare).toInt()
    val totalInvestment = sharesQuantity * pick.closePrice
    val potentialProfitT1 = sharesQuantity * (pick.target1 - pick.closePrice)
    val potentialProfitT2 = sharesQuantity * (pick.target2 - pick.closePrice)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Position Size: ${pick.symbol}", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Calculate exact shares to buy based on Mark Minervini's max loss risk management rule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = capitalInput,
                        onValueChange = { capitalInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Portfolio Capital (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_capital")
                    )

                    OutlinedTextField(
                        value = riskPercentInput,
                        onValueChange = { riskPercentInput = it },
                        label = { Text("Risk % (e.g. 1-2%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(0.7f).testTag("input_risk_pct")
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CalcRow("Max Risk Allowed:", "₹${formatPrice(maxRiskAmount)}", AmberAccent)
                        CalcRow("Risk per Share (CMP - SL):", "₹${formatPrice(riskPerShare)}", BearRed)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        CalcRow("Recommended Shares:", "$sharesQuantity Qty", BullGreen, isBold = true)
                        CalcRow("Total Position Value:", "₹${formatPrice(totalInvestment)}", MaterialTheme.colorScheme.onSurface)
                        CalcRow("Potential Gain at Target 1:", "+₹${formatPrice(potentialProfitT1)}", BullGreen)
                        CalcRow("Potential Gain at Target 2:", "+₹${formatPrice(potentialProfitT2)}", CyanAccent)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun CalcRow(
    label: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
            color = valueColor
        )
    }
}

fun formatPrice(value: Double): String {
    return if (value >= 1000.0) {
        String.format("%,.1f", value)
    } else {
        String.format("%.2f", value)
    }
}
