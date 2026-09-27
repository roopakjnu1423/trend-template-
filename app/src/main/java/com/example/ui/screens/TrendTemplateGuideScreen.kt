package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ChartinkRepository
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun TrendTemplateGuideScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize().testTag("guide_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Trend Template Strategy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Complete guide to the Chartink 'Momentum + 52-Week High Trend Template' screener and Mark Minervini's Stage 2 stock selection methodology.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 1: The 3 Screener Blocks
        item {
            StrategyBlockCard(
                icon = Icons.Default.Timeline,
                title = "Block 1: 1-Month Momentum (22 Days)",
                accentColor = EmeraldLight,
                points = listOf(
                    "Price increase > 25% over the past 22 trading sessions (~1 calendar month).",
                    "Market Capitalization > ₹500 Crores (eliminates microcaps).",
                    "20-day average turnover > ₹1 Crore (ensures high liquidity).",
                    "Price trading strictly above the 200-Day Moving Average."
                )
            )
        }

        item {
            StrategyBlockCard(
                icon = Icons.Default.TrendingUp,
                title = "Block 2: 1-Quarter Momentum (66 Days)",
                accentColor = CyanAccent,
                points = listOf(
                    "Price increase ≥ 35% over the past 66 trading sessions (~1 calendar quarter).",
                    "Market Capitalization > ₹500 Crores.",
                    "Minimum stock price of ₹20 (eliminates penny stocks).",
                    "20-day average turnover > ₹1 Crore.",
                    "Price trading above the 200-Day Moving Average."
                )
            )
        }

        item {
            StrategyBlockCard(
                icon = Icons.Default.AutoAwesome,
                title = "Block 3: Quality Near 52W High (252 Days)",
                accentColor = AmberAccent,
                points = listOf(
                    "Market Capitalization ≥ ₹1,000 Crores (established leaders).",
                    "Price within 25% of 52-Week High (Close > 0.75 * 52W High).",
                    "Price trading above both 50 DMA and 200 DMA.",
                    "Golden Cross alignment (50 DMA > 200 DMA) confirming Stage 2 trend.",
                    "20-day average turnover > ₹1 Crore."
                )
            )
        }

        // Section 2: How Stocks Are Picked
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "How The App Picks Winning Stocks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "While the Chartink screener filters ~400+ stocks that qualify the criteria, our Stock Picker Engine narrows them down to the top actionable setups:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    PickCriteriaItem(
                        title = "1. Trend Template Score (0-100)",
                        desc = "Combines Stage 2 moving average alignment, distance to 52-week high, and relative volume surges."
                    )
                    PickCriteriaItem(
                        title = "2. Pick of the Day Selection",
                        desc = "Identifies the #1 setup with ideal consolidation, institutional accumulation, and maximum risk-to-reward ratio."
                    )
                    PickCriteriaItem(
                        title = "3. Low Risk Pullback Setup",
                        desc = "Catches high-momentum stocks temporarily pulling back 1-2% to test 50 DMA support, offering minimal stop-loss risk."
                    )
                    PickCriteriaItem(
                        title = "4. Strict Risk Management",
                        desc = "Never risk more than 7% from entry price. Targets are mapped at 1:2 and 1:3 reward-to-risk ratios."
                    )
                }
            }
        }

        // Section 3: Risk Management Rules (Minervini)
        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Mark Minervini Golden Rules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Rule 1: Always use a stop loss. Never allow a loss to exceed 7-8% under any circumstance.\n• Rule 2: Never average down on a losing position.\n• Rule 3: Protect your profit — trail stop loss to breakeven once the stock gains +8%.\n• Rule 4: Use the Position Size Calculator to size shares based on a fixed 1-2% portfolio risk.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Section 4: Raw Chartink Scan Clause
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Raw Chartink Scan Clause",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Chartink Scan Clause", ChartinkRepository.FULL_SCAN_CLAUSE)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Scan clause copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp).testTag("copy_clause_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = ChartinkRepository.FULL_SCAN_CLAUSE,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StrategyBlockCard(
    icon: ImageVector,
    title: String,
    accentColor: androidx.compose.ui.graphics.Color,
    points: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            points.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BullGreen,
                        modifier = Modifier.size(14.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = point,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PickCriteriaItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EmeraldLight)
        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
