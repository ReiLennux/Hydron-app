package com.undefined.hydron.presentation.features.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            DashboardContent()

        }
    }

    DashboardView()
}
