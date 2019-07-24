package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.app.Application
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class FileListViewModel(
    application : Application
) : BaseViewModel(application){
    var mShouldRecyclerViewInvisible = ObservableField(false)
    var mIsEmptyDirectory = ObservableField(false)
}