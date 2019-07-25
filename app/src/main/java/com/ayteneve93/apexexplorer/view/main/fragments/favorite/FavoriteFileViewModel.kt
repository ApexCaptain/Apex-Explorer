package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.app.Application
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class FavoriteFileViewModel(
    application : Application
) : BaseViewModel(application) {
    lateinit var mFileModel : FileModel
}
