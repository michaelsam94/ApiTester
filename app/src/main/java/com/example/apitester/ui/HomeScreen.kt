package com.example.apitester.ui

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.apitester.utils.HttpMethod
import com.example.apitester.R
import com.example.apitester.utils.isInternetAvailable
import com.example.apitester.utils.isValidJson
import com.example.apitester.utils.isValidUrl
import com.example.domain.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = ViewModelFactory())
) {
    val selectedMethod by homeViewModel.selectedMethod.observeAsState(HttpMethod.GET)
    val selectedSchema by homeViewModel.selectedSchema.observeAsState("https://")
    val url by homeViewModel.url.observeAsState("dummyjson.com/products/")
    val body by homeViewModel.body.observeAsState("")
    val isLoading by homeViewModel.isLoading.observeAsState(false)
    val headersList = homeViewModel.headers.observeAsState(SnapshotStateList())
    val paramsList = homeViewModel.parameters.observeAsState(SnapshotStateList())
    val response = homeViewModel.response.observeAsState("")

    val context = LocalContext.current
    var sendEnabled by remember(url) {
        mutableStateOf(url.isValidUrl())
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = { Text(text = "Api Tester") },
            Modifier.background(Color(0xffF59F3F)),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xffF59F3F),
                titleContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = {
                    navController.navigate("history")
                }) {
                    Icon(painter = painterResource(id = R.drawable.ic_history), tint = Color.White, contentDescription = "history")
                }
            }
        )
    }) { innerPadding ->
        if (isLoading) {
            // Show loading indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Loading...")
                }
            }
        } else {
            Column {
                Column(
                    modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Request Method")
                        RequestMethodDropDownMenu(selectedItem = selectedMethod) { selected ->
                            homeViewModel.updateSelectedMethod(selected)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        RequestSchemaDropDownMenu(selectedItem = selectedSchema) { selected ->
                            homeViewModel.updateSelectedSchema(selected)
                        }
                        OutlinedTextField(
                            value = url,
                            onValueChange = { value ->
                                homeViewModel.updateUrl(value)
                                sendEnabled = value.isValidUrl()
                            },
                            Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(text = "www.example.com")
                            },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    RequestAdditionalHeaders(homeViewModel = homeViewModel,title = "Headers", headers = headersList.value)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    RequestAdditionalParams(homeViewModel = homeViewModel,title = "Parameters", params = paramsList.value)
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Body")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = body,
                        onValueChange = { value -> homeViewModel.updateBody(value) },
                        minLines = 5,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(
                    enabled = sendEnabled,
                    onClick = {
                        if (!isInternetAvailable(context)) {
                            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (selectedMethod == HttpMethod.POST && !body.isValidJson()) {
                            Toast.makeText(context, "JSON body not valid", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        homeViewModel.setLoading(true)
                        homeViewModel.makeRequest(selectedMethod.method, url, headersList.value.toMap(), paramsList.value.toMap(), body) { result ->
                            val mainHandler = Handler(Looper.getMainLooper())
                            mainHandler.post {
                                homeViewModel.setLoading(false)
                                when (result) {
                                    is NetworkResult.Success -> {
                                        val encodedParams = Uri.encode(paramsList.value
                                            .filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                            .joinToString("&") { "${Uri.encode(it.first)}=${Uri.encode(it.second)}" }
                                        )
                                        // Navigate to the response screen with the actual response
                                        navController.navigate(
                                            "response?response=${Uri.encode(result.response)}" +
                                                    "&requestUrl=${Uri.encode(url)}&responseCode=${result.responseCode}" +
                                                    "&error=${Uri.encode("")}&headers=" +
                                                    "${Uri.encode(headersList.value.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                                        .toString())}&body=${Uri.encode(body)}&params=$encodedParams" +
                                                    "&requestTime=${result.requestTime}")
                                    }
                                    is NetworkResult.Error -> {
                                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                ) {
                    Text(text = "Send Request")
                }
            }
        }
    }
}

@Composable
fun RequestAdditionalHeaders(homeViewModel: HomeViewModel, title: String, headers: SnapshotStateList<Pair<String, String>>) {
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
                    if (headers.size > 0) headers.removeLast()
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
                    headers.add(Pair("", ""))
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

@Composable
fun RequestAdditionalParams(homeViewModel: HomeViewModel, title: String, params: SnapshotStateList<Pair<String, String>>) {
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
                    if (params.size > 0) params.removeLast()
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
                    params.add(Pair("", ""))
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


