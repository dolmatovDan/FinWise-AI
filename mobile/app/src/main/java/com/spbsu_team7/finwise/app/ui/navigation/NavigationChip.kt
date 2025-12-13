package com.spbsu_team7.finwise.app.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class NavItem(val title: String, val icon: ImageVector, val iconFilled: ImageVector)

@Composable
fun NavigationChip(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.onSurface

    val icon = if (selected) item.iconFilled
    else item.icon

    Button(
        modifier = modifier.fillMaxHeight().padding(0.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.title,
                tint = backgroundColor,
                modifier = Modifier.padding(),
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(color = backgroundColor)
            )
        }
    }
}