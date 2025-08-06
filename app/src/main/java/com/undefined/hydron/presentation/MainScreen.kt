package com.undefined.hydron.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.undefined.hydron.presentation.shared.navigation.mainRoutes
import com.undefined.hydron.presentation.shared.viewmodels.LoginCheckViewModel


@Composable
fun MainScreen(

) {
    val viewModel: MainScreenViewModel = hiltViewModel()
    val loginViewModel: LoginCheckViewModel = hiltViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isVisible = remember { mutableStateOf(true) }


    val navigateToLogin by loginViewModel.navigateToLogin.collectAsState()

    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            navController.navigate(Routes.LOGIN.name) {
                popUpTo(Routes.LOGIN.name) { inclusive = true }
            }
        }
    }

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

        AnimatedVisibility(
            visible = isVisible.value,
            modifier = Modifier
                .fillMaxSize()
        ) {
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = Routes.HOME.name
            ) {
                mainRoutes(navController = navController)
            }
        }

    }
}