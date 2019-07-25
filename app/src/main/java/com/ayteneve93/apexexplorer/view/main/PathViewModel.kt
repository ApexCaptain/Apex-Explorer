package com.ayteneve93.apexexplorer.view.main

import android.app.Application
import androidx.databinding.ObservableField
import com.ayteneve93.apexexplorer.view.base.BaseViewModel
import java.util.*

class PathViewModel(
    application : Application
) : BaseViewModel(application) {
    lateinit var mDirectoryPath : String
    lateinit var mDirectoryTitle : String
    var mIsSelected = false
    lateinit var onItemClickListener : () -> Unit
    fun onItemClick() { onItemClickListener() }
    var mNextIndicatorString = " >"
}