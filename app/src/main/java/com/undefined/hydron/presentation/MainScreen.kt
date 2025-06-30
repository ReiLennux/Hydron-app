package com.undefined.hydron.presentation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.undefined.hydron.presentation.shared.components.navigation.BottomNavBar
import com.undefined.hydron.presentation.shared.components.navigation.TopAppBar
import com.undefined.hydron.presentation.shared.navigation.enums.Routes
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.undefined.hydron.infrastructure.services.ApiForegroundService
import com.undefined.hydron.presentation.shared.navigation.mainRoutes


@Composable
fun MainScreen() {
    val viewModel: MainScreenViewModel = hiltViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser by viewModel.currentUser.observeAsState(initial = null)

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val intent = Intent(context, ApiForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    val navigateToLogin by viewModel.navigateToLogin.collectAsState()

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(Routes.LOGIN.name) {
                popUpTo(Routes.LOGIN.name) { inclusive = true }
            }
        }
    }

    //MainScreenView(modifier = Modifier)
    Scaffold(
        topBar = {
            TopAppBar(visible = viewModel.verifyRouteTop(currentRoute = currentRoute))
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                visible = viewModel.verifyRouteBottom(currentRoute = currentRoute)
            )
        }
    ) { paddingValues ->

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Routes.HOME.name
        ) {

            mainRoutes(navController = navController)
        }
    }
}