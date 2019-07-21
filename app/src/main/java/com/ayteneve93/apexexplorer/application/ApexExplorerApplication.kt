package com.ayteneve93.apexexplorer.application

import android.app.Application
import com.ayteneve93.apexexplorer.di.apexExplorerApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApexExplorerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initUtils()
        setDependencyInjection()

    }

    private fun initUtils() {

    }

    private fun setDependencyInjection() {
        startKoin {
            // DI - Android Context, Application 레벨에서 지정
            androidContext(this@ApexExplorerApplication)
            // Di - Modules
            modules(apexExplorerApplicationModule)
        }
    }

}