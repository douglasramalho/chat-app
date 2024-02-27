package com.example.chatapp.ui.feature.splash

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatapp.R
import com.example.chatapp.ui.theme.BackgroundGradient
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    navigateToSigIn: () -> Unit,
    navigateToConversationsList: () -> Unit,
    closeApp: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val currentNavigateToSigIn by rememberUpdatedState(navigateToSigIn)
    val currentNavigateToConversationsList by rememberUpdatedState(navigateToConversationsList)
    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashViewModel.SplashUiState.Success -> {
                if ((uiState as SplashViewModel.SplashUiState.Success).user.id.isNotEmpty()) {
                    currentNavigateToConversationsList()
                } else currentNavigateToSigIn()
            }
            else -> {}
        }
    }

    if (uiState is SplashViewModel.SplashUiState.Error) {
        AlertDialog(
            onDismissRequest = {  },
            confirmButton = {
                Text(
                    text = context.getString(R.string.common_ok),
                    modifier = Modifier
                        .clickable {
                            closeApp()
                        }
                )
            },
            title = {
                Text(text = context.getString(R.string.common_generic_error_title))
            },
            text = {
                Text(text = context.getString(R.string.common_generic_error_message))
            }
        )
    }

    SplashScreen(uiState = uiState)
}

@Composable
fun SplashScreen(
    context: Context = LocalContext.current,
    uiState: SplashViewModel.SplashUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = BackgroundGradient
            )
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_safety),
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = context.getString(R.string.splash_safety_info),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Box(modifier = Modifier.height(48.dp)) {
            if (uiState is SplashViewModel.SplashUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    strokeWidth = 1.dp,
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewSplashScreen() {
    ChatAppTheme {
        SplashScreen(
            uiState = SplashViewModel.SplashUiState.Loading
        )
    }
}