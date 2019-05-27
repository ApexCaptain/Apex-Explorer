package com.gmail.ayteneve93.apex.explorer

import android.app.Application
import com.gmail.ayteneve93.apex.explorer.utils.ApplicationPreference

class ApexExplorer : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationPreference.initialize(this)
    }
}