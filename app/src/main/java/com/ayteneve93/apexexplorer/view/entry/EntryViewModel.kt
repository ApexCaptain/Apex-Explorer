package com.ayteneve93.apexexplorer.view.entry

import android.app.Application
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class EntryViewModel(
    application: Application
) : BaseViewModel(application) {
    val mIsAppLoading = ObservableField(false)
}