package com.undefined.hydron.presentation.features.auth.register

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            RegisterContent(paddingValues = innerPadding, navController = navController)
        }
    )
    RegisterView(modifier = Modifier.fillMaxSize(), navController = navController)
}