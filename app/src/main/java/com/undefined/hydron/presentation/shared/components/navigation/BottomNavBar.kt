package com.undefined.hydron.presentation.shared.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.undefined.hydron.presentation.shared.navigation.navigationItems

@Composable
fun BottomNavBar(navController: NavHostController, visible : Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + expandVertically() + fadeIn(),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navigationItems.forEach { navItem ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                    onClick = {
                        if (navController.currentDestination?.route != navItem.route) {
                            navController.navigate(navItem.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = { Text(text = navItem.title) },
                    alwaysShowLabel = true,
                    icon = { BadgedBox(
                        badge = {
                            if (navItem.hasBadge != null){
                                Badge { Text(text = navItem.hasBadge.toString()) }
                            } else if (navItem.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentDestination?.hierarchy?.any { it.route == navItem.route } == true) {
                                navItem.selectedIcon
                            } else navItem.unselectedIcon, contentDescription = navItem.title)
                    } },
                )
            }
        }
    }
}
