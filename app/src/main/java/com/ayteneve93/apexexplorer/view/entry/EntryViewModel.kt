package com.ayteneve93.apexexplorer.view.entry

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.data.AppTitleModel
import com.ayteneve93.apexexplorer.data.DataModelManager
import com.ayteneve93.apexexplorer.prompt.FireBaseAuthPrompt.FireBaseAuthManager
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class EntryViewModel(
    application: Application,
    private val dataModelManager: DataModelManager
) : BaseViewModel<Any>(application) {

    val appTitleObservableList = ObservableArrayList<AppTitleModel>()

    init {
        appTitleObservableList.addAll(dataModelManager.getAppTitleModel(getApplication()))
    }

}