package com.example.apitester

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.apitester.ui.MainScreen
import com.example.apitester.ui.ViewModelFactory
import com.example.apitester.ui.home.HomeViewModel
import com.example.apitester.ui.theme.ApiTesterTheme


class MainActivity : ComponentActivity() {

    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory())[HomeViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            ApiTesterTheme {
                MainScreen(this, homeViewModel = viewModel)
            }
        }

        pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.updateFile(pendingFileIndex, uri.toString(), pendingFileMimeType)
                    viewModel.updateFileNameAtIndex(uri.path.toString(),pendingFileIndex)
                    viewModel.updateFileUriAtIndex(uri,0)
                }
            }
        }
    }

    companion object {
        var pendingFileIndex = -1
        var pendingFileMimeType = ""
    }

    fun pickFile(index: Int, mimeType: String) {
        pendingFileIndex = index
        pendingFileMimeType = mimeType
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        pickFileLauncher.launch(intent)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApiTesterTheme {
    }
}
