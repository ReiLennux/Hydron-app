package com.undefined.hydron.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.undefined.hydron.domain.models.Response
import com.undefined.hydron.presentation.shared.components.GenericProgressLinearIndicator
import com.undefined.hydron.presentation.shared.viewmodels.LoginCheckViewModel

@Composable
fun MainScreenView(
    modifier: Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginCheckViewModel = hiltViewModel()
) {
    val currentUser by viewModel.userResponse.collectAsState()
    val navigateToLogin by viewModel.navigateToLogin.collectAsState()
    val isAuthenticationComplete by viewModel.isAuthenticationComplete.collectAsState()

    LaunchedEffect(navigateToLogin, isAuthenticationComplete) {
        if (isAuthenticationComplete) {
            if (navigateToLogin) {
                onNavigateToLogin()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        currentUser.let { response ->
            when (response) {
                is Response.Success -> {
                    LaunchedEffect(response.data) {
                        viewModel.assignCurrentUser(response.data)
                        // Navegar a la pantalla principal después de asignar el usuario
                        onNavigateToMain()
                    }

                    Text(
                        text = "Bienvenido ${response.data.name}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Response.Error -> {
                    if (!navigateToLogin) {
                        Text(
                            text = "Error: ${response.exception?.message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Response.Loading -> {
                    GenericProgressLinearIndicator()
                }
                null -> {
                    GenericProgressLinearIndicator()
                }

                Response.Idle -> TODO()
            }
        }
    }
}
