package com.spbsu_team7.finwise.app.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
    else MaterialTheme.colorScheme.secondaryContainer

    val icon = if (selected) item.iconFilled
    else item.icon

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxHeight().clickable(
            onClick = onClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )
    ) {

        Icon(
            imageVector = icon,
            contentDescription = item.title,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 10.dp)
        )
//            Spacer(modifier = Modifier.width(6.dp))
//            Text(
//                text = label,
//                style = MaterialTheme.typography.labelLarge,
//                color = contentColor
//            )

    }
}