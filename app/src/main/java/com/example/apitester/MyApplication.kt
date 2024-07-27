package com.example.apitester

import android.app.Application
import com.example.apitester.di.ServiceLocator

class MyApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initDB(this)
    }
}