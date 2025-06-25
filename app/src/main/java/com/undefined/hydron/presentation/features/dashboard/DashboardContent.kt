package com.undefined.hydron.presentation.features.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun DashboardContent(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavController
) {
    val tasks by viewModel.tasks.observeAsState(emptyList())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { /* Navegar si quieres */ }
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = {
                        task.isCompleted = it
                        viewModel.updateTask(task)
                    }
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Text(task.description, style = MaterialTheme.typography.bodyMedium)
                }
                Button(
                    onClick = { viewModel.deleteTask(task) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    Text("Eliminar")
                }

            }
        }
    }
}


@Composable
fun AddTaskModal(viewModel: DashboardViewModel) {
    val title by viewModel.taskTitle.observeAsState("")
    val description by viewModel.taskDescription.observeAsState("")

    ModalBottomSheet(onDismissRequest = { viewModel.toggleAddTaskDrawe() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nueva tarea", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.setTaskTitle(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.setTaskDescription(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.addTask()
                    viewModel.toggleAddTaskDrawe()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}
