package com.undefined.hydron.presentation.shared.components.textfields

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericDateField(
    modifier: Modifier = Modifier,
    @StringRes labelRes: Int,
    dateValue: String,
    onDateChange: (String) -> Unit,
    errorMessage: String? = null
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val calendar = remember(dateValue) {
        Calendar.getInstance().apply {
            try {
                if (dateValue.isNotEmpty()) {
                    time = dateFormatter.parse(dateValue) ?: Calendar.getInstance().time
                }
            } catch (e: Exception) {
            }
        }
    }

    Box(
        modifier = modifier .fillMaxWidth() .clickable { showDatePicker = true }
    ) {
        Column(
            modifier = modifier.fillMaxWidth().clickable { showDatePicker = true },
        ) {
            OutlinedTextField(
                value = dateValue,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth() .clickable { showDatePicker = true },
                label = { Text(text = stringResource(id = labelRes)) },
                readOnly = true,
                singleLine = true,
                isError = errorMessage != null,
                placeholder = { Text(text = "dd/mm/aaaa") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }


    if (showDatePicker) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                val newDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onDateChange(dateFormatter.format(newDate.time))
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.setOnDismissListener { showDatePicker = false }
        datePickerDialog.show()
    }
}