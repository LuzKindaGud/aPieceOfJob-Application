package com.bsit.peerx.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsit.peerx.ui.components.CyberBackground
import com.bsit.peerx.ui.components.CyberButton
import com.bsit.peerx.ui.components.CyberOutlineButton
import com.bsit.peerx.ui.components.CyberTextField
import com.bsit.peerx.ui.components.NeonTopBar
import com.bsit.peerx.ui.components.StatCard
import com.bsit.peerx.ui.components.StatusBadge
import com.bsit.peerx.ui.theme.BgPrimary
import com.bsit.peerx.ui.theme.BgSecondary
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.GradientGold
import com.bsit.peerx.ui.theme.GradientSilver
import com.bsit.peerx.ui.theme.NeonCyan
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonPrimary
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary
import com.bsit.peerx.ui.theme.neonBorder

data class DashboardStat(
    val label: String,
    val value: String,
    val change: String,
    val isUp: Boolean,
    val accent: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class TopProduct(
    val name: String,
    val sales: String,
    val rank: Int
)

data class OrderRow(
    val id: String,
    val customer: String,
    val amount: String,
    val status: String
)

@Composable
fun DashboardScreen(
    stats: List<DashboardStat>,
    topProducts: List<TopProduct>,
    orders: List<OrderRow>,
    onCreateReport: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberBackground(brush = Brush.verticalGradient(listOf(BgPrimary, BgPrimary))) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { NeonTopBar(title = "Dashboard") }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(Spacing.lg)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Operations Overview".uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Real-time metrics and activity.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        CyberOutlineButton(text = "Export", onClick = onExport)
                        CyberButton(text = "New Report", onClick = onCreateReport)
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(stats) { stat ->
                        StatCard(
                            label = stat.label,
                            value = stat.value,
                            change = stat.change,
                            isUp = stat.isUp,
                            icon = stat.icon,
                            accentColor = stat.accent
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    DashboardPanel(
                        title = "Sales Snapshot",
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(BgSecondary, RoundedCornerShape(12.dp))
                                .neonBorder(RoundedCornerShape(12.dp), BorderNeon)
                        ) {
                            Text(
                                text = "Chart Placeholder",
                                modifier = Modifier.align(Alignment.Center),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextMuted
                            )
                        }
                    }
                    DashboardPanel(
                        title = "Top Products",
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            topProducts.forEach { product ->
                                TopProductRow(product)
                            }
                        }
                    }
                }

                DashboardPanel(title = "Orders Pipeline") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CyberTextField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Search orders",
                            leadingIcon = Icons.Filled.Search,
                            modifier = Modifier.weight(1f)
                        )
                        CyberOutlineButton(text = "Filter", onClick = {})
                    }
                    Spacer(Modifier.height(Spacing.md))
                    OrderHeaderRow()
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        itemsIndexed(orders) { index, order ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                OrderItemRow(order, index)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(BgSecondary.copy(alpha = 0.92f), RoundedCornerShape(16.dp))
            .neonBorder(RoundedCornerShape(16.dp), BorderNeon)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        content()
    }
}

@Composable
private fun TopProductRow(product: TopProduct) {
    val badgeBrush = when (product.rank) {
        1 -> GradientGold
        2 -> GradientSilver
        else -> Brush.linearGradient(listOf(NeonSecondary, NeonPrimary))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(badgeBrush, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${product.rank}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Text(product.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
        Text(product.sales, style = MaterialTheme.typography.labelLarge, color = NeonGreen)
    }
}

@Composable
private fun OrderHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("ID", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text("Customer", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text("Amount", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text("Status", style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun OrderItemRow(order: OrderRow, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .border(1.dp, BorderNeon, RoundedCornerShape(10.dp))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(order.id, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(order.customer, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Text(order.amount, style = MaterialTheme.typography.labelLarge, color = NeonCyan)
        StatusBadge(order.status)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun DashboardScreenPreview() {
    PeerXTheme {
        DashboardScreen(
            stats = listOf(
                DashboardStat("Active Peers", "248", "+12%", true, NeonCyan, Icons.Filled.Person),
                DashboardStat("Messages", "1.2K", "+5%", true, NeonGreen, Icons.Filled.Email),
                DashboardStat("Transfers", "89", "-2%", false, NeonSecondary, Icons.Filled.ShoppingCart),
                DashboardStat("Inventory", "42", "+9%", true, NeonPrimary, Icons.Filled.Search)
            ),
            topProducts = listOf(
                TopProduct("Neon Router", "890 sold", 1),
                TopProduct("Quantum Dock", "672 sold", 2),
                TopProduct("Pulse Hub", "540 sold", 3)
            ),
            orders = listOf(
                OrderRow("A-102", "Nova", "$1,250", "Completed"),
                OrderRow("A-103", "Orion", "$860", "Processing"),
                OrderRow("A-104", "Luna", "$420", "Pending")
            ),
            onCreateReport = {},
            onExport = {}
        )
    }
}





