package com.innovaction.finance.presentation.components.inputs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InnovTextField(
    value            : String,
    onValueChange    : (String) -> Unit,
    label            : String,
    modifier         : Modifier = Modifier,
    placeholder      : String?  = null,
    supportingText   : String?  = null,
    isError          : Boolean  = false,
    enabled          : Boolean  = true,
    singleLine       : Boolean  = true,
    keyboardType     : KeyboardType        = KeyboardType.Text,
    imeAction        : ImeAction           = ImeAction.Next,
    keyboardActions  : KeyboardActions     = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon      : @Composable (() -> Unit)? = null,
    trailingIcon     : @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value             = value,
        onValueChange     = onValueChange,
        label             = { Text(label) },
        placeholder       = if (placeholder != null) {{ Text(placeholder) }} else null,
        supportingText    = if (supportingText != null) {{ Text(supportingText) }} else null,
        isError           = isError,
        enabled           = enabled,
        singleLine        = singleLine,
        keyboardOptions   = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions   = keyboardActions,
        visualTransformation = visualTransformation,
        leadingIcon       = leadingIcon,
        trailingIcon      = if (value.isNotEmpty() && trailingIcon == null) {
            { IconButton(onClick = { onValueChange("") }) { Icon(Icons.Filled.Clear, "Effacer") } }
        } else trailingIcon,
        modifier          = modifier.fillMaxWidth(),
        shape             = MaterialTheme.shapes.small,
    )
}

@Composable
fun AmountTextField(
    value         : String,
    onValueChange : (String) -> Unit,
    label         : String,
    currency      : String,
    modifier      : Modifier = Modifier,
    supportingText: String?  = null,
    isError       : Boolean  = false,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = { new ->
            if (new.isEmpty() || new.matches(Regex("^\\d*\\.?\\d{0,2}$"))) onValueChange(new)
        },
        label          = { Text(label) },
        supportingText = if (supportingText != null) {{ Text(supportingText) }} else null,
        isError        = isError,
        singleLine     = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        trailingIcon   = { Text(currency, style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(end = 12.dp)) },
        modifier       = modifier.fillMaxWidth(),
        shape          = MaterialTheme.shapes.small,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> InnovDropdown(
    items          : List<T>,
    selectedItem   : T?,
    onItemSelected : (T) -> Unit,
    label          : String,
    itemLabel      : (T) -> String,
    modifier       : Modifier = Modifier,
    isError        : Boolean  = false,
    supportingText : String?  = null,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it },
        modifier = modifier) {
        OutlinedTextField(
            value         = selectedItem?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            isError       = isError,
            supportingText = if (supportingText != null) {{ Text(supportingText) }} else null,
            modifier      = Modifier.fillMaxWidth().menuAnchor(),
            shape         = MaterialTheme.shapes.small,
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(itemLabel(item)) },
                    onClick = { onItemSelected(item); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnovDateField(
    selectedDate   : String,
    onDateSelected : (String) -> Unit,
    label          : String,
    modifier       : Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    OutlinedTextField(
        value = selectedDate, onValueChange = {}, readOnly = true,
        label = { Text(label) },
        trailingIcon = { IconButton(onClick = { showPicker = true }) {
            Icon(Icons.Filled.CalendarMonth, "Date") } },
        modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small,
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onDateSelected(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)))
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Annuler") } },
        ) { DatePicker(state = state) }
    }
}
