package com.example.apitester.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.apitester.R
import com.example.domain.FilterOption
import com.example.domain.SortOption
import com.example.domain.model.HttpResponse
import com.example.domain.model.getFullRequestUrl


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel(factory = ViewModelFactory())
) {
    val historyItems by viewModel.historyItems.observeAsState(emptyList())
    val isMenuExpanded by viewModel.moreMenuExpanded.observeAsState(false)
    val showSortDialog by viewModel.showSortDialog.observeAsState(false)
    val showFilterTypeDialog by viewModel.showFilterTypeDialog.observeAsState(false)
    val showFilterStatusDialog by viewModel.showFilterStatusDialog.observeAsState(false)
    val selectedSortOption by viewModel.selectedSortOption.observeAsState(null)
    val selectedRequestType by viewModel.selectedRequestType.observeAsState(null)
    val selectedResponseStatus by viewModel.selectedResponseStatus.observeAsState(null)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            tint = Color.White,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setMoreExpanded(true) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { viewModel.setMoreExpanded(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by execution time") },
                            onClick = {
                                viewModel.showSortDialog(true)
                                viewModel.setMoreExpanded(false)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter by request type") },
                            onClick = {
                                viewModel.showFilterTypeDialog(true)
                                viewModel.setMoreExpanded(false)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter by response status") },
                            onClick = {
                                viewModel.showFilterStatusDialog(true) // Correctly show the filter status dialog
                                viewModel.setMoreExpanded(false)
                            }
                        )
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
            LazyColumn {
                items(historyItems) { item ->
                    HistoryItemView(item = item)

                    if (historyItems.last() != item) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    // Sort Dialog
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.showSortDialog(false)
            },
            title = { Text("Sort by Execution Time") },
            text = {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedSortOption is SortOption.Ascending,
                            onClick = {
                                viewModel.selectSortOption(SortOption.Ascending)
                            }
                        )
                        Text("Sort Ascending")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedSortOption is SortOption.Descending,
                            onClick = {
                                viewModel.selectSortOption(SortOption.Descending)
                            }
                        )
                        Text("Sort Descending")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Handle the selected sort option
                        if (selectedSortOption != null) {
                            viewModel.applySortingAndFiltering()
                        }
                        viewModel.showSortDialog(false)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.showSortDialog(false)
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Filter Request Type Dialog
    if (showFilterTypeDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showFilterTypeDialog(false)},
            title = { Text("Filter by Request Type") },
            text = {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRequestType is FilterOption.Get,
                            onClick = {
                                viewModel.selectRequestType(FilterOption.Get)
                            }
                        )
                        Text("GET")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRequestType is FilterOption.Post,
                            onClick = {
                                viewModel.selectRequestType(FilterOption.Post)
                            }
                        )
                        Text("POST")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRequestType is FilterOption.Put,
                            onClick = {
                                viewModel.selectRequestType(FilterOption.Put)
                            }
                        )
                        Text("PUT")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRequestType is FilterOption.Delete,
                            onClick = {
                                viewModel.selectRequestType(FilterOption.Delete)
                            }
                        )
                        Text("DELETE")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRequestType is FilterOption.Patch,
                            onClick = {
                                viewModel.selectRequestType(FilterOption.Patch)
                            }
                        )
                        Text("PATCH")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Handle the selected request type
                        if (selectedRequestType != null) {
                            viewModel.applySortingAndFiltering()
                        }
                        viewModel.showFilterTypeDialog(false)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showFilterTypeDialog(false)}) {
                    Text("Cancel")
                }
            }
        )
    }

    // Filter Response Status Dialog
    if (showFilterStatusDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showFilterTypeDialog(false) },
            title = { Text("Filter by Response Status") },
            text = {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedResponseStatus is FilterOption.Success,
                            onClick = {
                                viewModel.selectResponseStatus(FilterOption.Success)
                            }
                        )
                        Text("Success")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedResponseStatus is FilterOption.Failure,
                            onClick = {
                                viewModel.selectResponseStatus(FilterOption.Failure)
                            }
                        )
                        Text("Failure")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Handle the selected response status
                        if (selectedResponseStatus != null) {
                            viewModel.applySortingAndFiltering()
                        }
                        viewModel.showFilterStatusDialog(false)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {  viewModel.showFilterStatusDialog(false)}) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryItemView(item: HttpResponse) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .weight(3f)
        ) {
            Text(
                text = item.getFullRequestUrl(),
                maxLines = Int.MAX_VALUE, // Allows the text to wrap as needed
                overflow = TextOverflow.Visible // Makes sure the text wraps to the next line
            )
            Text(text = "Response Code: ${item.responseCode}")
        }
        Spacer(modifier = Modifier.width(32.dp))
        Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.End) {
            Text(text = item.requestMethod, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${item.requestTime}  millis", textAlign = TextAlign.End)
        }
    }
}
