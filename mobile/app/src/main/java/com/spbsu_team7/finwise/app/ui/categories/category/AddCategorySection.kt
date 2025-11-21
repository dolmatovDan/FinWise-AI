package com.spbsu_team7.finwise.app.ui.categories.category

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.unit.dp
import com.spbsu_team7.finwise.app.ui.categories.categoryOptions.CategoryColors
import com.spbsu_team7.finwise.app.ui.categories.categoryOptions.CategoryIcons
import com.spbsu_team7.finwise.app.ui.util.TextWithOption
import com.spbsu_team7.finwise.core.model.Category
import com.spbsu_team7.finwise.core.model.CategoryToSend
import com.spbsu_team7.finwise.core.model.TransactionToSend
import com.spbsu_team7.finwise.core.model.UserColor
import com.spbsu_team7.finwise.core.model.UserIcon
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySection(
    expanded: Boolean,
    onClick: () -> Unit,
    colors: List<UserColor>,
    icons: List<UserIcon>,
    sendCategory: (CategoryToSend) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    var name by remember { mutableStateOf("") }
    var selectedColor: UserColor?  by remember { mutableStateOf(null) }
    var selectedIcon: UserIcon?  by remember { mutableStateOf(null) }

    if (expanded) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            onDismissRequest = onClick,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Новая категория", style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.height(20.dp)
                    )

                    TextWithOption(
                        name = "Название",
                        modifier = Modifier.height(55.dp),
                        value = name,
                        "Название категории",
                        valueChange = { name = it })
                    CategoryColors(
                        colors = colors,
                        selectedColor = selectedColor,
                        onClick = { selectedColor = it })
                    CategoryIcons(
                        icons = icons,
                        selectedIcon = selectedIcon,
                        onClick = { selectedIcon = it })
                }

                AddCategoryButton(Modifier.fillMaxWidth().height(40.dp).align(Alignment.BottomCenter)) {
                    if (!name.isEmpty() && selectedColor != null && selectedIcon != null) {
                        sendCategory(
                            CategoryToSend(0, name, selectedIcon!!.id, selectedColor!!.id)
                        )
                        scope.launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible)
                                    onClick()
                            }
                    }
                }
            }
        }
    }
}


@Composable
fun AddCategoryButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.surface,
        ),
        border = null,
    ) {
        Text("Добавить")
    }
}
