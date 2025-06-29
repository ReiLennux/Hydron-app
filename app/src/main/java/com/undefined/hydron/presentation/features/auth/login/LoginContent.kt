package com.undefined.hydron.presentation.features.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.hydron.R
import com.undefined.hydron.presentation.features.auth.register.ActionsSection
import com.undefined.hydron.presentation.features.auth.register.RegisterFormEvent
import com.undefined.hydron.presentation.features.auth.register.RegisterSection
import com.undefined.hydron.presentation.features.auth.register.RegisterTitle
import com.undefined.hydron.presentation.features.auth.register.RegisterViewModel
import com.undefined.hydron.presentation.shared.components.textfields.GenericPasswordTextField
import com.undefined.hydron.presentation.shared.components.textfields.GenericTextField
import com.undefined.hydron.presentation.shared.navigation.enums.Routes

@Composable
fun LoginContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .padding(paddingValues),
    ) {

        LoginTitle()
        LoginForm(
            viewModel = viewModel,
            navController = navController
        )
        ActionsSection(
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun LoginTitle(){
    Text(
        text = "Bienvenido de nuevo!",
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LoginForm(
    viewModel: LoginViewModel,
    navController: NavController,
) {

    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "Inicia Sesion con tu cuenta o crea una nueva.")
        GenericTextField(
            value = email,
            onValueChange = {
                viewModel.onEvent(
                    LoginFormEvent.EmailChanged(it)
                )
            },
            labelRes = R.string.val_email_label,
            errorMessage = viewModel.emailError.value,
        )
        GenericPasswordTextField(
            value = password,
            onValueChange = {
                viewModel.onEvent(
                    LoginFormEvent.PasswordChanged(it)
                )
            },
            labelRes = R.string.val_password_label,
            errorMessage = viewModel.passwordError.value,
        )
    }
}

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navController: NavController,
    enabled: Boolean = true // Nuevo parámetro para controlar el estado de los botones
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { navController.navigate(Routes.SIGN_UP.name) },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.register_description) // Usar recursos de strings
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.register_action),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Botón de Inicio de Sesión
        Button(
            onClick = { viewModel.onEvent(LoginFormEvent.Submit) },
            modifier = Modifier.weight(1f),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.login_description)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.login_action),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
