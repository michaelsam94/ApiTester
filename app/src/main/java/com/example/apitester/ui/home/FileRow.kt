package com.example.apitester.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apitester.MainActivity
import com.example.apitester.R
import com.example.apitester.utils.MimeType
import com.example.apitester.utils.toMimeType
import com.google.ai.client.generativeai.common.shared.FileData

@Composable
fun FileRow(
    fileData: FileData,
    homeViewModel: HomeViewModel,
    index: Int,
    activity: MainActivity?
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMimeType by remember { mutableStateOf(fileData.mimeType.toMimeType()) }
    val fileNames = homeViewModel.fileNames.observeAsState(emptyList<String>())
    val fileUris = homeViewModel.fileUri.observeAsState(emptyList())

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            activity?.pickFile(index, selectedMimeType.type)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach),
                contentDescription = "attach file"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = fileNames.value[index], // Display the selected file name
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(4.0f)
                .clickable {
                    // Check if the selected file name is not empty and does not have a default value
                    if (fileNames.value[index].isNotEmpty() && fileNames.value[index] != "Select File...") {
                        // Create an intent to open the file
                        val fileUri = Uri.parse(fileUris.value[index].toString())
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(fileUri, selectedMimeType.type)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        activity?.startActivity(intent)
                    }
                },
            color = Color.Gray
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.weight(2f)) {
            Text(
                text = selectedMimeType.type,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { expanded = true },
                color = Color(0xffF59F3F)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                MimeType.allMimeTypes.forEach { mimeType ->
                    DropdownMenuItem(onClick = {
                        selectedMimeType = mimeType
                        homeViewModel.updateFileType(index, mimeType.type)
                        expanded = false
                    }, text = {
                        Text(text = mimeType.type)
                    })
                }
            }
        }
    }


}