package com.example.apitester.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.apitester.utils.HttpMethod

@Composable
fun RequestMethodDropDownMenu(selectedItem: HttpMethod, onSelect: (HttpMethod) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = selectedItem.method, modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    expanded = true
                }, color = Color(0xffF59F3F)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            HttpMethod.allMethods.forEach { method ->
                DropdownMenuItem(text = { Text(text = method.method) }, onClick = {
                    expanded = false
                    onSelect.invoke(method)
                })
            }
        }
    }
}