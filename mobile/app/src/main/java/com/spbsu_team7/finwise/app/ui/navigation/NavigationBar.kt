package com.spbsu_team7.finwise.app.ui.navigation

import android.R
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp


@Composable
fun NavigationBar(
    items: List<NavItem>,
    selectedItem: Int,
    onSelect: (Int) -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.secondaryContainer
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.drawBehind {
            drawLine(
                color = borderColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 2.dp.toPx()
            )
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp, vertical = 0.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    NavigationChip(
                        item = item,
                        selected = selectedItem == index,
                        onClick = { onSelect(index) },
                        modifier = Modifier.weight(0.1f)
                    )
                }
            }
        }
    }
}