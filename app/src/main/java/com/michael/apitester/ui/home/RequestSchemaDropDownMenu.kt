package com.michael.apitester.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestSchemaDropDownMenu(selectedItem: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val schemas = listOf("http://", "https://")

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = selectedItem, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
                    .clickable {
                        expanded = true
                    })
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            schemas.forEach {
                DropdownMenuItem(text = { Text(text = it) }, onClick = {
                    expanded = false
                    onSelect.invoke(it)
                })
            }
        }
    }
}