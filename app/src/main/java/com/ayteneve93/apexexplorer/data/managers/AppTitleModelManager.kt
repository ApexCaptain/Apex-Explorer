package com.ayteneve93.apexexplorer.data.managers

import android.app.Application
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.data.AppTitleModel

class AppTitleModelManager(private val application : Application){
    fun getAppTitleModel() : List<AppTitleModel> {
        val appTitle = application.getString(R.string.app_name)
        val appTitleModelList : ArrayList<AppTitleModel> = ArrayList()
        appTitle.forEach {
            appTitleModelList.add(AppTitleModel(it.toString()))
        }
        return appTitleModelList
    }
}