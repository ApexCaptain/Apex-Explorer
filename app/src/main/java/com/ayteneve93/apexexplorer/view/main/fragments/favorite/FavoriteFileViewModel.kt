package com.ayteneve93.apexexplorer.view.main.fragments.favorite

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class FavoriteFileViewModel(
    application : Application
) : BaseViewModel(application) {
    lateinit var mFileModel : FileModel
    var mThumbnail = ObservableField(Uri.parse(""))
    lateinit var mExtension : String
    lateinit var mPath : String
    var mSizeAndUnit = ObservableField("")
    lateinit var onItemClickListener : () -> Unit
    fun onItemClick() {onItemClickListener() }
    lateinit var onDeleteButtonClickListener : () -> Unit
    fun onDeleteButtonClick() { onDeleteButtonClickListener() }
}
