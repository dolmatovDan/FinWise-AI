package com.spbsu_team7.finwise.app.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.spbsu_team7.finwise.R
import com.spbsu_team7.finwise.app.ui.special.ErrorScreen
import com.spbsu_team7.finwise.app.ui.special.LoadingScreen
import com.spbsu_team7.finwise.app.ui.theme.FinanceTheme
import com.spbsu_team7.finwise.app.ui.util.TextWithOption

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    login: () -> Unit
) {
    val uiState: AuthUiState by viewModel.uiState.collectAsStateWithLifecycle()

    when(uiState) {
        is AuthUiState.Loading -> LoadingScreen()
        is AuthUiState.Error -> ErrorScreen((uiState as AuthUiState.Error).error)
        is AuthUiState.Success -> AuthContent(
            (uiState as AuthUiState.Success).email,
            (uiState as AuthUiState.Success).password,
            viewModel::changeEmail,
            viewModel::changePassword,
            {
                viewModel.login()
                login()
            }
        )
    }
}

@Preview
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AuthScreenPreview() {
    FinanceTheme {
        AuthContent("", "", {}, {}, {})
    }
}

@Composable
fun AuthContent(
    email: String,
    password: String,
    updateEmail: (String) -> Unit,
    updatePassword: (String) -> Unit,
    login: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    ),
                shape = MaterialTheme.shapes.medium,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column (
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box (
                        modifier = Modifier.fillMaxWidth()
                            .weight(0.2f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            modifier = Modifier
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    TextWithOption(
                        name = stringResource(R.string.login),
                        modifier = Modifier.height(55.dp),
                        value = email,
                        placeholder = "",
                        valueChange = updateEmail
                    )
                    TextWithOption(
                        name = stringResource(R.string.password),
                        modifier = Modifier.height(55.dp),
                        value = password,
                        placeholder = "",
                        valueChange = updatePassword
                    )
                    Button(
                        onClick = login,
                        modifier = Modifier.height(30.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContentColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = null,
                    ) {
                        Text(stringResource(R.string.enter))
                    }
                }
            }
        }
    }
}