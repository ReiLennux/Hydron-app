package com.undefined.hydron.presentation.shared.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.undefined.hydron.presentation.features.auth.login.LoginScreen
import com.undefined.hydron.presentation.features.auth.register.RegisterScreen
import com.undefined.hydron.presentation.features.dashboard.DashboardScreen
import com.undefined.hydron.presentation.features.placeholder.PlaceHolderScreen
import com.undefined.hydron.presentation.shared.navigation.enums.Routes

fun NavGraphBuilder.mainRoutes(navController: NavController) {
    composable(Routes.HOME.name) { PlaceHolderScreen(navController) }
    composable(Routes.PROFILE.name) { PlaceHolderScreen(navController) }
    composable(Routes.SIGN_UP.name) { RegisterScreen(navController) }
    composable(Routes.DASHBOARD.name) { DashboardScreen(navController) }
    composable(Routes.LOGIN.name) { LoginScreen(navController) }

}