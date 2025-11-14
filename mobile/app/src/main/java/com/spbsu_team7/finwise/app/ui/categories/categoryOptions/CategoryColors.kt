package com.spbsu_team7.finwise.app.ui.categories.categoryOptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.core.model.UserColor

@Composable
fun CategoryColors(colors: List<UserColor>, selectedColor: UserColor?, onClick: (UserColor) -> Unit) {
    Column(
        modifier = Modifier.height(50.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Цвет",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach {
                var modifier = if (selectedColor == it) Modifier.border(
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(11.dp)
                ) else Modifier
                Surface(
                    modifier = modifier.aspectRatio(1f).weight(1f).padding(3.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Surface(
                        modifier = Modifier.clickable(
                            onClick = { onClick(it) }
                        ),
                        color = it.color,
                        shape = MaterialTheme.shapes.small
                    ) {

                    }
                }
            }
        }
    }
}