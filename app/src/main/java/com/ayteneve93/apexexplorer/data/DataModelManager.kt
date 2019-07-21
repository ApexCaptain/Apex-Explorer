package com.ayteneve93.apexexplorer.data

import android.content.Context
import com.ayteneve93.apexexplorer.R

class DataModelManager{

    fun getAppTitleModel(context : Context) : List<AppTitleModel> {
        val appTitle = context.getString(R.string.app_name)
        val appTitleModelList : ArrayList<AppTitleModel> = ArrayList()
        appTitle.forEach {
            appTitleModelList.add(AppTitleModel(it.toString()))
        }
        return appTitleModelList
    }

}