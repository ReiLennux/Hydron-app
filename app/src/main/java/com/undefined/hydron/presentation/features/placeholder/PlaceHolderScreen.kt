package com.undefined.hydron.presentation.features.placeholder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun PlaceHolderScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            PlaceHolderContent(paddingValues = innerPadding, navController = navController)
        }
    )
    PlaceHolderView(modifier = Modifier.fillMaxSize(), navController = navController)
}