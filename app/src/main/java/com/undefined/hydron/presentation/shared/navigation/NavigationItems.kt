package com.undefined.hydron.presentation.shared.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.undefined.hydron.presentation.shared.navigation.enums.Routes

data class NavigationItems(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val hasBadge: Int? = null
)

val navigationItems = listOf(
    NavigationItems(
        title = "Inicio",
        route = Routes.HOME.name,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false
    ),
    NavigationItems(
        title = "Perfil",
        route = Routes.PROFILE.name,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        hasNews = false
    ),
    NavigationItems(
        title = "TODO",
        route = Routes.DASHBOARD.name,
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        hasNews = false
    )
)