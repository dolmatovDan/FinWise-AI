package com.spbsu_team7.finwise.app.ui.categories.categoryOptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun CategoryColors() {
    var selectedColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF03A9F4),
        Color(0xFF9C27B0),
        Color(0xFFFF9800),
        Color(0xFFF44336),
        Color(0xFFE91E63),
        Color(0xFF009688),
        Color(0xFF3F51B5),
        Color(0xFF673AB7),
        Color(0xFFCE67D5)
    )
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach {
                Surface(
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    color = it,
                    shape = MaterialTheme.shapes.small
                ) {

                }
            }
        }
    }
}