package com.example.apitester.ui.home

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
import com.example.apitester.R

@Composable
fun RequestAdditionalParams(
    homeViewModel: HomeViewModel,
    title: String,
    params: SnapshotStateList<Pair<String, String>>
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
                    if (params.size > 0) {
                        homeViewModel.removeLastParameter()
                    }
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
                    homeViewModel.addParameter(Pair("", ""))
                }
                .padding(4.dp)
                .size(24.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    params.forEachIndexed { index, parameter ->
        val valueFocusRequester = remember { FocusRequester() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = parameter.first,
                onValueChange = { key ->
                    homeViewModel.updateParameter(index, key to parameter.second)
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
                value = parameter.second,
                onValueChange = { value ->
                    homeViewModel.updateParameter(index, parameter.first to value)
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