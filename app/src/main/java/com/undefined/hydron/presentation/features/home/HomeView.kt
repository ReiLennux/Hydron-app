package com.undefined.hydron.presentation.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.presentation.shared.components.GenericProgressLinearIndicator
import com.undefined.hydron.presentation.shared.components.toast.ToastMessage
import com.undefined.hydron.presentation.shared.viewmodels.LoginCheckViewModel


@Composable
fun HomeView(
    modifier: Modifier,
    navController: NavController,
    viewModel: LoginCheckViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        val loginState by viewModel.userResponse.collectAsState()
        val dashboardState by homeViewModel.isLoading.collectAsState()

        ToastMessage(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )

        loginState?.let { state ->
            when (state) {
                is Response.Loading -> {
                    GenericProgressLinearIndicator(modifier = Modifier.fillMaxWidth())
                }

                is Response.Error -> {
                    LaunchedEffect(Unit) {
                        viewModel.resetInitialState()
                    }
                }

                is Response.Success -> {
                    viewModel.assignCurrentUser(currentUser = state.data)
                }

                else -> {}
            }
        }

        when (dashboardState) {
            is Response.Loading -> {
                GenericProgressLinearIndicator()
            }

            is Response.Error -> {
                LaunchedEffect(Unit) {
                    homeViewModel.resetState()
                }
            }

            is Response.Success -> {
                LaunchedEffect(Unit) {
                    homeViewModel.setWeather((dashboardState as Response.Success).data)
                }
            }

            else -> {}
        }
    }
}
