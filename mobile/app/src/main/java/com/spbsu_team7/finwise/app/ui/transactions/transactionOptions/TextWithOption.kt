package com.spbsu_team7.finwise.app.ui.transactions.transactionOptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextWithOption(
    name: String,
    modifier: Modifier,
    value: String,
    placeholder: String,
    valueChange: (String) -> Unit = {},
    onClick: (() -> Unit)? = null,
    trailingIcon: @Composable () -> Unit = {},
) {
    val modifierWithCallback = if (onClick != null) Modifier.padding(horizontal = 0.dp).fillMaxSize().clickable { onClick() }
    else Modifier.padding(horizontal = 0.dp).fillMaxSize()
    Column(
        modifier = modifier.height(60.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = valueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerTextField ->
                Surface(
                    modifier = modifierWithCallback,
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        trailingIcon()
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}