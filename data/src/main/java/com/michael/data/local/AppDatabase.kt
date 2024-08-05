package com.michael.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.michael.data.dao.HttpResponseDao

class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(HttpResponseDao.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${HttpResponseDao.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "api_tester.db"
        private const val DATABASE_VERSION = 1

        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: AppDatabase(context).also { instance = it }
            }
    }

    fun httpResponseDao(): HttpResponseDao {
        return HttpResponseDao(this)
    }
}