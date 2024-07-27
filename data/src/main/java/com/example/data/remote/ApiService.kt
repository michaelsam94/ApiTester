package com.example.data.remote

import com.example.data.HttpResult

interface ApiService {
    fun makeRequest(
        method: String,
        url: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        body: String? = null,
        onResponse: (HttpResult) -> Unit
    )
}