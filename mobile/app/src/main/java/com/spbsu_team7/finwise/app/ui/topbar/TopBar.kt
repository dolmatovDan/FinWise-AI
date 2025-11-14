package com.spbsu_team7.finwise.app.ui.topbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.R
import com.spbsu_team7.finwise.app.Events
import com.spbsu_team7.finwise.app.UiState

@Composable
fun TopBar(uiState: UiState, events: Events) {
    val borderColor = MaterialTheme.colorScheme.secondaryContainer
    Surface(
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .drawBehind {
            drawLine(
                color = borderColor,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoGraph,
                    contentDescription = "app icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TopBarButton({}, Icons.Default.FileDownload, "export")
                TopBarButton({}, Icons.Default.Settings, "settings")
                TopBarButton({}, Icons.Default.ExitToApp, "exit account")
            }
        }
    }
}

@Composable
fun TopBarButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    description: String,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.medium
        ).height(35.dp).width(35.dp),
        contentPadding = PaddingValues(7.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}