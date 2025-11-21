package com.spbsu_team7.finwise.app.ui.transactions.transactionOptions

import android.media.Image
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.MainScreen
import com.spbsu_team7.finwise.app.ui.transactions.transaction.TransactionRow
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.UserIcon
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCategory(modifier: Modifier, categories: List<Category>, selectedCategory: Category?, onChange: (Category) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "Категория",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
        Surface(
            modifier = Modifier.clickable(onClick = { expanded = !expanded }),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {

                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(contentAlignment = Alignment.CenterStart) {
                        Icon(
                            modifier = Modifier.padding(0.dp),
                            imageVector = if (!expanded) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                            contentDescription = "выбрать категорию"
                        )
                    }
                    if (selectedCategory != null) {
                        Icon (
                            imageVector = selectedCategory.icon,
                            contentDescription = selectedCategory.name,
                            tint = selectedCategory.color,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }

                    if (selectedCategory == null) {
                        Text(
                            text = "Выберите категорию",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else (
                            Text(
                                text = selectedCategory.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                    )
                }

                if (expanded) {
                    ModalBottomSheet(
                        modifier = Modifier,
                        onDismissRequest = {
                            expanded = false
                        },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {

                            LazyColumn(
                                modifier = Modifier.height(400.dp)
                            ) {
                                items(items = categories.mapIndexed { index, category -> Pair(index, category) }) { (index, category) ->
                                    CategoryRow(
                                        category = category,
                                        {
                                            onChange(category)
                                            scope.launch { sheetState.hide() }
                                                .invokeOnCompletion {
                                                    if (!sheetState.isVisible) {
                                                        expanded = false
                                                    }
                                                }
                                        })
                                    if (index != categories.size - 1)
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 70.dp),
                                        thickness = Dp.Hairline, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                    }
                }

        }

    }

}


@Composable
fun CategoryRow(category: Category, onClick: () -> Unit) {
    Row (
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon (
            imageVector = category.icon,
            contentDescription = category.name,
            tint = category.color,
            modifier = Modifier.padding(vertical = 10.dp).weight(0.2f)
        )
        Text(text = category.name, style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.8f),
            )
    }

}