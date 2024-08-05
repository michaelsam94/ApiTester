package com.michael.domain.model

data class HttpResponse(
    val requestMethod: String,
    val requestUrl: String,
    val requestSchema: String,
    val responseCode: Int,
    val error: String?,
    val headers: Map<String, String>,
    val body: String,
    val params: Map<String, String>,
    val requestTime: Int,
)


fun HttpResponse.isSuccessful(): Boolean {
    return responseCode in 200..299
}


fun HttpResponse.getFullRequestUrl(): String {
    val queryParams = params.entries.joinToString{ "${it.key}=${it.value}" }
    return if (queryParams.isNotEmpty()) {
        "$requestSchema$requestUrl?$queryParams"
    } else {
        "$requestSchema$requestUrl"
    }
}
