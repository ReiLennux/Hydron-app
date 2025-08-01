package com.undefined.hydron.presentation.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.presentation.shared.components.GenericProgressLinearIndicator
import com.undefined.hydron.presentation.shared.viewmodels.LoginCheckViewModel


@Composable
fun HomeView (
    modifier: Modifier,
    navController: NavController,
    viewModel: LoginCheckViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {

        val state by viewModel.userResponse.collectAsState()

        state?.let { state ->

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

    }
}