package com.michael.data.remote

import com.michael.data.HttpResult
import java.io.File

interface ApiService {
    fun makeRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String? = null,
        files: Map<String, File?>? = null,
        onResponse: (HttpResult) -> Unit
    )
}