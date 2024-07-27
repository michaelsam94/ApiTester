package com.example.apitester.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.apitester.R
import com.example.apitester.utils.convertMillisToSecondsString
import com.example.apitester.utils.formatJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseScreen(
    navController: NavHostController,
    response: String,
    requestUrl: String,
    responseCode: String,
    error: String?,
    headers: String,
    body: String,
    params: String,
    requestTime: Int,
    responseViewModel: ResponseViewModel = viewModel(factory = ViewModelFactory())
) {


    LaunchedEffect(key1 = requestUrl) {
        // Set the values in the ViewModel
        responseViewModel.setResponse(response)
        responseViewModel.setRequestUrl(requestUrl)
        responseViewModel.setResponseCode(responseCode)
        responseViewModel.setError(error)
        responseViewModel.setHeaders(headers)
        responseViewModel.setBody(body)
        responseViewModel.setParams(params)
        responseViewModel.setRequestTime(requestTime)

        responseViewModel.saveRequest()
    }


    var showBottomSheet by remember { mutableStateOf(false) }

    // Get values from the ViewModel
    val responseText by responseViewModel.response.observeAsState()
    val requestUrlText by responseViewModel.requestUrl.observeAsState()
    val responseCodeText by responseViewModel.responseCode.observeAsState()
    val errorText by responseViewModel.error.observeAsState()
    val headersText by responseViewModel.headers.observeAsState()
    val bodyText by responseViewModel.body.observeAsState()
    val paramsText by responseViewModel.params.observeAsState()
    val requestTimeState by responseViewModel.requestTime.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Result") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_back),tint = Color.White, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(painter = painterResource(id = R.drawable.ic_info),tint = Color.White, contentDescription = "Info")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xffF59F3F),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .horizontalScroll(state = rememberScrollState())) {
                Text(text = formatJson(responseText ?: ""))
            }

            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Request URL: ${requestUrlText ?: ""}")
                        Text(text = "Response Code: ${responseCodeText ?: ""}")
                        Text(text = "Error: ${errorText ?: "None"}")
                        Text(text = "Request Headers: ${headersText ?: ""}")
                        Text(text = "Request Body: ${bodyText ?: ""}")
                        Text(text = "Query Parameters: ${paramsText ?: ""}")
                        Text(text = "Request Time: $requestTimeState millis")
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
