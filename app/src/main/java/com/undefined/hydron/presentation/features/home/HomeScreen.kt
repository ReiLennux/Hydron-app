package com.undefined.hydron.presentation.features.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    Scaffold(
        content = { innerPadding ->
            HomeContent(paddingValues = innerPadding, navController = navController)
        }
    )
    HomeView(modifier = Modifier.fillMaxSize(), navController = navController)
}