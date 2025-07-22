package com.undefined.hydron.presentation.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun HomeContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val transferState by viewModel.transferState
    var showDialog by remember { mutableStateOf(false) }
    val totalItems by viewModel.totalItems.collectAsState(initial = 0)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Estado actual: ${transferState::class.simpleName}")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total de registros: $totalItems")
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = transferState) {
            is HomeViewModel.TransferState.Idle -> {
                Button(
                    onClick = {
                        viewModel.startTransfer()
                              },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Iniciar Carga de Datos")
                }
            }
            is HomeViewModel.TransferState.Loading -> {
                CircularProgressIndicator(
                    progress = state.progress / 100f,
                    modifier = Modifier.size(100.dp),
                )
                Text(
                    text = "${state.progress}%",
                    style = MaterialTheme.typography.titleLarge,
                    )
                    Button(
                        onClick = { showDialog = true }
                    ){
                        Text(text="Cancelar")
                    }
            }
            HomeViewModel.TransferState.Success -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(100.dp))
                Text(
                    text = "Datos transferidos con éxito",
                    style = MaterialTheme.typography.titleLarge)
            }
            HomeViewModel.TransferState.Cancelled -> {
                Text("Transferencia cancelada")
                Button(
                    onClick = { viewModel.startTransfer() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Reintentar")
                }
            }
            is HomeViewModel.TransferState.Error -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(100.dp))
                Text(
                    text = state.message ?: "Error desconocido",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error)
                Button(
                    onClick = { viewModel.startTransfer() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Reintentar")
                }
            }
        }


    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Cancelación") },
            text = { Text("¿Estás seguro de que deseas cancelar la transferencia?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelTransfer()
                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun PlaceHolder(
    paddingValues: PaddingValues,
    message: String = "Contenido no disponible"
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.HourglassEmpty,
                contentDescription = "Placeholder",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}