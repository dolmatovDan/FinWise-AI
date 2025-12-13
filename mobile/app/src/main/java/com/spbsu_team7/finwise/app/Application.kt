package com.spbsu_team7.finwise.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class FinWiseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}