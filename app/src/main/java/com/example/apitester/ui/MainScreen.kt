package com.example.apitester.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apitester.MainActivity
import com.example.apitester.ui.home.HomeScreen
import com.example.apitester.ui.home.HomeViewModel

@Composable
fun MainScreen(activity: MainActivity? = null,homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, activity = activity, viewModel = homeViewModel) }
        composable("response?response={response}&requestUrl={requestUrl}&responseCode={responseCode}" +
                "&error={error}&headers={headers}&body={body}&params={params}" +
                "&requestTime={requestTime}&requestMethod={requestMethod}&requestSchema={requestSchema}") { backStackEntry ->
            val response = backStackEntry.arguments?.getString("response") ?: ""
            val requestUrl = backStackEntry.arguments?.getString("requestUrl") ?: ""
            val responseCode = backStackEntry.arguments?.getString("responseCode") ?: "0"
            val error = backStackEntry.arguments?.getString("error") ?: "No Error"
            val headers = backStackEntry.arguments?.getString("headers") ?: ""
            val body = backStackEntry.arguments?.getString("body") ?: ""
            val params = backStackEntry.arguments?.getString("params") ?: ""
            val requestTimeString = backStackEntry.arguments?.getString("requestTime") ?: "0"
            val requestTime = requestTimeString.toInt()
            val requestMethod = backStackEntry.arguments?.getString("requestMethod") ?: "GET"
            val requestSchema = backStackEntry.arguments?.getString("requestSchema") ?: "https://"

            ResponseScreen(
                navController = navController,
                response = response,
                requestUrl = requestUrl,
                responseCode = responseCode,
                error = error,
                headers = headers,
                body = body,
                params = params,
                requestTime = requestTime,
                requestMethod = requestMethod,
                requestSchema = requestSchema,
            )
        }
        composable("history") { HistoryScreen(navController) }
    }
}