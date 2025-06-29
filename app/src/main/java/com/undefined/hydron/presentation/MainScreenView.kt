package com.undefined.hydron.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.presentation.shared.components.GenericProgressLinearIndicator

@Composable
fun MainScreenView(
    modifier: Modifier,
    viewModel: MainScreenViewModel = hiltViewModel()
){
    val currentUser = viewModel.userResponse.collectAsState()

    Box(modifier = modifier){
        currentUser.let{ stateFlow ->
            when (val response = stateFlow.value) {
                is Response.Success -> {
                    LaunchedEffect(Unit) {
                        viewModel.assignCurrentUser(response.data)
                    }
                }
                is Response.Error -> {
                    viewModel.resetInitialState()
                }
                Response.Loading ->{
                    GenericProgressLinearIndicator()
                }
                else -> {}
            }
        }
    }
}