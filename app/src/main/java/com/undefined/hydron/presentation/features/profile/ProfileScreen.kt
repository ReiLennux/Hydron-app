package com.undefined.hydron.presentation.features.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ProfileScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            ProfileContent(paddingValues = innerPadding, navController = navController)
        }
    )
    ProfileView(modifier = Modifier.fillMaxSize(), navController = navController)
}