package com.example.apitester.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("response?response={response}&requestUrl={requestUrl}&responseCode={responseCode}&error={error}&headers={headers}&body={body}&params={params}&requestTime={requestTime}") { backStackEntry ->
            val response = backStackEntry.arguments?.getString("response") ?: ""
            val requestUrl = backStackEntry.arguments?.getString("requestUrl") ?: ""
            val responseCode = backStackEntry.arguments?.getString("responseCode") ?: "0"
            val error = backStackEntry.arguments?.getString("error")
            val headers = backStackEntry.arguments?.getString("headers") ?: ""
            val body = backStackEntry.arguments?.getString("body") ?: ""
            val params = backStackEntry.arguments?.getString("params") ?: ""
            val requestTimeString = backStackEntry.arguments?.getString("requestTime") ?: "0"
            val requestTime = requestTimeString.toInt()

            ResponseScreen(
                navController = navController,
                response = response,
                requestUrl = requestUrl,
                responseCode = responseCode,
                error = error,
                headers = headers,
                body = body,
                params = params,
                requestTime = requestTime
            )
        }
        composable("history") { HistoryScreen(navController) }
    }
}