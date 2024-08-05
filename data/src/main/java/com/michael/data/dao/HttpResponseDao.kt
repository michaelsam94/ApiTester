package com.michael.data.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.michael.data.local.AppDatabase
import com.michael.data.local.entity.HttpResponseEntity
import java.util.concurrent.Executors

class HttpResponseDao(private val dbHelper: AppDatabase) {

    fun insert(httpResponse: HttpResponseEntity) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REQUEST_URL, httpResponse.requestUrl)
            put(COLUMN_RESPONSE_CODE, httpResponse.responseCode)
            put(COLUMN_REQUEST_METHOD, httpResponse.requestMethod)
            put(COLUMN_REQUEST_SCHEMA, httpResponse.requestSchema)
            put(COLUMN_ERROR, httpResponse.error)
            put(COLUMN_HEADERS, httpResponse.headers)
            put(COLUMN_BODY, httpResponse.body)
            put(COLUMN_PARAMS, httpResponse.params)
            put(COLUMN_REQUEST_TIME, httpResponse.requestTime)
        }
        Executors.newSingleThreadExecutor().execute {
            db.insert(TABLE_NAME, null, values)
        }
    }

    fun getAll(onResponse: (List<HttpResponseEntity>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {
            val db: SQLiteDatabase = dbHelper.readableDatabase
            val cursor: Cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null
            )
            val responses = mutableListOf<HttpResponseEntity>()
            with(cursor) {
                while (moveToNext()) {
                    val requestUrl = getString(getColumnIndexOrThrow(COLUMN_REQUEST_URL))
                    val responseCode = getInt(getColumnIndexOrThrow(COLUMN_RESPONSE_CODE))
                    val error = getString(getColumnIndexOrThrow(COLUMN_ERROR))
                    val headers = getString(getColumnIndexOrThrow(COLUMN_HEADERS))
                    val body = getString(getColumnIndexOrThrow(COLUMN_BODY))
                    val params = getString(getColumnIndexOrThrow(COLUMN_PARAMS))
                    val requestTime = getInt(getColumnIndexOrThrow(COLUMN_REQUEST_TIME))
                    val requestMethod = getString(getColumnIndexOrThrow(COLUMN_REQUEST_METHOD))
                    val requestSchema = getString(getColumnIndexOrThrow(COLUMN_REQUEST_SCHEMA))
                    responses.add(HttpResponseEntity(requestUrl,requestMethod,requestSchema, responseCode, error, headers, body, params,requestTime))
                }
            }
            onResponse.invoke(responses)
            cursor.close()
        }

    }

    companion object {
        const val TABLE_NAME = "http_responses"
        const val COLUMN_REQUEST_URL = "request_url"
        const val COLUMN_REQUEST_METHOD = "request_method"
        const val COLUMN_REQUEST_SCHEMA = "request_schema"
        const val COLUMN_RESPONSE_CODE = "response_code"
        const val COLUMN_ERROR = "error"
        const val COLUMN_HEADERS = "headers"
        const val COLUMN_BODY = "body"
        const val COLUMN_PARAMS = "params"
        const val COLUMN_REQUEST_TIME = "request_time"

        const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_REQUEST_URL TEXT NOT NULL,
                $COLUMN_RESPONSE_CODE INTEGER NOT NULL,
                $COLUMN_ERROR TEXT,
                $COLUMN_HEADERS TEXT,
                $COLUMN_BODY TEXT,
                $COLUMN_REQUEST_METHOD TEXT NOT NULL,
                $COLUMN_REQUEST_SCHEMA TEXT NOT NULL,
                $COLUMN_PARAMS TEXT,
                $COLUMN_REQUEST_TIME INTEGER 
            )
        """
    }
}