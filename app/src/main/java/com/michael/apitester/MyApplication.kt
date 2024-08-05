package com.michael.apitester

import android.app.Application
import com.michael.apitester.di.ServiceLocator

class MyApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initDB(this)
    }
}