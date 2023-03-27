package com.example.williamhybrid

import android.app.Application
import com.igaworks.v2.core.AdBrixRm

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        sdkInit()
    }
    private fun sdkInit(){
        AdBrixRm.init(this, "{앱키}", "{시크릿키}")
    }
}