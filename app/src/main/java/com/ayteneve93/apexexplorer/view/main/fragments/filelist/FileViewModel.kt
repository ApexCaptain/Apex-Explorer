package com.ayteneve93.apexexplorer.view.main.fragments.filelist

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.data.FileModel
import com.ayteneve93.apexexplorer.view.base.BaseViewModel

class FileViewModel(
    application : Application
) : BaseViewModel(application){
    lateinit var mFileModel : FileModel
    lateinit var mSubTitle : String
    lateinit var onFavoriteButtonClickListener : () -> Unit
    fun onFavoriteButtonClick() { onFavoriteButtonClickListener() }
    lateinit var onItemClickListener : () -> Unit
    fun onItemClick() { onItemClickListener() }
    lateinit var onItemLongClickListener : (view : View) -> Boolean
    fun onItemLongClick(view : View) : Boolean { return onItemLongClickListener(view) }
}

