package com.undefined.hydron.presentation.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.presentation.shared.components.GenericProgressLinearIndicator
import com.undefined.hydron.presentation.shared.components.toast.ToastMessage


@Composable
fun ProfileView (
    modifier: Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.isLoading.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ToastMessage(
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )


        state.let { state ->

            when (state.value) {
                is Response.Loading -> {
                    GenericProgressLinearIndicator(modifier = Modifier.fillMaxWidth())
                }

                is Response.Error -> {
                    LaunchedEffect(Unit) {
                        viewModel.resetState()
                    }
                }

                is Response.Success -> {
                        viewModel.resetState()
                }

                else -> {}
            }

        }
    }
}