package com.lihb.network

import android.app.Application

object NetworkPlugins {
    var app: Application? = null

    fun init(application: Application): NetworkPlugins {
        app = application
        return this
    }


}