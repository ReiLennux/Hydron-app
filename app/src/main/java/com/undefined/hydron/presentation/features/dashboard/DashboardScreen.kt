package com.undefined.hydron.presentation.features.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.hydron.presentation.shared.navigation.enums.Routes

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val showDrawer = viewModel.showAddTaskDrawe.observeAsState(false)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.toggleAddTaskDrawe() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            DashboardContent(viewModel, navController)
            if (showDrawer.value) {
                AddTaskModal(viewModel)
            }
        }
    }
}
