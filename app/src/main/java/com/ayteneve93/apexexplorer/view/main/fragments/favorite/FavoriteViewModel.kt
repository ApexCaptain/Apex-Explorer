package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.ayteneve93.apexexplorer.R
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class FavoriteViewModel(
    application : Application
) : BaseViewModel(application){
    var mShouldRecyclerViewInvisible = ObservableField(false)
    var mIsEmptyDirectory = ObservableField(false)
    var mNoContentString = MutableLiveData<CharSequence>().apply {
        value = application.getString(R.string.directory_no_content)
    }
}