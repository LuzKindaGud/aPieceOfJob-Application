package com.bsit.peerx.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import com.bsit.peerx.ui.components.StatusBadge
import com.bsit.peerx.ui.theme.BgPrimary
import com.bsit.peerx.ui.theme.BgSecondary
import com.bsit.peerx.ui.theme.BorderNeon
import com.bsit.peerx.ui.theme.NeonGreen
import com.bsit.peerx.ui.theme.NeonSecondary
import com.bsit.peerx.ui.theme.OctagonShape
import com.bsit.peerx.ui.theme.PeerXTheme
import com.bsit.peerx.ui.theme.Spacing
import com.bsit.peerx.ui.theme.TextMuted
import com.bsit.peerx.ui.theme.TextPrimary

data class ProductRow(
    val name: String,
    val sku: String,
    val stockStatus: String,
    val price: String
)

@Composable
fun ProductsScreen(
    stats: List<Pair<String, String>>,
    products: List<ProductRow>,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberBackground(brush = Brush.verticalGradient(listOf(BgPrimary, BgPrimary))) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { NeonTopBar(title = "Products") }
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
                            text = "Inventory Control".uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                        Text(
                            text = "Track stock levels and pricing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    CyberButton(text = "Add Product", onClick = onAddProduct, icon = Icons.Filled.Add)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    stats.forEach { (label, value) ->
                        StatsCountCard(label = label, value = value, accent = NeonSecondary, modifier = Modifier.weight(1f))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CyberTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = "Search products",
                        leadingIcon = Icons.Filled.Search,
                        modifier = Modifier.weight(1f)
                    )
                    CyberOutlineButton(text = "Filter", onClick = {})
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    itemsIndexed(products) { _, product ->
                        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                            ProductItemRow(product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCountCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(110.dp)
            .background(BgSecondary.copy(alpha = 0.95f), OctagonShape)
            .border(1.dp, BorderNeon, OctagonShape)
            .padding(Spacing.md)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text(value, style = MaterialTheme.typography.displayMedium, color = accent)
        }
    }
}

@Composable
private fun ProductItemRow(product: ProductRow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSecondary.copy(alpha = 0.7f), MaterialTheme.shapes.medium)
            .border(1.dp, BorderNeon, MaterialTheme.shapes.medium)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Spacer(Modifier.height(Spacing.xs))
            Text("SKU ${product.sku}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Text(product.price, style = MaterialTheme.typography.labelLarge, color = NeonGreen)
        Spacer(Modifier.width(Spacing.md))
        StatusBadge(product.stockStatus)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ProductsScreenPreview() {
    PeerXTheme {
        ProductsScreen(
            stats = listOf(
                "Total Items" to "482",
                "Low Stock" to "12",
                "Categories" to "18",
                "On Hold" to "6"
            ),
            products = listOf(
                ProductRow("Neon Router", "NR-204", "Online", "$199"),
                ProductRow("Pulse Hub", "PH-118", "Low Stock", "$149"),
                ProductRow("Quantum Dock", "QD-501", "Critical", "$399")
            ),
            onAddProduct = {}
        )
    }
}





