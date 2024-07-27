package com.example.apitester.ui.home

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.apitester.MainActivity
import com.example.apitester.R
import com.example.apitester.ui.ViewModelFactory
import com.example.apitester.utils.HttpMethod
import com.example.apitester.utils.getFileFromUri
import com.example.apitester.utils.isInternetAvailable
import com.example.apitester.utils.isValidJson
import com.example.apitester.utils.isValidUrl
import com.example.domain.NetworkResult
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory()),
    activity: MainActivity? = null
) {
    val selectedMethod by viewModel.selectedMethod.observeAsState(HttpMethod.GET)
    val selectedSchema by viewModel.selectedSchema.observeAsState("https://")
    val url by viewModel.url.observeAsState("dummyjson.com/products/")
    val body by viewModel.body.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val headersList = viewModel.headers.observeAsState(SnapshotStateList())
    val paramsList = viewModel.parameters.observeAsState(SnapshotStateList())
    val response = viewModel.response.observeAsState("")
    val filesList = viewModel.files.observeAsState(SnapshotStateList())
    var isBodyView by remember { mutableStateOf(true) }

    val fileUris = viewModel.fileUri.observeAsState(emptyList())

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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        tint = Color.White,
                        contentDescription = "history"
                    )
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
                            viewModel.updateSelectedMethod(selected)
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
                            viewModel.updateSelectedSchema(selected)
                        }
                        OutlinedTextField(
                            value = url,
                            onValueChange = { value ->
                                viewModel.updateUrl(value)
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

                    RequestAdditionalHeaders(
                        homeViewModel = viewModel,
                        title = "Headers",
                        headers = headersList.value
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    RequestAdditionalParams(
                        homeViewModel = viewModel,
                        title = "Parameters",
                        params = paramsList.value
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle between Body and File Upload views
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Request Body")
                        Spacer(modifier = Modifier.weight(1.0f))
                        Text(text = if (isBodyView) "Switch to File Upload" else "Switch to Body")
                        IconButton(onClick = { isBodyView = !isBodyView }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_switch),
                                contentDescription = "Switch View"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isBodyView) {
                        OutlinedTextField(
                            value = body,
                            onValueChange = { value -> viewModel.updateBody(value) },
                            minLines = 5,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        FileUploadSection(
                            homeViewModel = viewModel,
                            files = filesList.value,
                            activity = activity
                        )
                    }
                }
                Button(
                    enabled = sendEnabled,
                    onClick = {
                        if (!isInternetAvailable(context)) {
                            Toast.makeText(context, "Internet not connected", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }

                        if (selectedMethod == HttpMethod.POST && isBodyView && !body.isValidJson()) {
                            Toast.makeText(context, "JSON body not valid", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }

                        viewModel.setLoading(true)
                        val filesMap: Map<String, File?> = fileUris.value.mapIndexed { index, item ->
                            "key $index" to context.getFileFromUri(item.toString())
                        }.toMap()
                        viewModel.makeRequest(
                            method = selectedMethod.method,
                            url = url,
                            headers = headersList.value.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                .toMap(),
                            parameters = paramsList.value.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                .toMap(),
                            body = if (isBodyView) body else null,
                            files = if (!isBodyView) filesMap else null
                        ) { result ->
                            val mainHandler = Handler(Looper.getMainLooper())
                            mainHandler.post {
                                viewModel.setLoading(false)
                                when (result) {
                                    is NetworkResult.Success -> {
                                        val encodedParams = Uri.encode(paramsList.value
                                            .filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                            .joinToString("&") {
                                                "${Uri.encode(it.first)}=${
                                                    Uri.encode(
                                                        it.second
                                                    )
                                                }"
                                            }
                                        )
                                        // Navigate to the response screen with the actual response
                                        navController.navigate(
                                            "response?response=${Uri.encode(result.response)}" +
                                                    "&requestUrl=${Uri.encode(url)}&responseCode=${result.responseCode}" +
                                                    "&error=${result.message}&headers=" +
                                                    "${
                                                        Uri.encode(headersList.value.filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
                                                            .toString())
                                                    }&body=${Uri.encode(body)}&params=$encodedParams" +
                                                    "&requestTime=${result.requestTime}" + "&requestMethod=${selectedMethod}" + "&requestSchema=${selectedSchema}")
                                    }

                                    is NetworkResult.Error -> {
                                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT)
                                            .show()
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





