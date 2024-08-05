package com.michael.apitester.ui.home

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.michael.apitester.MainActivity
import com.michael.apitester.R
import com.google.ai.client.generativeai.common.shared.FileData

@Composable
fun FileUploadSection(
    homeViewModel: HomeViewModel,
    files: SnapshotStateList<FileData>,
    activity: MainActivity?
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Files")
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(
            painter = painterResource(id = R.drawable.ic_action_delete_circle_outline),
            contentDescription = "remove file",
            modifier = Modifier
                .clickable {
                    if (files.size > 0) {
                        homeViewModel.removeLastFile()
                        homeViewModel.removeLastFileName()
                        homeViewModel.removeLastFileUri()
                    }
                }
                .padding(4.dp)
                .size(21.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_action_add_circle_outline),
            contentDescription = "add file",
            modifier = Modifier
                .clickable {
                    homeViewModel.addFile()
                    homeViewModel.addFileName("Select File...")
                    homeViewModel.addFileUrl(Uri.EMPTY)
                }
                .padding(4.dp)
                .size(24.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        files.forEachIndexed { index, fileData ->
            FileRow(fileData, homeViewModel, index, activity)
        }
    }
}
