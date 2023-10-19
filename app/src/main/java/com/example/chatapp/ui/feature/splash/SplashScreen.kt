package com.example.chatapp.ui.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.R
import com.example.chatapp.ui.theme.BackgroundGradient
import com.example.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    navigate: (isLoggedIn: Boolean) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.navigateAfterCheckingAuthentication.collectLatest { isLoggedIn ->
            navigate(isLoggedIn)
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = BackgroundGradient
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_safety),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Mensagens seguras, criptografadas e privadas",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSplashScreen() {
    ChatAppTheme {
        SplashScreen()
    }
}