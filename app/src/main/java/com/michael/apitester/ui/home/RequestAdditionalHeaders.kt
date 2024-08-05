package com.michael.apitester.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.michael.apitester.R

@Composable
fun RequestAdditionalHeaders(
    homeViewModel: HomeViewModel,
    title: String,
    headers: SnapshotStateList<Pair<String, String>>
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(
            painter = painterResource(id = R.drawable.ic_action_delete_circle_outline),
            contentDescription = "remove header",
            modifier = Modifier
                .clickable {
                    if (headers.size > 0) homeViewModel.removeLastHeader()
                }
                .padding(4.dp)
                .size(21.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_action_add_circle_outline),
            contentDescription = "add header",
            modifier = Modifier
                .clickable {
                    homeViewModel.addHeader(Pair("", ""))
                }
                .padding(4.dp)
                .size(24.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    headers.forEachIndexed { index, header ->
        val valueFocusRequester = remember { FocusRequester() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = header.first,
                onValueChange = { key ->
                    homeViewModel.updateHeader(index, key to header.second)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(valueFocusRequester),
                placeholder = {
                    Text(text = "Key")
                },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        valueFocusRequester.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = header.second,
                onValueChange = { value ->
                    homeViewModel.updateHeader(index, header.first to value)
                },
                modifier = Modifier
                    .weight(2f)
                    .focusRequester(valueFocusRequester),
                placeholder = {
                    Text(text = "Value")
                },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
