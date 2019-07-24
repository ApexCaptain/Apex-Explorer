package com.ayteneve93.apexexplorer.view.main

import android.app.Application
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class MainViewModel(
    application : Application
) : BaseViewModel(application) {
    val mIsViewChanging = ObservableField(false)
}