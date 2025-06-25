package com.undefined.hydron.presentation.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.undefined.hydron.domain.models.GenericCatalogModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Componente personalizado de menú desplegable (dropdown)
 *
 * @param modifier Modificador de diseño Compose para personalizar la apariencia
 * @param label ID del recurso de string para la etiqueta del campo
 * @param selectedText Texto actualmente seleccionado que se muestra en el campo
 * @param isExpanded Controla si el menú desplegable está actualmente expandido
 * @param isEnabled Habilita o deshabilita la interacción con el componente (true por defecto)
 * @param items Lista de opciones disponibles para seleccionar (de tipo GenericCatalogModel)
 * @param onSelectedItem Callback que se ejecuta cuando se selecciona un ítem (recibe el modelo completo)
 * @param onShowRequestAction Callback para solicitar la expansión del menú
 * @param onDismissRequestAction Callback para solicitar el cierre del menú
 * @param errorMessage Mensaje de error opcional que se muestra debajo del campo (null por defecto)
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericDropDownMenu(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    selectedText: String,
    isEnabled: Boolean = true,
    items: List<GenericCatalogModel>,
    onSelectedItem: (GenericCatalogModel) -> Unit,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && isEnabled,
        onExpandedChange = { expanded = it && isEnabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            enabled = isEnabled,
            label = { Text(stringResource(id = label)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            isError = errorMessage != null
        )

        ExposedDropdownMenu(
            expanded = expanded && isEnabled,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.value) },
                    onClick = {
                        onSelectedItem(item)
                        expanded = false
                    }
                )
            }
        }
    }

    errorMessage?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(6.dp))
}